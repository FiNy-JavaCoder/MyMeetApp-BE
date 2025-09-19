package MyApp.BE.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserProfilePrivateDTO extends UserProfileDTO {
    private String favoriteFilter;
}