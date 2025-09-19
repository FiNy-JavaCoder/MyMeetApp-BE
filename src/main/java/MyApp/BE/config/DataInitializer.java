package MyApp.BE.config;

import MyApp.BE.entity.UserEntity;
import MyApp.BE.entity.UserProfileEntity;
import MyApp.BE.entity.repository.IUserRepository;
import MyApp.BE.entity.repository.IUserProfileRepository;
import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.SearchSexualOrientation;
import MyApp.BE.enums.SearchTypeRelationShip;
import MyApp.BE.enums.Regions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IUserProfileRepository userProfileRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            createTestUsers();
        }
    }

    private void createTestUsers() {
        // Vytvoření testovacích uživatelů
        createTestUser("anna.svobodova", "anna@test.cz", "Anna Svobodová", 
                      GenderType.female, LocalDate.of(1995, 5, 15),
                      SearchSexualOrientation.HETEROSEXUAL, 
                      SearchTypeRelationShip.FORMAL_RELATIONSHIP);

        createTestUser("petr.novak", "petr@test.cz", "Petr Novák", 
                      GenderType.male, LocalDate.of(1992, 8, 22),
                      SearchSexualOrientation.HETEROSEXUAL, 
                      SearchTypeRelationShip.FORMAL_RELATIONSHIP);

        createTestUser("marie.krasna", "marie@test.cz", "Marie Krásná", 
                      GenderType.female, LocalDate.of(1998, 12, 3),
                      SearchSexualOrientation.BISEXUAL, 
                      SearchTypeRelationShip.FLIRT);

        createTestUser("jakub.svoboda", "jakub@test.cz", "Jakub Svoboda", 
                      GenderType.male, LocalDate.of(1990, 3, 18),
                      SearchSexualOrientation.HETEROSEXUAL, 
                      SearchTypeRelationShip.ROMANCE_RELATIONSHIP);

        createTestUser("tereza.nova", "tereza@test.cz", "Tereza Nová", 
                      GenderType.female, LocalDate.of(1994, 7, 9),
                      SearchSexualOrientation.HETEROSEXUAL, 
                      SearchTypeRelationShip.FRIENDSHIP);
    }

    private void createTestUser(String nickName, String email, String fullName,
                               GenderType gender, LocalDate birthDate,
                               SearchSexualOrientation orientation,
                               SearchTypeRelationShip relationshipType) {
        
        // Vytvoření základního uživatele
        UserEntity user = new UserEntity();
        user.setNickName(nickName);
        user.setEmail(email);
        user.setPassword("$2a$10$dummyHashForTesting"); // V produkci použijte BCrypt
        user.setAdmin(false);

        // Vytvoření profilu
        UserProfileEntity profile = new UserProfileEntity();
        profile.setUserEntity(user);
        profile.setGender(gender);
        profile.setBirthDate(birthDate);
        profile.setSexualOrientation(orientation);
        profile.setSearchTypeRelationShip(relationshipType);
        profile.setRegions(Set.of(Regions.STREDOCESKY_KRAJ, Regions.JIHOMORAVSKY_KRAJ));
        profile.setAboutMe("Ahoj! Jsem " + fullName + " a hledám nové přátele a možná i lásku.");

        // Nastavení vzájemných vztahů
        user.setUserProfileEntity(profile);

        // Uložení
        userRepository.save(user);
    }
}