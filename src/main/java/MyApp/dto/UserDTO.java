package MyApp.dto;


import MyApp.enums.GenderType;
import MyApp.enums.Regions;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String nickName;
    private GenderType gender;
    private int age;
    private String email;
    private String password;
    private Set<Regions> regions;
}
