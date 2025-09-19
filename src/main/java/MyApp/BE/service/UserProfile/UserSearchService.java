package MyApp.BE.service.UserProfile;

import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.dto.mapper.UserProfileMapper;
import MyApp.BE.entity.UserProfileEntity;
import MyApp.BE.entity.repository.IUserProfileRepository;
import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.SearchSexualOrientation;
import MyApp.BE.enums.SearchTypeRelationShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSearchService {

    @Autowired
    private IUserProfileRepository userProfileRepository;

    @Autowired
    private UserProfileMapper userProfileMapper;

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
        if (nickName != null && !nickName.isEmpty()) {
            if (!profile.getUserEntity().getNickName()
                    .toLowerCase().contains(nickName.toLowerCase())) {
                return false;
            }
        }

        // Filtrování podle pohlaví
        if (gender != null && !profile.getGender().equals(gender)) {
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
        if (sexualOrientation != null && 
            !sexualOrientation.equals(profile.getSexualOrientation())) {
            return false;
        }

        // Filtrování podle typu vztahu
        if (lookingFor != null && 
            !lookingFor.equals(profile.getSearchTypeRelationShip())) {
            return false;
        }

        // Filtrování podle regionů - implementujte podle vaší logiky
        if (regions != null && !regions.isEmpty()) {
            // Zde implementujte logiku pro kontrolu regionů
            // Například pokud profile.getRegions() obsahuje některý z hledaných regionů
        }

        // Filtrování podle okresů
        if (districts != null && !districts.isEmpty()) {
            // Zde implementujte logiku pro kontrolu okresů
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