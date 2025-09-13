package MyApp.BE.service.User;

import MyApp.BE.dto.RegistrationDTO;
import org.springframework.http.ResponseEntity;

public interface IUserService {

    ResponseEntity<?> registerUser(RegistrationDTO registrationDTO);
}
