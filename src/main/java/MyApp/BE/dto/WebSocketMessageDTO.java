package MyApp.BE.webSocket;

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

/**
 * DTO for message delivery status
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDeliveryStatusDTO {
    private Long messageId;
    private String conversationId;
    private DeliveryStatus status;
    private OffsetDateTime timestamp;
    
    public enum DeliveryStatus {
        SENT,
        DELIVERED,
        READ,
        FAILED
    }
}