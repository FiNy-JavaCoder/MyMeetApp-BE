package MyApp.BE.service.UserProfile;

import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.dto.UserProfilePrivateDTO;
import MyApp.BE.dto.mapper.UserMapper;
import MyApp.BE.dto.mapper.UserProfileMapper;
import MyApp.BE.entity.UserProfileEntity;
import MyApp.BE.entity.repository.IUserProfileRepository;
import MyApp.BE.entity.repository.IUserRepository;
import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.SearchSexualOrientation;
import MyApp.BE.enums.SearchTypeRelationShip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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
@RequiredArgsConstructor
@Slf4j
public class UserProfileService implements IUserProfileService {

    private final IUserRepository userRepository;
    private final IUserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final UserSearchService userSearchService;

    @Override
    @Cacheable(value = "userProfile", key = "#userId")
    @Transactional(readOnly = true)
    public ResponseEntity<UserProfileDTO> getUserPublicProfile(Long userId) {
        log.debug("Getting public profile for user {}", userId);
        
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<UserProfileEntity> profileOpt = userProfileRepository.findById(userId);
        if (profileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UserProfileEntity profileEntity = profileOpt.get();
        UserProfileDTO userProfileDTO = userProfileMapper.toPublicDTO(profileEntity);
        
        // Calculate and set age
        userProfileDTO.setAge(calculateAge(profileEntity));
        
        // Set userId from entity
        userProfileDTO.setUserId(profileEntity.getUserId());

        return ResponseEntity.ok(userProfileDTO);
    }

    @Override
    @Cacheable(value = "userProfile", key = "'private_' + #userId")
    @Transactional(readOnly = true)
    public ResponseEntity<UserProfilePrivateDTO> getUserPrivateProfile(Long userId) {
        log.debug("Getting private profile for user {}", userId);
        
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<UserProfileEntity> profileOpt = userProfileRepository.findById(userId);
        if (profileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UserProfileEntity profileEntity = profileOpt.get();
        UserProfilePrivateDTO userProfileDTO = userProfileMapper.toPrivateDTO(profileEntity);
        
        // Calculate and set age
        userProfileDTO.setAge(calculateAge(profileEntity));
        
        // Set userId from entity
        userProfileDTO.setUserId(profileEntity.getUserId());

        return ResponseEntity.ok(userProfileDTO);
    }

    @Override
    @Cacheable(value = "userProfiles", key = "#genderType")
    @Transactional(readOnly = true)
    public List<UserProfileDTO> findByGender(GenderType genderType) {
        log.debug("Finding profiles by gender: {}", genderType);
        
        if (genderType == null) {
            return List.of();
        }

        List<UserProfileEntity> userProfileEntities = userProfileRepository.findByGender(genderType);
        return userProfileEntities.stream()
                .map(userProfileEntity -> {
                    UserProfileDTO userProfileDTO = userProfileMapper.toPublicDTO(userProfileEntity);
                    userProfileDTO.setAge(calculateAge(userProfileEntity));
                    userProfileDTO.setUserId(userProfileEntity.getUserId());
                    return userProfileDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserProfileDTO> findProfilesByGender(GenderType genderType) {
        return findByGender(genderType);
    }

    @Transactional(readOnly = true)
    public List<UserProfileDTO> searchUsers(
            String nickName,
            GenderType gender,
            Integer minAge,
            Integer maxAge,
            SearchSexualOrientation sexualOrientation,
            SearchTypeRelationShip lookingFor,
            List<String> regions,
            List<String> districts
    ) {
        log.info("Searching users with advanced criteria");
        return userSearchService.searchUsers(nickName, gender, minAge, maxAge, 
                                           sexualOrientation, lookingFor, regions, districts);
    }

    @Transactional(readOnly = true)
    public UserProfileDTO findProfile(Long userId) {
        log.debug("Finding profile for user {}", userId);
        
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        UserProfileEntity entity = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for ID: " + userId));
        
        UserProfileDTO dto = userProfileMapper.toPublicDTO(entity);
        dto.setAge(calculateAge(entity));
        dto.setUserId(entity.getUserId());
        
        return dto;
    }

    @Transactional(readOnly = true)
    public List<UserProfileDTO> getAllProfiles() {
        log.debug("Getting all user profiles");
        
        List<UserProfileEntity> allProfiles = userProfileRepository.findAll();
        return allProfiles.stream()
                .map(entity -> {
                    UserProfileDTO dto = userProfileMapper.toPublicDTO(entity);
                    dto.setAge(calculateAge(entity));
                    dto.setUserId(entity.getUserId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserProfileDTO> findProfilesByNickName(String nickName) {
        log.debug("Finding profiles by nickname containing: {}", nickName);
        
        if (nickName == null || nickName.trim().isEmpty()) {
            return List.of();
        }

        List<UserProfileEntity> profiles = userProfileRepository.findByNickNameContaining(nickName.trim());
        return profiles.stream()
                .map(entity -> {
                    UserProfileDTO dto = userProfileMapper.toPublicDTO(entity);
                    dto.setAge(calculateAge(entity));
                    dto.setUserId(entity.getUserId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean profileExists(Long userId) {
        if (userId == null) {
            return false;
        }
        return userProfileRepository.existsById(userId);
    }

    @Transactional(readOnly = true)
    public long getTotalProfileCount() {
        return userProfileRepository.count();
    }

    @Transactional(readOnly = true)
    public long getProfileCountByGender(GenderType gender) {
        if (gender == null) {
            return 0;
        }
        return userProfileRepository.findByGender(gender).size();
    }

    private int calculateAge(UserProfileEntity userEntity) {
        if (userEntity == null || userEntity.getBirthDate() == null) {
            return 0;
        }
        
        try {
            return Period.between(userEntity.getBirthDate(), LocalDate.now()).getYears();
        } catch (Exception e) {
            log.warn("Error calculating age for user {}: {}", userEntity.getUserId(), e.getMessage());
            return 0;
        }
    }
}