package MyApp.BE.controller;

import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.enums.GenderType;
import MyApp.BE.service.UserProfile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;


    @GetMapping("/person-display/{personId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long personId) {
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

}
