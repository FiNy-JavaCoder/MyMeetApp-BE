// Nový Search Controller pro filtrování uživatelů
// src/main/java/MyApp/BE/controller/UserSearchController.java
package MyApp.BE.controller;

import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.service.UserProfile.UserProfileService;
import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.SearchSexualOrientation;
import MyApp.BE.enums.SearchTypeRelationShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class UserSearchController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/search")
    public ResponseEntity<List<UserProfileDTO>> searchUsers(
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
            List<UserProfileDTO> results = userProfileService.searchUsers(
                nickName, gender, minAge, maxAge, sexualOrientation, 
                lookingFor, regions, districts
            );
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}