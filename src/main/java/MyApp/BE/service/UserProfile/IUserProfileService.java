package MyApp.BE.service.UserProfile;

import MyApp.BE.dto.RegistrationDTO;
import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.dto.UserProfilePrivateDTO;
import MyApp.BE.enums.GenderType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserProfileService {


    ResponseEntity<UserProfileDTO> getUserPublicProfile(Long personId);

    ResponseEntity<UserProfilePrivateDTO> getUserPrivateProfile(Long personId);

    List<UserProfileDTO> findByGender(GenderType genderType);

}
