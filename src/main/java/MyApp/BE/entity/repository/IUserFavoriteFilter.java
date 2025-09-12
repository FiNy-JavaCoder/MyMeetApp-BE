package MyApp.BE.entity.repository;

import MyApp.BE.entity.UserFavoriteFilterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserFavoriteFilter extends JpaRepository<UserFavoriteFilterEntity, Long> {


}
