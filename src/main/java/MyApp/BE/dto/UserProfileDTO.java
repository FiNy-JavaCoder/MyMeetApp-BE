package MyApp.BE.dto;

import MyApp.BE.enums.*;
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
    protected int heightCm;
    protected int weightKg;
    protected GenderType gender;
    protected SearchSexualOrientation sexualOrientation;
    protected SearchTypeRelationShip typeRelationShip;
    protected int birthYear;
    protected int birthMonth;
    protected LocalDate birthDate;
    protected int age;
    protected Set<Regions> regions;
    protected Set<Districts> districts;
    protected String profilePictureUrl;
    protected String aboutMe;
}
