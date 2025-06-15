package MyApp.BE.entity;


import MyApp.BE.dto.mapper.converters.RegionsSetConverter;
import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.Regions;
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

    @Convert(converter = RegionsSetConverter.class)
    @Column(name = "regions", nullable = false, columnDefinition = "TEXT")
    private Set<Regions> regions;

    @Column(name = "birth_date", nullable = false)
    LocalDate birthDate;

    @Column(name = "profile_picture_url", nullable = true)
    private String profilePictureUrl;

}
