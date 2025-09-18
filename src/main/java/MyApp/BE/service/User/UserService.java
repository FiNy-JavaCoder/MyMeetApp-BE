package MyApp.BE.service.User;

import MyApp.BE.dto.*;
import MyApp.BE.dto.mapper.UserMapper;
import MyApp.BE.dto.mapper.UserProfileMapper;
import MyApp.BE.entity.UserEntity;
import MyApp.BE.entity.UserProfileEntity;
import MyApp.BE.entity.repository.IUserProfileRepository;
import MyApp.BE.entity.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private final IUserProfileRepository profileRepository;
    private final UserProfileMapper profileMapper;

    @Autowired
    public UserService(IUserRepository userRepository, IUserProfileRepository profileRepository, UserMapper userMapper,
            UserProfileMapper profileMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
    }

    public ResponseEntity<?> registerUser(RegistrationDTO registrationDTO) {
        if ((userRepository.existsByNickName(registrationDTO.getNickName()))
                || userRepository.existsByEmail(registrationDTO.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorDTO("Nickname or Email is already taken"));
        }
        PrivateUserDTO privateUserDTO = new PrivateUserDTO();
        privateUserDTO.setNickName(registrationDTO.getNickName());
        privateUserDTO.setEmail(registrationDTO.getEmail());
        privateUserDTO.setPasswordHash(registrationDTO.getPasswordHash());

        UserEntity userToSave = userMapper.toEntity(privateUserDTO);
        UserProfileEntity userProfile = new UserProfileEntity();
        userToSave.setUserProfileEntity(userProfile);
        userProfile.setUserEntity(userToSave);
        userRepository.save(userToSave);

        return ResponseEntity.ok("User and its profile saved");
    }
}