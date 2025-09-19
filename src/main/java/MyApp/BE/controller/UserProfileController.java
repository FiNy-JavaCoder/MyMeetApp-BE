// 1. Přidejte do UserProfileController.java rozšířené endpointy:

package MyApp.BE.controller;

import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.enums.GenderType;
import MyApp.BE.service.UserProfile.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173" })
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/person-display/{personId}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long personId) {
        return userProfileService.getUserPublicProfile(personId);
    }

    @GetMapping("/all-females")
    public ResponseEntity<List<UserProfileDTO>> getFemaleProfiles() {
        List<UserProfileDTO> femaleUsers = userProfileService.findProfilesByGender(GenderType.female);
        return ResponseEntity.ok(femaleUsers);
    }

    @GetMapping("/all-males")
    public ResponseEntity<List<UserProfileDTO>> getMaleProfiles() {
        List<UserProfileDTO> maleUsers = userProfileService.findProfilesByGender(GenderType.male);
        return ResponseEntity.ok(maleUsers);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<UserProfileDTO>> getAllProfiles() {
        List<UserProfileDTO> allUsers = userProfileService.getAllProfiles();
        return ResponseEntity.ok(allUsers);
    }
}