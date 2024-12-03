package MyApp.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String nickName;
    private Integer age;
    private String email;
    private String password;
    private List<String> regions;
}
