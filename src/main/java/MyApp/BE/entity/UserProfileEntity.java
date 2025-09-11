package MyApp.BE.entity;


import MyApp.BE.dto.mapper.converters.RegionsSetConverter;
import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.Regions;
import MyApp.BE.enums.SearchSexualOrientation;
import MyApp.BE.enums.SearchTypeRelationShip;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;


@Data
@Entity(name = "user_profile")
public class UserProfileEntity {


    @Id
    @Column(name = "user_id") // Maps to the primary key, which is also a foreign key
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @MapsId
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private GenderType gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexual_orientation", nullable = false)
    private SearchSexualOrientation sexualOrientation;

    @Enumerated(EnumType.STRING)
    @Column(name = "search_type_relationship", nullable = false)
    private SearchTypeRelationShip searchTypeRelationShip;

    @Convert(converter = RegionsSetConverter.class)
    @Column(name = "regions", nullable = false, columnDefinition = "TEXT")
    private Set<Regions> regions;

    @Convert(converter = RegionsSetConverter.class)
    @Column(name = "districts", nullable = false, columnDefinition = "TEXT")
    private Set<Regions> districts;

    @Column(name = "birth_date", nullable = false)
    LocalDate birthDate;

    @Column(name = "profile_picture_url", nullable = true )
    private String profilePictureUrl;

    @Column (name = "about_me", length = 500, nullable = true)
    private String aboutMe;

    @Column(name = "favoriteFilter", length = 2000, nullable = true)
    private String favoriteFilter;

}
