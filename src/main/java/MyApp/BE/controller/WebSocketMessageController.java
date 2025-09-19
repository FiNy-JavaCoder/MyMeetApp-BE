package MyApp.BE.controller;

import MyApp.BE.dto.MessageDTO;
import MyApp.BE.service.Message.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketMessageController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    /**
     * Handle private messages
     */
    @MessageMapping("/private-message")
    public void sendPrivateMessage(@Payload MessageDTO messageDTO, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Save message to database
            messageService.sendMessage(messageDTO);
            
            // Send message to recipient via WebSocket
            messagingTemplate.convertAndSendToUser(
                messageDTO.getRecipientId().toString(),
                "/queue/messages",
                messageDTO
            );
            
            // Send confirmation to sender
            messagingTemplate.convertAndSendToUser(
                messageDTO.getSenderId().toString(),
                "/queue/message-sent",
                messageDTO
            );
            
        } catch (Exception e) {
            // Send error to sender
            messagingTemplate.convertAndSendToUser(
                messageDTO.getSenderId().toString(),
                "/queue/errors",
                "Failed to send message: " + e.getMessage()
            );
        }
    }

    /**
     * Handle typing indicators
     */
    @MessageMapping("/typing")
    public void handleTyping(@Payload TypingIndicatorDTO typingDTO) {
        messagingTemplate.convertAndSendToUser(
            typingDTO.getRecipientId().toString(),
            "/queue/typing",
            typingDTO
        );
    }

    /**
     * Handle user status updates
     */
    @MessageMapping("/status")
    public void handleStatusUpdate(@Payload UserStatusDTO statusDTO) {
        // Broadcast status to all relevant users
        messagingTemplate.convertAndSend("/topic/user-status", statusDTO);
    }
}