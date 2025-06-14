package MyApp.BE.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
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

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "is_admin", nullable = false)
    private boolean admin = false;

}
