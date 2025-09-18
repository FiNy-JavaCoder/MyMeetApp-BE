package MyApp.BE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

/**
 * DTO for connection events
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketConnectionDTO {
    private Long userId;
    private String sessionId;
    private ConnectionStatus status;
    private OffsetDateTime timestamp;

    public enum ConnectionStatus {
        CONNECTED,
        DISCONNECTED,
        RECONNECTING
    }
}