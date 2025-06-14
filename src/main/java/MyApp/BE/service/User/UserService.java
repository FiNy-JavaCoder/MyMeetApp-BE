package MyApp.BE.service.User;

import MyApp.BE.dto.ErrorDTO;
import MyApp.BE.dto.PrivateUserDTO;
import MyApp.BE.dto.UserDTO;
import MyApp.BE.dto.mapper.UserMapper;
import MyApp.BE.entity.UserEntity;
import MyApp.BE.entity.repository.IUserRepository;
import MyApp.BE.enums.GenderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

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
        if (userRepository.existsByNickName(privateUserDTO.getNickName())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorDTO("Nickname is already taken"));
        }
        PrivateUserDTO newUser = new PrivateUserDTO();
        newUser.setNickName(privateUserDTO.getNickName());
        newUser.setEmail(privateUserDTO.getEmail());
        newUser.setBirthDate(LocalDate.of(privateUserDTO.getBirthYear(),privateUserDTO.getBirthMonth(), 15));
        newUser.setGender(privateUserDTO.getGender());
        newUser.setRegions(privateUserDTO.getRegions());
        newUser.setPassword(privateUserDTO.getPassword());
        userRepository.save(userMapper.toEntity(newUser));
        return ResponseEntity.ok("User saved");
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getPerson(Long personId) {
        if (!userRepository.existsById(personId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorDTO("User with this ID does not exists"));
        }
        UserDTO entityUserDTO = userMapper.toDTO(userRepository.getReferenceById(personId));
        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setUserId(entityUserDTO.getUserId());
        newUserDTO.setNickName(entityUserDTO.getNickName());
        newUserDTO.setGender(entityUserDTO.getGender());
        newUserDTO.setAge(Period.between(entityUserDTO.getBirthDate(), LocalDate.now()).getYears());
        newUserDTO.setRegions(entityUserDTO.getRegions());
        newUserDTO.setBirthDate(null);
        return new ResponseEntity<>(newUserDTO, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findByGender(GenderType genderType) {
        List<UserEntity> userEntities = userRepository.findByGender(genderType);
        List<UserDTO> userDTOs = userEntities.stream()
                .map(userEntity -> {
                    UserDTO userDTO = userMapper.toDTO(userEntity);

                    userDTO.setAge(Period.between(userEntity.getBirthDate(), LocalDate.now()).getYears());
                    userDTO.setBirthDate(null);

                    return userDTO;
                })
                .collect(Collectors.toList());

        return userDTOs;
    }
}