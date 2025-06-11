package MyApp.service.User;

import MyApp.dto.ErrorDTO;
import MyApp.dto.PrivateUserDTO;
import MyApp.dto.UserDTO;
import MyApp.dto.mapper.UserMapper;
import MyApp.entity.UserEntity;
import MyApp.entity.repository.IUserRepository;
import MyApp.enums.GenderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(IUserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public ResponseEntity<?> registerUser(PrivateUserDTO privateUserDTO) {
        if (userRepository.existsByNickName(privateUserDTO.getNickName()))  {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorDTO("Nickname is already taken"));
        }
        userRepository.save(userMapper.toEntity(privateUserDTO));
        return ResponseEntity.ok("User saved");
    }

    public UserDTO getPerson(Long personId) {
        return userMapper.toDTO(userRepository.getReferenceById(personId));
    }

    public List<UserDTO> findByGender(GenderType genderType) {
        return userMapper.toDTOs(userRepository.findByGender(genderType));

    }
}