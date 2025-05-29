package MyApp.controllers;

import MyApp.dto.UserDTO;
import MyApp.entity.UserEntity;
import MyApp.enums.GenderType;
import MyApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/user-rg")
    public void registerUser(@RequestBody UserDTO userDTO) {
        userService.registerUser(userDTO);

    }

    @GetMapping("/person-display")
    public UserDTO getPerson(@PathVariable Long personId) {
        return userService.getPerson(personId);
    }

    @GetMapping("/all-females")
    public List<UserEntity> getFemales() {
        return userService.findByGender(GenderType.female);
    }
    @GetMapping("/all-males")
    public List<UserEntity> getMales() {
        return userService.findByGender(GenderType.male);
    }



}
