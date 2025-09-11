package MyApp.BE.dto;


import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.Regions;
import MyApp.BE.enums.SearchSexualOrientation;
import MyApp.BE.enums.SearchTypeRelationShip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

    protected Long userId;
    protected String nickName;
    protected GenderType gender;
    protected SearchSexualOrientation SexualOrientation;
    protected SearchTypeRelationShip searchTypeRelationShip;
    protected LocalDate birthDate;
    protected int age;
    protected Set<Regions> regions;
    protected String profilePictureUrl;
    protected String aboutMe;
}
