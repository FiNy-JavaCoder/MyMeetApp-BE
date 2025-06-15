package MyApp.BE.service.User;

import MyApp.BE.dto.RegistrationDTO;
import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.enums.GenderType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserService {


    ResponseEntity<?> getPerson(Long personId);

    List<UserProfileDTO> findByGender(GenderType genderType);

    ResponseEntity<?> registerUser(RegistrationDTO registrationDTO);
}
