package MyApp.dto;


import MyApp.enums.GenderType;
import MyApp.enums.Regions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivateUserDTO {

    private String nickName;
    private GenderType gender;
    private int age;
    private String email;
    private String password;
    private Set<Regions> regions;
}
