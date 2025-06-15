package MyApp.BE.controller;

import MyApp.BE.dto.PrivateUserDTO;
import MyApp.BE.dto.RegistrationDTO;
import MyApp.BE.dto.UserDTO;
import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.enums.GenderType;
import MyApp.BE.service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/user-rg")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationDTO registrationDTO) {
       return userService.registerUser(registrationDTO);

    }

    @GetMapping("/person-display/{personId}")
    public ResponseEntity<?> getPerson(@PathVariable Long personId) {
        return userService.getPerson(personId);
    }

    @GetMapping("/all-females")
    public ResponseEntity<List<UserProfileDTO>> getFemales() {
        List<UserProfileDTO> femaleUsers = userService.findByGender(GenderType.female);
        return ResponseEntity.ok(femaleUsers);
    }

    @GetMapping("/all-males")
    public ResponseEntity<List<UserProfileDTO>> getMales() {
        List<UserProfileDTO> maleUsers = userService.findByGender(GenderType.male);
        return ResponseEntity.ok(maleUsers);

    }
}
