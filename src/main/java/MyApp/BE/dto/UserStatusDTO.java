package MyApp.BE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusDTO {
    private Long userId;
    private String status; // ONLINE, OFFLINE, AWAY
    private OffsetDateTime timestamp;
}