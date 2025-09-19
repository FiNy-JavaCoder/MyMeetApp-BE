package MyApp.BE.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity(name = "person")
@ToString(exclude = "userProfileEntity") // Prevent circular reference in toString
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

    @Column(name = "is_admin", nullable = false)
    private boolean admin = false;

    @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserProfileEntity userProfileEntity;

    // Helper method to maintain bidirectional relationship
    public void setUserProfileEntity(UserProfileEntity userProfileEntity) {
        this.userProfileEntity = userProfileEntity;
        if (userProfileEntity != null) {
            userProfileEntity.setUserEntity(this);
        }
    }
}