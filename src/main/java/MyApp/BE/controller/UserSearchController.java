package MyApp.BE.controller;

import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.dto.ErrorDTO;
import MyApp.BE.service.UserProfile.UserProfileService;
import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.SearchSexualOrientation;
import MyApp.BE.enums.SearchTypeRelationShip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
@RequiredArgsConstructor
@Slf4j
public class UserSearchController {

    private final UserProfileService userProfileService;

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam(required = false) String nickName,
            @RequestParam(required = false) GenderType gender,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) SearchSexualOrientation sexualOrientation,
            @RequestParam(required = false) SearchTypeRelationShip lookingFor,
            @RequestParam(required = false) List<String> regions,
            @RequestParam(required = false) List<String> districts
    ) {
        try {
            log.info("Searching users with criteria: nickName={}, gender={}, age={}-{}", 
                    nickName, gender, minAge, maxAge);
            
            List<UserProfileDTO> results = userProfileService.searchUsers(
                nickName, gender, minAge, maxAge, sexualOrientation, 
                lookingFor, regions, districts
            );
            
            log.info("Found {} users matching search criteria", results.size());
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid search parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        } catch (Exception e) {
            log.error("Error searching users", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorDTO("Nepodařilo se vyhledat uživatele"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProfiles() {
        try {
            List<UserProfileDTO> profiles = userProfileService.getAllProfiles();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            log.error("Error getting all profiles", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorDTO("Nepodařilo se načíst profily"));
        }
    }

    @GetMapping("/by-nickname")
    public ResponseEntity<?> findByNickname(@RequestParam String nickName) {
        try {
            if (nickName == null || nickName.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorDTO("Přezdívka nemůže být prázdná"));
            }
            
            List<UserProfileDTO> profiles = userProfileService.findProfilesByNickName(nickName);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            log.error("Error finding profiles by nickname: {}", nickName, e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorDTO("Nepodařilo se vyhledat profily"));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getProfileStats() {
        try {
            long totalCount = userProfileService.getTotalProfileCount();
            long maleCount = userProfileService.getProfileCountByGender(GenderType.male);
            long femaleCount = userProfileService.getProfileCountByGender(GenderType.female);
            
            return ResponseEntity.ok(new ProfileStatsResponse(totalCount, maleCount, femaleCount));
        } catch (Exception e) {
            log.error("Error getting profile stats", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorDTO("Nepodařilo se načíst statistiky"));
        }
    }

    @GetMapping("/exists/{userId}")
    public ResponseEntity<?> checkProfileExists(@PathVariable Long userId) {
        try {
            boolean exists = userProfileService.profileExists(userId);
            return ResponseEntity.ok(new ProfileExistsResponse(exists));
        } catch (Exception e) {
            log.error("Error checking if profile exists for user {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorDTO("Nepodařilo se ověřit existenci profilu"));
        }
    }

    // Response DTOs
    public record ProfileStatsResponse(long total, long male, long female) {}
    public record ProfileExistsResponse(boolean exists) {}
}