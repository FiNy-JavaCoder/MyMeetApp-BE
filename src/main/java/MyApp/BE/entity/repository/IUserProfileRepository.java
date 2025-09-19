package MyApp.BE.entity.repository;

import MyApp.BE.entity.UserProfileEntity;
import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.SearchSexualOrientation;
import MyApp.BE.enums.SearchTypeRelationShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IUserProfileRepository extends JpaRepository<UserProfileEntity, Long> {

    List<UserProfileEntity> findByGender(GenderType gender);

    @Query("SELECT p FROM user_profile p WHERE " +
           "(:gender IS NULL OR p.gender = :gender) AND " +
           "(:sexualOrientation IS NULL OR p.sexualOrientation = :sexualOrientation) AND " +
           "(:relationshipType IS NULL OR p.searchTypeRelationShip = :relationshipType) AND " +
           "(:minBirthDate IS NULL OR p.birthDate <= :minBirthDate) AND " +
           "(:maxBirthDate IS NULL OR p.birthDate >= :maxBirthDate)")
    List<UserProfileEntity> findBySearchCriteria(
            @Param("gender") GenderType gender,
            @Param("sexualOrientation") SearchSexualOrientation sexualOrientation,
            @Param("relationshipType") SearchTypeRelationShip relationshipType,
            @Param("minBirthDate") LocalDate minBirthDate,
            @Param("maxBirthDate") LocalDate maxBirthDate
    );

    @Query("SELECT p FROM user_profile p JOIN p.userEntity u WHERE " +
           "LOWER(u.nickName) LIKE LOWER(CONCAT('%', :nickName, '%'))")
    List<UserProfileEntity> findByNickNameContaining(@Param("nickName") String nickName);
}