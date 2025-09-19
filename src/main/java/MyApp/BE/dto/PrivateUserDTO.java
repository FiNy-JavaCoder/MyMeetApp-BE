package MyApp.BE.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivateUserDTO {

    private String nickName;
    @Email
    private String email;
    private String passwordHash;

}
