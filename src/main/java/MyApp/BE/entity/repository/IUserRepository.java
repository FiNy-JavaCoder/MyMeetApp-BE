package MyApp.BE.entity.repository;

import MyApp.BE.entity.UserEntity;
import MyApp.BE.enums.GenderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByNickName(String nickname);

    List<UserEntity> findByGender(GenderType gender);
}
