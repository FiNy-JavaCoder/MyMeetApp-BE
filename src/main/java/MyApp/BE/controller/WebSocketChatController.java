
package MyApp.BE.controller;

import MyApp.BE.dto.MessageDTO;
import MyApp.BE.dto.ReadStatusDTO;
import MyApp.BE.dto.TypingIndicatorDTO;
import MyApp.BE.dto.UserStatusDTO;

import MyApp.BE.service.message.ChatService;
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
import org.springframework.security.core.Authentication;
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
        
        log.info("WebSocket message received in conversation: {}", conversationId);
        
        // Create MessageDTO from WebSocket message
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setSenderId(Long.parseLong(principal.getName()));
        messageDTO.setRecipientId(message.getRecipientId());
        messageDTO.setCntMessage(message.getContent());
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
        
        typing.setUserId(Long.parseLong(principal.getName()));
        typing.setTimestamp(OffsetDateTime.now());
        log.info("User {} is typing in conversation {}", typing.getUserId(), conversationId);
        
        return typing;
    }

    /**
     * Handle message read status updates
     */
    @MessageMapping("/chat.read/{conversationId}")
    public void markAsRead(
            @DestinationVariable String conversationId,
            Principal principal) {
        
        Long userId = Long.parseLong(principal.getName());
        log.info("User {} marked conversation {} as read", userId, conversationId);
        
        // Mark messages as read
        chatService.markMessagesAsRead(conversationId, userId);
        
        // Notify sender that messages were read
        ReadStatusDTO readStatus = new ReadStatusDTO();
        readStatus.setConversationId(conversationId);
        readStatus.setReadByUserId(userId);
        readStatus.setTimestamp(OffsetDateTime.now());
        
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId + "/read", readStatus);
    }

    /**
     * Handle user online/offline status
     */
    @MessageMapping("/user.status")
    @SendTo("/topic/user-status")
    public UserStatusDTO updateUserStatus(@Payload UserStatusDTO status, Principal principal) {
        status.setUserId(Long.parseLong(principal.getName()));
        status.setTimestamp(OffsetDateTime.now());
        log.info("User {} status updated to {}", status.getUserId(), status.getStatus());
        
        return status;
    }
}