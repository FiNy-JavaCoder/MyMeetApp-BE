package MyApp.BE.entity;

import MyApp.BE.dto.mapper.converters.RegionsSetConverter;
import MyApp.BE.enums.*;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity(name = "user_profile")
public class UserProfileEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @MapsId
    private UserEntity userEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = true)
    private GenderType gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexual_orientation", nullable = true)
    private SearchSexualOrientation sexualOrientation;

    @Enumerated(EnumType.STRING)
    @Column(name = "search_type_relationship", nullable = true)
    private SearchTypeRelationShip searchTypeRelationShip;

    @Convert(converter = RegionsSetConverter.class)
    @Column(name = "regions", nullable = true, columnDefinition = "TEXT")
    private Set<Regions> regions;

    @Convert(converter = RegionsSetConverter.class)
    @Column(name = "districts", nullable = true, columnDefinition = "TEXT")
    private Set<Districts> districts;

    @Column(name = "birth_date", nullable = true)
    LocalDate birthDate;

    @Column(name = "profile_picture_url", nullable = true)
    private String profilePictureUrl;

    @Column(name = "about_me", length = 500, nullable = true)
    private String aboutMe;
}