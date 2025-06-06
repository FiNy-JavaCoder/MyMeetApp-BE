package MyApp.service.User;

import MyApp.dto.UserDTO;
import MyApp.entity.UserEntity;
import MyApp.enums.GenderType;

import java.util.List;

public interface IUserService {


    UserDTO getPerson(Long personId);

    List<UserEntity> findByGender(GenderType genderType);

    void registerUser(UserDTO userDTO);
}
