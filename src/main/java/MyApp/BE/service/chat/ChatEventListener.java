package MyApp.BE.service.chat;

import MyApp.BE.dto.MessageDTO;
import MyApp.BE.dto.UserStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatEventListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleMessageSent(MessageSentEvent event) {
        MessageDTO message = event.getMessage();
        
        // Send notification to recipient
        messagingTemplate.convertAndSendToUser(
            message.getRecipientId().toString(),
            "/queue/notifications",
            "New message from " + message.getSenderId()
        );
    }

    @EventListener
    public void handleUserStatusChange(UserStatusChangeEvent event) {
        UserStatusDTO status = event.getStatus();
        
        // Broadcast status change
        messagingTemplate.convertAndSend("/topic/user-status", status);
    }
}