package MyApp.BE.entity;


import MyApp.BE.enums.GenderType;
import MyApp.BE.enums.Regions;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;
import MyApp.BE.dto.mapper.converters.RegionsSetConverter;


@Data
@Entity(name = "person")
public class UserEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String nickName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenderType gender;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Convert(converter = RegionsSetConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private Set<Regions> regions;


    @Column(nullable = false)
    private boolean admin = false;

    @Column
    private String profilePictureUrl;

}
