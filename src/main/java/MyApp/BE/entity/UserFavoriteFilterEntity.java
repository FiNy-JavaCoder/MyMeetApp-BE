package MyApp.BE.entity;

import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.SearchSexualOrientation;
import MyApp.BE.enums.SearchTypeRelationShip;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity(name = "user_favorite_filter")
public class UserFavoriteFilterEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @MapsId
    private UserEntity userEntity;

    @Column(name = "gender", nullable = true)
    private GenderType gender;

    @Column(name = "sexual_orientation", nullable = true)
    private SearchSexualOrientation sexualOrientation;

    @Column(name = "type_relationship", nullable = true)
    private SearchTypeRelationShip typeRelationShip;

    @Column(name = "min_age", nullable = true)
    private Integer minAge;

    @Column(name = "max_age", nullable = true)
    private Integer maxAge;

}
