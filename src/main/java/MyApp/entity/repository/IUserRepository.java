package MyApp.entity.repository;

import MyApp.entity.UserEntity;
import MyApp.enums.GenderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findByGender(GenderType gender);
}
