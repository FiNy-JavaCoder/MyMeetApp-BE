package MyApp.BE.service.User;

import MyApp.BE.dto.ErrorDTO;
import MyApp.BE.dto.PrivateUserDTO;
import MyApp.BE.dto.UserDTO;
import MyApp.BE.dto.mapper.UserMapper;
import MyApp.BE.entity.repository.IUserRepository;
import MyApp.BE.enums.GenderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public UserDTO getPerson(Long personId) {
        return userMapper.toDTO(userRepository.getReferenceById(personId));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findByGender(GenderType genderType) {
        return userMapper.toDTOs(userRepository.findByGender(genderType));

    }
}