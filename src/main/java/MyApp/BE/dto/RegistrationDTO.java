package MyApp.BE.dto;


import MyApp.BE.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {

    private String nickName;
    private String email;
    private String passwordHash;
}
