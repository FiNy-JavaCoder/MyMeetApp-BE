package MyApp.service.User;

import MyApp.dto.PrivateUserDTO;
import MyApp.dto.UserDTO;
import MyApp.entity.UserEntity;
import MyApp.enums.GenderType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserService {


    UserDTO getPerson(Long personId);

    List<UserDTO> findByGender(GenderType genderType);

    ResponseEntity<?> registerUser(PrivateUserDTO privateUserDTO);
}
