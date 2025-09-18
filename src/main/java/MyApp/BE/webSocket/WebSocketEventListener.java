package MyApp.BE.webSocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Listener for WebSocket connection events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    // Track online users
    private final Map<String, String> onlineUsers = new ConcurrentHashMap<>();

    // Track user sessions
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Principal user = headerAccessor.getUser();

        if (user != null) {
            String userId = user.getName();
            onlineUsers.put(userId, sessionId);
            sessionUserMap.put(sessionId, userId);

            log.info("User {} connected with session {}", userId, sessionId);

            // Notify other users that this user is online
            UserStatusNotification notification = new UserStatusNotification();
            notification.setUserId(Long.parseLong(userId));
            notification.setStatus("ONLINE");
            notification.setTimestamp(OffsetDateTime.now());

            messagingTemplate.convertAndSend("/topic/user-status", notification);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String userId = sessionUserMap.get(sessionId);

        if (userId != null) {
            onlineUsers.remove(userId);
            sessionUserMap.remove(sessionId);

            log.info("User {} disconnected from session {}", userId, sessionId);

            // Notify other users that this user is offline
            UserStatusNotification notification = new UserStatusNotification();
            notification.setUserId(Long.parseLong(userId));
            notification.setStatus("OFFLINE");
            notification.setTimestamp(OffsetDateTime.now());

            messagingTemplate.convertAndSend("/topic/user-status", notification);
        }
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        Principal user = headerAccessor.getUser();

        if (user != null && destination != null) {
            log.debug("User {} subscribed to {}", user.getName(), destination);

            // If subscribing to a conversation, mark messages as delivered
            if (destination.startsWith("/topic/conversation/")) {
                String conversationId = extractConversationId(destination);
                if (conversationId != null) {
                    notifyMessageDelivery(conversationId, Long.parseLong(user.getName()));
                }
            }
        }
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();

        if (user != null) {
            log.debug("User {} unsubscribed", user.getName());
        }
    }

    /**
     * Check if a user is online
     */
    public boolean isUserOnline(Long userId) {
        return onlineUsers.containsKey(userId.toString());
    }

    /**
     * Get all online users
     */
    public Map<String, String> getOnlineUsers() {
        return new ConcurrentHashMap<>(onlineUsers);
    }

    /**
     * Extract conversation ID from destination path
     */
    private String extractConversationId(String destination) {
        String prefix = "/topic/conversation/";
        if (destination.startsWith(prefix)) {
            int endIndex = destination.indexOf('/', prefix.length());
            if (endIndex == -1) {
                return destination.substring(prefix.length());
            }
            return destination.substring(prefix.length(), endIndex);
        }
        return null;
    }

    /**
     * Notify about message delivery
     */
    private void notifyMessageDelivery(String conversationId, Long userId) {
        MessageDeliveryNotification notification = new MessageDeliveryNotification();
        notification.setConversationId(conversationId);
        notification.setDeliveredToUserId(userId);
        notification.setTimestamp(OffsetDateTime.now());

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId + "/delivery",
                notification);
    }

    /**
     * Inner class for user status notifications
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    private static class UserStatusNotification {
        private Long userId;
        private String status;
        private OffsetDateTime timestamp;
    }

    /**
     * Inner class for message delivery notifications
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    private static class MessageDeliveryNotification {
        private String conversationId;
        private Long deliveredToUserId;
        private OffsetDateTime timestamp;
    }
}