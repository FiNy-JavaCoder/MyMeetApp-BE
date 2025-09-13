package MyApp.BE.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "person")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nick_name", nullable = false, unique = true)
    private String nickName;

    @Column(name = "passwordHash", nullable = true)
    private String password;

    @Getter
    @Column(name = "is_admin", nullable = false)
    private boolean admin = false;

    @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfileEntity userProfileEntity;
}