package MyApp.BE.service.UserProfile;

import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.dto.mapper.UserProfileMapper;
import MyApp.BE.entity.UserProfileEntity;
import MyApp.BE.entity.repository.IUserProfileRepository;
import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.SearchSexualOrientation;
import MyApp.BE.enums.SearchTypeRelationShip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSearchService {

    private final IUserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

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
        log.info("Searching users with criteria: gender={}, age={}-{}, orientation={}, lookingFor={}", 
                gender, minAge, maxAge, sexualOrientation, lookingFor);

        List<UserProfileEntity> allProfiles = userProfileRepository.findAll();
        
        return allProfiles.stream()
                .filter(profile -> matchesSearchCriteria(
                    profile, nickName, gender, minAge, maxAge, 
                    sexualOrientation, lookingFor, regions, districts))
                .map(entity -> {
                    UserProfileDTO dto = userProfileMapper.toPublicDTO(entity);
                    dto.setAge(calculateAge(entity));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private boolean matchesSearchCriteria(
            UserProfileEntity profile,
            String nickName,
            GenderType gender,
            Integer minAge,
            Integer maxAge,
            SearchSexualOrientation sexualOrientation,
            SearchTypeRelationShip lookingFor,
            List<String> regions,
            List<String> districts
    ) {
        // Filtrování podle přezdívky
        if (nickName != null && !nickName.trim().isEmpty()) {
            String profileNickName = profile.getUserEntity() != null && profile.getUserEntity().getNickName() != null 
                ? profile.getUserEntity().getNickName() : "";
            if (!profileNickName.toLowerCase().contains(nickName.toLowerCase().trim())) {
                return false;
            }
        }

        // Filtrování podle pohlaví
        if (gender != null && profile.getGender() != null && !profile.getGender().equals(gender)) {
            return false;
        }

        // Filtrování podle věku
        if (profile.getBirthDate() != null) {
            int age = calculateAge(profile);
            if (minAge != null && age < minAge) {
                return false;
            }
            if (maxAge != null && age > maxAge) {
                return false;
            }
        }

        // Filtrování podle sexuální orientace
        if (sexualOrientation != null && profile.getSexualOrientation() != null && 
            !sexualOrientation.equals(profile.getSexualOrientation())) {
            return false;
        }

        // Filtrování podle typu vztahu
        if (lookingFor != null && profile.getSearchTypeRelationShip() != null && 
            !lookingFor.equals(profile.getSearchTypeRelationShip())) {
            return false;
        }

        // Filtrování podle regionů
        if (regions != null && !regions.isEmpty() && profile.getRegions() != null) {
            boolean hasMatchingRegion = profile.getRegions().stream()
                    .anyMatch(profileRegion -> regions.contains(profileRegion.toString()));
            if (!hasMatchingRegion) {
                return false;
            }
        }

        // Filtrování podle okresů
        if (districts != null && !districts.isEmpty() && profile.getDistricts() != null) {
            boolean hasMatchingDistrict = profile.getDistricts().stream()
                    .anyMatch(profileDistrict -> districts.contains(profileDistrict.toString()));
            if (!hasMatchingDistrict) {
                return false;
            }
        }

        return true;
    }

    private int calculateAge(UserProfileEntity profile) {
        if (profile.getBirthDate() == null) {
            return 0;
        }
        return Period.between(profile.getBirthDate(), LocalDate.now()).getYears();
    }
}