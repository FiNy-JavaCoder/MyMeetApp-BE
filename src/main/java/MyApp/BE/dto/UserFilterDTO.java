package MyApp.BE.dto;

import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.SearchSexualOrientation;
import MyApp.BE.enums.SearchTypeRelationShip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterDTO {
    private Long userId;
    private String nickName;
    private GenderType gender;
    private SearchSexualOrientation sexualOrientation;
    private SearchTypeRelationShip searchTypeRelationShip;
    private int age;
}
