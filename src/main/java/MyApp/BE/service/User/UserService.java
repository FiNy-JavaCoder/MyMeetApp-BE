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
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private final IUserProfileRepository profileRepository;
    private final UserProfileMapper profileMapper;

    @Autowired
    public UserService(IUserRepository userRepository, 
                      IUserProfileRepository profileRepository, 
                      UserMapper userMapper, 
                      UserProfileMapper profileMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<?> registerUser(RegistrationDTO registrationDTO) {
        // Validace duplicity
        if (userRepository.existsByNickName(registrationDTO.getNickName()) || 
            userRepository.existsByEmail(registrationDTO.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorDTO("Nickname or Email is already taken"));
        }

        try {
            // Vytvoření user entity
            UserEntity userToSave = userMapper.toEntity(registrationDTO);
            
            // Vytvoření prázdného profilu
            UserProfileEntity userProfile = new UserProfileEntity();
            
            // Nastavení bidirectional relationship
            userToSave.setUserProfileEntity(userProfile);
            userProfile.setUserEntity(userToSave);
            
            // Uložení (cascade se postará o profil)
            UserEntity savedUser = userRepository.save(userToSave);
            
            return ResponseEntity.ok("User and its profile saved successfully with ID: " + savedUser.getUserId());
            
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Registration failed: " + e.getMessage()));
        }
    }
}