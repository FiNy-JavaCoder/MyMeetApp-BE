package MyApp.BE.dto;


import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.Regions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

    private Long userId;
    private String nickName;
    private GenderType gender;
    private LocalDate birthDate;
    private int age;
    private Set<Regions> regions;
    private String profilePictureUrl;
}
