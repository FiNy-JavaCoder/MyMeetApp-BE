package MyApp.BE.dto;


import MyApp.BE.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

    protected Long userId;
    protected String nickName;
    protected int heightCm;
    protected int weightKg;
    protected GenderType gender;
    protected SearchSexualOrientation sexualOrientation;
    protected int birthYear;
    protected int birthMonth;
    protected int age;
    protected Set<Regions> regions;
    protected Set<Districts> districts;
    protected String profilePictureUrl;
    protected String aboutMe;
}
