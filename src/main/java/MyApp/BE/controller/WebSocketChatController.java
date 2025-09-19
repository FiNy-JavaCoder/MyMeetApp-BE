package MyApp.BE.controller;

import MyApp.BE.dto.MessageDTO;
import MyApp.BE.dto.ReadStatusDTO;
import MyApp.BE.dto.TypingIndicatorDTO;
import MyApp.BE.dto.UserStatusDTO;
import MyApp.BE.service.chat.ChatService;
import MyApp.BE.dto.WebSocketNotificationDTO;
import MyApp.BE.dto.WebSocketMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.OffsetDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle sending messages through WebSocket
     */
    @MessageMapping("/chat.send/{conversationId}")
    public void sendMessage(
            @DestinationVariable String conversationId,
            @Payload WebSocketMessageDTO message,
            Principal principal) {
        
        log.info("WebSocket message received in conversation: {} from user: {}", 
                conversationId, principal.getName());
        
        try {
            // Validate principal
            if (principal == null || principal.getName() == null) {
                log.error("No authenticated user found for WebSocket message");
                return;
            }

            Long senderId = Long.parseLong(principal.getName());
            
            // Validate message content
            if (message.getContent() == null || message.getContent().trim().isEmpty()) {
                log.warn("Empty message content from user {}", senderId);
                return;
            }

            // Create MessageDTO from WebSocket message
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setSenderId(senderId);
            messageDTO.setRecipientId(message.getRecipientId());
            messageDTO.setCntMessage(message.getContent().trim());
            messageDTO.setConversationId(conversationId);
            messageDTO.setTimeStamp(OffsetDateTime.now());
            
            // Save message through service
            MessageDTO savedMessage = chatService.sendMessage(messageDTO);
            
            // Convert to WebSocket response
            WebSocketMessageDTO response = new WebSocketMessageDTO();
            response.setMessageId(savedMessage.getMessageId());
            response.setSenderId(savedMessage.getSenderId());
            response.setRecipientId(savedMessage.getRecipientId());
            response.setContent(savedMessage.getCntMessage());
            response.setTimestamp(savedMessage.getTimeStamp());
            response.setConversationId(conversationId);
            response.setRead(savedMessage.isRead());
            response.setEdited(savedMessage.isEdited());
            
            // Send to conversation topic (all participants)
            messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, response);
            
            // Send notification to recipient's personal queue
            WebSocketNotificationDTO notification = new WebSocketNotificationDTO();
            notification.setType("NEW_MESSAGE");
            notification.setFromUserId(savedMessage.getSenderId());
            notification.setMessage(savedMessage.getCntMessage());
            notification.setConversationId(conversationId);
            notification.setTimestamp(savedMessage.getTimeStamp());
            
            messagingTemplate.convertAndSendToUser(
                    savedMessage.getRecipientId().toString(),
                    "/queue/notifications",
                    notification
            );
            
            log.info("WebSocket message sent successfully: messageId={}", savedMessage.getMessageId());
            
        } catch (NumberFormatException e) {
            log.error("Invalid user ID in principal: {}", principal.getName(), e);
        } catch (Exception e) {
            log.error("Error handling WebSocket message in conversation {}", conversationId, e);
            
            // Send error notification back to sender
            WebSocketNotificationDTO errorNotification = new WebSocketNotificationDTO();
            errorNotification.setType("ERROR");
            errorNotification.setMessage("Nepodařilo se odeslat zprávu");
            errorNotification.setTimestamp(OffsetDateTime.now());
            
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/notifications",
                    errorNotification
            );
        }
    }

    /**
     * Handle typing indicators
     */
    @MessageMapping("/chat.typing/{conversationId}")
    @SendTo("/topic/conversation/{conversationId}/typing")
    public TypingIndicatorDTO handleTyping(
            @DestinationVariable String conversationId,
            @Payload TypingIndicatorDTO typing,
            Principal principal) {
        
        try {
            if (principal == null || principal.getName() == null) {
                log.warn("No authenticated user for typing indicator");
                return null;
            }

            Long userId = Long.parseLong(principal.getName());
            typing.setUserId(userId);
            typing.setTimestamp(OffsetDateTime.now());
            
            log.debug("User {} is typing in conversation {}", userId, conversationId);
            return typing;
            
        } catch (NumberFormatException e) {
            log.error("Invalid user ID for typing indicator: {}", principal.getName(), e);
            return null;
        } catch (Exception e) {
            log.error("Error handling typing indicator", e);
            return null;
        }
    }

    /**
     * Handle message read status updates
     */
    @MessageMapping("/chat.read/{conversationId}")
    public void markAsRead(
            @DestinationVariable String conversationId,
            Principal principal) {
        
        try {
            if (principal == null || principal.getName() == null) {
                log.warn("No authenticated user for read status update");
                return;
            }

            Long userId = Long.parseLong(principal.getName());
            log.debug("User {} marked conversation {} as read", userId, conversationId);
            
            // Mark messages as read
            chatService.markMessagesAsRead(conversationId, userId);
            
            // Notify sender that messages were read
            ReadStatusDTO readStatus = new ReadStatusDTO();
            readStatus.setConversationId(conversationId);
            readStatus.setReadByUserId(userId);
            readStatus.setTimestamp(OffsetDateTime.now());
            
            messagingTemplate.convertAndSend("/topic/conversation/" + conversationId + "/read", readStatus);
            
        } catch (NumberFormatException e) {
            log.error("Invalid user ID for read status: {}", principal.getName(), e);
        } catch (Exception e) {
            log.error("Error marking messages as read in conversation {}", conversationId, e);
        }
    }

    /**
     * Handle user online/offline status
     */
    @MessageMapping("/user.status")
    @SendTo("/topic/user-status")
    public UserStatusDTO updateUserStatus(@Payload UserStatusDTO status, Principal principal) {
        try {
            if (principal == null || principal.getName() == null) {
                log.warn("No authenticated user for status update");
                return null;
            }

            Long userId = Long.parseLong(principal.getName());
            status.setUserId(userId);
            status.setTimestamp(OffsetDateTime.now());
            
            log.debug("User {} status updated to {}", userId, status.getStatus());
            return status;
            
        } catch (NumberFormatException e) {
            log.error("Invalid user ID for status update: {}", principal.getName(), e);
            return null;
        } catch (Exception e) {
            log.error("Error updating user status", e);
            return null;
        }
    }

    /**
     * Handle user joining a conversation
     */
    @MessageMapping("/chat.join/{conversationId}")
    public void joinConversation(
            @DestinationVariable String conversationId,
            Principal principal) {
        
        try {
            if (principal == null || principal.getName() == null) {
                log.warn("No authenticated user for conversation join");
                return;
            }

            Long userId = Long.parseLong(principal.getName());
            log.info("User {} joined conversation {}", userId, conversationId);
            
            // Mark messages as delivered when user joins
            WebSocketNotificationDTO joinNotification = new WebSocketNotificationDTO();
            joinNotification.setType("USER_JOINED");
            joinNotification.setFromUserId(userId);
            joinNotification.setConversationId(conversationId);
            joinNotification.setTimestamp(OffsetDateTime.now());
            
            messagingTemplate.convertAndSend("/topic/conversation/" + conversationId + "/status", joinNotification);
            
        } catch (NumberFormatException e) {
            log.error("Invalid user ID for conversation join: {}", principal.getName(), e);
        } catch (Exception e) {
            log.error("Error handling conversation join", e);
        }
    }

    /**
     * Handle user leaving a conversation
     */
    @MessageMapping("/chat.leave/{conversationId}")
    public void leaveConversation(
            @DestinationVariable String conversationId,
            Principal principal) {
        
        try {
            if (principal == null || principal.getName() == null) {
                log.warn("No authenticated user for conversation leave");
                return;
            }

            Long userId = Long.parseLong(principal.getName());
            log.info("User {} left conversation {}", userId, conversationId);
            
            WebSocketNotificationDTO leaveNotification = new WebSocketNotificationDTO();
            leaveNotification.setType("USER_LEFT");
            leaveNotification.setFromUserId(userId);
            leaveNotification.setConversationId(conversationId);
            leaveNotification.setTimestamp(OffsetDateTime.now());
            
            messagingTemplate.convertAndSend("/topic/conversation/" + conversationId + "/status", leaveNotification);
            
        } catch (NumberFormatException e) {
            log.error("Invalid user ID for conversation leave: {}", principal.getName(), e);
        } catch (Exception e) {
            log.error("Error handling conversation leave", e);
        }
    }
}