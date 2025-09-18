package MyApp.BE.dto;

import java.time.OffsetDateTime;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
class UserStatusDTO {
    private Long userId;
    private String status; // ONLINE, OFFLINE, AWAY
    private OffsetDateTime timestamp;
}