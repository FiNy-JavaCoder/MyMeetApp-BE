package MyApp.BE.entity.repository;

import MyApp.BE.entity.UserEntity;
import MyApp.BE.entity.UserProfileEntity;
import MyApp.BE.enums.GenderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserProfileRepository extends JpaRepository<UserProfileEntity, Long> {

    List<UserProfileEntity> findByGender(GenderType gender);


}
