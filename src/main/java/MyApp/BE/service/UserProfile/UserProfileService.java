package MyApp.BE.service.UserProfile;

import MyApp.BE.dto.ErrorDTO;
import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.dto.mapper.UserMapper;
import MyApp.BE.dto.mapper.UserProfileMapper;
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
public class UserProfileService {
    //        userProfileEntity.setBirthDate(LocalDate.of(registrationDTO.getBirthYear(), registrationDTO.getBirthMonth(), 15));
    //        userProfileDTO.setAge(Period.between(entityUserDTO.getBirthDate(), LocalDate.now()).getYears());

    private final IUserRepository userRepository;
    private final IUserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;

    @Autowired
    public UserProfileService(IUserRepository userRepository, IUserProfileRepository userProfileRepository, UserMapper userMapper, UserProfileMapper userProfileMapper) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userMapper = userMapper;
        this.userProfileMapper = userProfileMapper;
    }


    @Transactional(readOnly = true)
    public ResponseEntity<?> getUserPublicProfile(Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorDTO("User with this ID does not exists"));
        }
        UserProfileDTO entityUserDTO = findProfile(userId);
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setUserId(entityUserDTO.getUserId());
        userProfileDTO.setNickName(entityUserDTO.getNickName());
        return new ResponseEntity<>(userProfileDTO, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public List<UserProfileDTO> findProfilesByGender(GenderType genderType) {
        List<UserProfileEntity> userProfileEntities = userProfileRepository.findByGender(genderType);
        List<UserProfileDTO> userDTOs = userProfileEntities.stream()
                .map(userProfileEntity  -> {
                    UserProfileDTO userProfileDTO = userProfileMapper.toPublicDTO(userProfileEntity);
                    userProfileDTO.setAge(calculateAge(userProfileEntity, userProfileDTO));
                    return userProfileDTO;
                })
                .collect(Collectors.toList());

        return userDTOs;
    }

    public int calculateAge(UserProfileEntity userEntity, UserProfileDTO userProfileDTO) {
        userProfileDTO.setAge(Period.between(userEntity.getBirthDate(), LocalDate.now()).getYears());
        return userProfileDTO.getAge();
    }

    public UserProfileDTO findProfile(Long userId) {
        return userProfileMapper.toPublicDTO(userProfileRepository.findById(userId).orElseThrow(() -> new RuntimeException("User profile not found")));

    }
    /** public UserProfileDTO editUserProfile(Long userId) {
        UserProfileDTO loadedProfile = findProfile(userId);

        return
    }
**/


}
