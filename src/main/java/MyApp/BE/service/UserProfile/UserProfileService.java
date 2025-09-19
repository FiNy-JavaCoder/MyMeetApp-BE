package MyApp.BE.service.UserProfile;

import MyApp.BE.dto.ErrorDTO;
import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.dto.UserProfilePrivateDTO;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserProfileService implements IUserProfileService {

    private final IUserRepository userRepository;
    private final IUserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;

    @Autowired
    public UserProfileService(IUserRepository userRepository,
            IUserProfileRepository userProfileRepository,
            UserMapper userMapper,
            UserProfileMapper userProfileMapper) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userMapper = userMapper;
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<UserProfileDTO> getUserPublicProfile(Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<UserProfileEntity> profileOpt = userProfileRepository.findById(userId);
        if (profileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UserProfileEntity profileEntity = profileOpt.get();
        UserProfileDTO userProfileDTO = userProfileMapper.toPublicDTO(profileEntity);
        userProfileDTO.setAge(calculateAge(profileEntity, userProfileDTO));

        return ResponseEntity.ok(userProfileDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<UserProfilePrivateDTO> getUserPrivateProfile(Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<UserProfileEntity> profileOpt = userProfileRepository.findById(userId);
        if (profileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UserProfileEntity profileEntity = profileOpt.get();
        UserProfilePrivateDTO userProfileDTO = userProfileMapper.toPrivateDTO(profileEntity);
        userProfileDTO.setAge(calculateAge(profileEntity, userProfileDTO));

        return ResponseEntity.ok(userProfileDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileDTO> findByGender(GenderType genderType) {
        List<UserProfileEntity> userProfileEntities = userProfileRepository.findByGender(genderType);
        return userProfileEntities.stream()
                .map(userProfileEntity -> {
                    UserProfileDTO userProfileDTO = userProfileMapper.toPublicDTO(userProfileEntity);
                    userProfileDTO.setAge(calculateAge(userProfileEntity, userProfileDTO));
                    return userProfileDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserProfileDTO> findProfilesByGender(GenderType genderType) {
        return findByGender(genderType);
    }

    public int calculateAge(UserProfileEntity userEntity, UserProfileDTO userProfileDTO) {
        if (userEntity.getBirthDate() == null) {
            return 0;
        }
        int age = Period.between(userEntity.getBirthDate(), LocalDate.now()).getYears();
        userProfileDTO.setAge(age);
        return age;
    }

    public UserProfileDTO findProfile(Long userId) {
        UserProfileEntity entity = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        return userProfileMapper.toPublicDTO(entity);
    }
}