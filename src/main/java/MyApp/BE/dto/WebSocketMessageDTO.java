package MyApp.BE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * DTO for WebSocket message transmission
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessageDTO {
    private Long messageId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private String conversationId;
    private OffsetDateTime timestamp;
    private boolean isRead;
    private boolean isEdited;
}
