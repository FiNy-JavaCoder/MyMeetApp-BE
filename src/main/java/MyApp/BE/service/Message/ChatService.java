package MyApp.BE.service.message;

import MyApp.BE.dto.MessageDTO;
import MyApp.BE.dto.mapper.MessageMapper;
import MyApp.BE.entity.MessageEntity;
import MyApp.BE.entity.UserEntity;
import MyApp.BE.exception.ResourceNotFoundException;
import MyApp.BE.exception.UnauthorizedException;
import MyApp.BE.entity.repository.IMessageRepository;
import MyApp.BE.entity.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatService {

    private final IMessageRepository messageRepository;
    private final IUserRepository userRepository;
    private final MessageMapper messageMapper;

    /**
     * Send a new message
     */
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        log.info("Sending message from user {} to user {}",
                messageDTO.getSenderId(), messageDTO.getRecipientId());

        // Validate sender and recipient exist
        UserEntity sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        UserEntity recipient = userRepository.findById(messageDTO.getRecipientId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        // Check if users are not blocked (you can implement blocking logic)
        // checkIfUsersBlocked(sender, recipient);

        // Create message entity
        MessageEntity messageEntity = messageMapper.toEntity(messageDTO);
        messageEntity.setMsgSender(sender);
        messageEntity.setMsgRecipient(recipient);
        messageEntity.setTimeStamp(OffsetDateTime.now());
        messageEntity.setRead(false);

        // Generate conversation ID if not provided
        if (messageDTO.getConversationId() == null || messageDTO.getConversationId().isEmpty()) {
            messageEntity.setConversationId(generateConversationId(
                        messageDTO.getSenderId(), messageDTO.getRecipientId()));
        } else {
            messageEntity.setConversationId(messageDTO.getConversationId());
        }

        // Save message
        MessageEntity savedMessage = messageRepository.save(messageEntity);

        // Convert to DTO and return
        return messageMapper.toDTO(savedMessage);
    }

    /**
     * Get conversation between two users
     */
    public List<MessageDTO> getConversation(Long userId1, Long userId2) {
        log.info("Fetching conversation between users {} and {}", userId1, userId2);

        List<MessageEntity> messages = messageRepository.findMessagesBetweenUsers(userId1, userId2);

        // Mark messages as read if current user is recipient
        messages.forEach(msg -> {
            if (msg.getMsgRecipient().getUserId().equals(userId1) && !msg.isRead()) {
                msg.setRead(true);
            }
        });

        return messageMapper.toDTOs(messages);
    }

    /**
     * Get messages by conversation ID
     */
    public List<MessageDTO> getMessagesByConversationId(String conversationId, Long requestingUserId) {
        log.info("Fetching messages for conversation {}", conversationId);

        List<MessageEntity> messages = messageRepository.findByConversationId(conversationId);

        // Verify that requesting user is part of this conversation
        if (!messages.isEmpty()) {
            MessageEntity firstMessage = messages.get(0);
            if (!firstMessage.getMsgSender().getUserId().equals(requestingUserId) &&
                        !firstMessage.getMsgRecipient().getUserId().equals(requestingUserId)) {
                throw new UnauthorizedException("You are not authorized to view this conversation");
            }
        }

        // Mark messages as read
        messages.forEach(msg -> {
            if (msg.getMsgRecipient().getUserId().equals(requestingUserId) && !msg.isRead()) {
                msg.setRead(true);
            }
        });

        // Filter out deleted messages
        messages = messages.stream()
                .filter(msg -> {
                    if (msg.getMsgSender().getUserId().equals(requestingUserId)) {
                        return !msg.isDeletedBySender();
                    } else {
                        return !msg.isDeletedByRecipient();
                    }
                })
                .collect(Collectors.toList());

        return messageMapper.toDTOs(messages);
    }

    /**
     * Get recent conversations for a user
     */
    public List<ConversationSummaryDTO> getRecentConversations(Long userId) {
        log.info("Fetching recent conversations for user {}", userId);

        List<MessageEntity> recentMessages = messageRepository.findRecentMessagesForUser(userId);

        // Group by conversation and get last message from each
        return recentMessages.stream()
                .collect(Collectors.groupingBy(MessageEntity::getConversationId))
                .entrySet().stream()
                .map(entry -> {
                    MessageEntity lastMessage = entry.getValue().get(0); // Already sorted by timestamp DESC

                    ConversationSummaryDTO summary = new ConversationSummaryDTO();
                    summary.setConversationId(entry.getKey());
                    summary.setLastMessage(lastMessage.getCntMessage());
                    summary.setLastMessageTime(lastMessage.getTimeStamp());

                    // Set other user info
                    if (lastMessage.getMsgSender().getUserId().equals(userId)) {
                        summary.setOtherUserId(lastMessage.getMsgRecipient().getUserId());
                        summary.setOtherUserNickname(lastMessage.getMsgRecipient().getNickName());
                    } else {
                        summary.setOtherUserId(lastMessage.getMsgSender().getUserId());
                        summary.setOtherUserNickname(lastMessage.getMsgSender().getNickName());
                    }

                    // Count unread messages in this conversation
                    long unreadCount = entry.getValue().stream()
                            .filter(msg -> msg.getMsgRecipient().getUserId().equals(userId) && !msg.isRead())
                            .count();
                    summary.setUnreadCount(unreadCount);

                    return summary;
                })
                .sorted((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()))
                .collect(Collectors.toList());
    }

    /**
     * Mark messages as read
     */
    public void markMessagesAsRead(String conversationId, Long userId) {
        log.info("Marking messages as read in conversation {} for user {}", conversationId, userId);

        List<MessageEntity> messages = messageRepository.findByConversationId(conversationId);
        messages.forEach(msg -> {
            if (msg.getMsgRecipient().getUserId().equals(userId) && !msg.isRead()) {
                msg.setRead(true);
            }
        });

        messageRepository.saveAll(messages);
    }

    /**
     * Delete a message for a user
     */
    public void deleteMessage(Long messageId, Long userId) {
        log.info("Deleting message {} for user {}", messageId, userId);

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (message.getMsgSender().getUserId().equals(userId)) {
            message.setDeletedBySender(true);
        } else if (message.getMsgRecipient().getUserId().equals(userId)) {
            message.setDeletedByRecipient(true);
        } else {
            throw new UnauthorizedException("You are not authorized to delete this message");
        }

        // If both users deleted, remove from database
        if (message.isDeletedBySender() && message.isDeletedByRecipient()) {
            messageRepository.delete(message);
        } else {
            messageRepository.save(message);
        }
    }

    /**
     * Edit a message
     */
    public MessageDTO editMessage(Long messageId, String newContent, Long userId) {
        log.info("Editing message {} by user {}", messageId, userId);

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        // Only sender can edit
        if (!message.getMsgSender().getUserId().equals(userId)) {
            throw new UnauthorizedException("Only sender can edit the message");
        }

        // Check if message is too old to edit (e.g., 24 hours)
        if (message.getTimeStamp().plusHours(24).isBefore(OffsetDateTime.now())) {
            throw new UnauthorizedException("Message is too old to edit");
        }

        message.setCntMessage(newContent);
        message.setEditedAt(OffsetDateTime.now());

        MessageEntity savedMessage = messageRepository.save(message);
        return messageMapper.toDTO(savedMessage);
    }

    /**
     * Get unread message count for a user
     */
    public Long getUnreadMessageCount(Long userId) {
        return messageRepository.countUnreadMessagesForUser(userId);
    }

    /**
     * Get new messages since a timestamp (for polling/real-time updates)
     */
    public List<MessageDTO> getNewMessages(String conversationId, OffsetDateTime lastCheckTime, Long userId) {
        List<MessageEntity> newMessages = messageRepository.findNewMessagesInConversation(
                conversationId, lastCheckTime);

        // Verify user is part of conversation
        if (!newMessages.isEmpty()) {
            MessageEntity firstMessage = newMessages.get(0);
            if (!firstMessage.getMsgSender().getUserId().equals(userId) &&
                        !firstMessage.getMsgRecipient().getUserId().equals(userId)) {
                throw new UnauthorizedException("You are not authorized to view this conversation");
            }
        }

        return messageMapper.toDTOs(newMessages);
    }

    /**
     * Helper method to generate conversation ID
     */
    private String generateConversationId(Long userId1, Long userId2) {
        return userId1 < userId2 ?  

// Additional DTO for conversation summary
@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
class ConversationSummaryDTO {
    private String conversationId;
    private Long otherUserId;
    private String otherUserNickname;
    private String lastMessage;
    private OffsetDateTime lastMessageTime;
    private long unreadCount;
}