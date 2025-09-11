package MyApp.BE.service.User;

import MyApp.BE.dto.*;
import MyApp.BE.dto.mapper.UserMapper;
import MyApp.BE.dto.mapper.UserProfileMapper;
import MyApp.BE.entity.UserEntity;
import MyApp.BE.entity.UserProfileEntity;
import MyApp.BE.entity.repository.IUserProfileRepository;
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
    private final IUserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;

    @Autowired
    public UserService(IUserRepository userRepository, IUserProfileRepository userProfileRepository, UserMapper userMapper, UserProfileMapper userProfileMapper) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userMapper = userMapper;
        this.userProfileMapper = userProfileMapper;
    }

    public ResponseEntity<?> registerUser(RegistrationDTO registrationDTO) {
        if ((userRepository.existsByNickName(registrationDTO.getNickName())) || userRepository.existsByEmail(registrationDTO.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorDTO("Nickname or Email is already taken"));
        }
        PrivateUserDTO privateUserDTO = new PrivateUserDTO();
        privateUserDTO.setNickName(registrationDTO.getNickName());
        privateUserDTO.setEmail(registrationDTO.getEmail());
        privateUserDTO.setPasswordHash("");
        UserEntity userToSave = userMapper.toEntity(privateUserDTO);
        UserEntity savedUser = userRepository.save(userToSave);

        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setUser(savedUser);
        userProfileEntity.setGender(registrationDTO.getGenderType());
        userProfileEntity.setBirthDate(LocalDate.of(registrationDTO.getBirthYear(), registrationDTO.getBirthMonth(), 15));
        userProfileEntity.setRegions(registrationDTO.getRegions());
        userProfileEntity.setProfilePictureUrl("");
        userProfileRepository.save(userProfileEntity);
        return ResponseEntity.ok("User and its profile saved");
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getPerson(Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorDTO("User with this ID does not exists"));
        }
        UserProfileDTO entityUserDTO = userProfileMapper.toDTO(userProfileRepository.findById(userId).orElseThrow(() -> new RuntimeException("User profile not found")));
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setUserId(entityUserDTO.getUserId());
        userProfileDTO.setNickName(entityUserDTO.getNickName());
        userProfileDTO.setGender(entityUserDTO.getGender());
        userProfileDTO.setAge(Period.between(entityUserDTO.getBirthDate(), LocalDate.now()).getYears());
        userProfileDTO.setRegions(entityUserDTO.getRegions());
        userProfileDTO.setBirthDate(null);
        return new ResponseEntity<>(userProfileDTO, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public List<UserProfileDTO> findByGender(GenderType genderType) {
        List<UserProfileEntity> userProfileEntities = userProfileRepository.findByGender(genderType);
        List<UserProfileDTO> userDTOs = userProfileEntities.stream()
                .map(userProfileEntity  -> {
                    UserProfileDTO userProfileDTO = userProfileMapper.toDTO(userProfileEntity);
                    userProfileDTO.setAge(calculateAge(userProfileEntity, userProfileDTO));
                    userProfileDTO.setBirthDate(null);
                    return userProfileDTO;
                })
                .collect(Collectors.toList());

        return userDTOs;
    }

    public int calculateAge(UserProfileEntity userEntity, UserProfileDTO userProfileDTO) {
        userProfileDTO.setAge(Period.between(userEntity.getBirthDate(), LocalDate.now()).getYears());
        return userProfileDTO.getAge();
    }

}