package MyApp.BE.service.Conversation;

import MyApp.BE.dto.ConversationDTO;
import MyApp.BE.dto.MessageDTO;
import MyApp.BE.entity.MessageEntity;
import MyApp.BE.entity.UserEntity;
import MyApp.BE.entity.repository.IMessageRepository;
import MyApp.BE.entity.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class ConversationService {

    private final IMessageRepository messageRepository;
    private final IUserRepository userRepository;

    @Autowired
    public ConversationService(IMessageRepository messageRepository, IUserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all conversations for a user with their latest messages
     */
    @Transactional(readOnly = true)
    public List<ConversationDTO> getUserConversations(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        // Get latest messages from all conversations
        List<MessageEntity> latestMessages = messageRepository.findLatestMessagesByUserId(userId);
        List<ConversationDTO> conversations = new ArrayList<>();

        for (MessageEntity message : latestMessages) {
            ConversationDTO conversation = createConversationDTO(message, userId);
            conversations.add(conversation);
        }

        return conversations;
    }

    /**
     * Create ConversationDTO from MessageEntity
     */
    private ConversationDTO createConversationDTO(MessageEntity message, Long currentUserId) {
        ConversationDTO conversation = new ConversationDTO();
        conversation.setConversationId(message.getConversationId());
        
        // Determine who is the "other" user in conversation
        UserEntity otherUser;
        if (message.getMsgSender().getUserId().equals(currentUserId)) {
            otherUser = message.getMsgRecipient();
        } else {
            otherUser = message.getMsgSender();
        }
        
        conversation.setOtherUserId(otherUser.getUserId());
        conversation.setOtherUserNickName(otherUser.getNickName());
        conversation.setOtherUserProfilePicture(getProfilePictureUrl(otherUser));
        conversation.setLastMessage(message.getCntMessage());
        conversation.setLastMessageTime(message.getTimeStamp());
        conversation.setUnreadCount(getUnreadCount(message.getConversationId(), currentUserId));
        conversation.setOnline(false); // TODO: Implement online status
        
        return conversation;
    }

    /**
     * Get profile picture URL for user
     */
    private String getProfilePictureUrl(UserEntity user) {
        if (user.getUserProfileEntity() != null && 
            user.getUserProfileEntity().getProfilePictureUrl() != null) {
            return user.getUserProfileEntity().getProfilePictureUrl();
        }
        return null;
    }

    /**
     * Get unread message count for conversation
     */
    private Long getUnreadCount(String conversationId, Long userId) {
        // TODO: Implement unread count logic
        // This would require additional field in MessageEntity or separate table
        return 0L;
    }

    /**
     * Mark conversation as read
     */
    @Transactional
    public void markConversationAsRead(String conversationId, Long userId) {
        // TODO: Implement mark as read functionality
        // This would require additional field in MessageEntity for read status
    }

    /**
     * Get conversation statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getConversationStats(String conversationId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("messageCount", messageRepository.countByConversationId(conversationId));
        stats.put("conversationId", conversationId);
        return stats;
    }
}