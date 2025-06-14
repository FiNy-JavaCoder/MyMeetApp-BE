package MyApp.BE.service.User;

import MyApp.BE.dto.PrivateUserDTO;
import MyApp.BE.dto.UserDTO;
import MyApp.BE.enums.GenderType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserService {


    ResponseEntity<?> getPerson(Long personId);

    List<UserDTO> findByGender(GenderType genderType);

    ResponseEntity<?> registerUser(PrivateUserDTO privateUserDTO);
}
