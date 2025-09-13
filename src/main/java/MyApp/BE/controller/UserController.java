package MyApp.BE.controller;

import MyApp.BE.dto.RegistrationDTO;
import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.enums.GenderType;
import MyApp.BE.service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/user-rg")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationDTO registrationDTO) {
       return userService.registerUser(registrationDTO);

    }
}
