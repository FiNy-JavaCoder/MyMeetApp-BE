package MyApp.BE.dto;

import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.Regions;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivateUserDTO {

    private String nickName;
    @Email
    private String email;
    private String passwordHash;

}
