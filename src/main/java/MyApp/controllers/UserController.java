package MyApp.controllers;

import MyApp.dto.UserDTO;
import MyApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/user-rg")
    public void registerUser(@RequestBody UserDTO userDto) {
        userService.registerUser(userDto);

    }

    @GetMapping("/person-display")
    public UserDTO getperson(@PathVariable Long personId) {
        return userService.getPerson(personId);
    }



}
