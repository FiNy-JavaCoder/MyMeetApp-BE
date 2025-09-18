package MyApp.BE.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
/**
 * DTO for WebSocket notifications
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketNotificationDTO {
    private String type; // NEW_MESSAGE, MESSAGE_READ, USER_ONLINE, USER_OFFLINE, TYPING
    private Long fromUserId;
    private String message;
    private String conversationId;
    private OffsetDateTime timestamp;
    private Object metadata; // Additional data based on notification type
}