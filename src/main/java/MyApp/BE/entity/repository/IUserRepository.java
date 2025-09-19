package MyApp.BE.entity.repository;

import MyApp.BE.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByNickName(String nickname);

    boolean existsByEmail(String email);
}
