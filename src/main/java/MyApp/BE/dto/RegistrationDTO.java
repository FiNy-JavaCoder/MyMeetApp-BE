package MyApp.BE.dto;


import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.Regions;
import MyApp.BE.enums.SearchSexualOrientation;
import MyApp.BE.enums.SearchTypeRelationShip;
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
    private int birthMonth;
    private int birthYear;
    private String password;
    private Set<Regions> regions;
    private GenderType gender;
    private SearchTypeRelationShip searchTypeRelationShip;
    private SearchSexualOrientation sexualOrientation;
}
