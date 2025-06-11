package MyApp.controllers;

import MyApp.dto.PrivateUserDTO;
import MyApp.dto.UserDTO;
import MyApp.enums.GenderType;
import MyApp.service.User.UserService;
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
    public void registerUser(@RequestBody PrivateUserDTO privateUserDTO) {
        userService.registerUser(privateUserDTO);

    }

    @GetMapping("/person-display/{personId}")
    public UserDTO getPerson(@PathVariable Long personId) {
        return userService.getPerson(personId);
    }

    @GetMapping("/all-females")
    public ResponseEntity<List<UserDTO>> getFemales() {
        List<UserDTO> femaleUsers = userService.findByGender(GenderType.female);
        return ResponseEntity.ok(femaleUsers);
    }

    @GetMapping("/all-males")
    public ResponseEntity<List<UserDTO>> getMales() {
        List<UserDTO> maleUsers = userService.findByGender(GenderType.male);
        return ResponseEntity.ok(maleUsers);

    }
}
