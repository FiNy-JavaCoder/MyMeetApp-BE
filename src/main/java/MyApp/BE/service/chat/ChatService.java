package MyApp.BE.service.chat;

import MyApp.BE.dto.ConversationSummaryDTO;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
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
    @CacheEvict(value = {"conversations", "messageCount"}, allEntries = true)
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        log.info("Sending message from user {} to user {}",
                messageDTO.getSenderId(), messageDTO.getRecipientId());

        // Validate sender and recipient exist
        UserEntity sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found with ID: " + messageDTO.getSenderId()));

        UserEntity recipient = userRepository.findById(messageDTO.getRecipientId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found with ID: " + messageDTO.getRecipientId()));

        // Validate that sender and recipient are different
        if (sender.getUserId().equals(recipient.getUserId())) {
            throw new IllegalArgumentException("Cannot send message to yourself");
        }

        // Create message entity
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMsgSender(sender);
        messageEntity.setMsgRecipient(recipient);
        messageEntity.setCntMessage(messageDTO.getCntMessage());
        messageEntity.setTimeStamp(OffsetDateTime.now());
        messageEntity.setRead(false);
        messageEntity.setDeletedBySender(false);
        messageEntity.setDeletedByRecipient(false);

        // Generate conversation ID if not provided
        String conversationId = messageDTO.getConversationId();
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = generateConversationId(messageDTO.getSenderId(), messageDTO.getRecipientId());
        }
        messageEntity.setConversationId(conversationId);

        // Save message
        MessageEntity savedMessage = messageRepository.save(messageEntity);
        log.info("Message saved with ID: {}", savedMessage.getMessageId());

        // Convert to DTO and return
        return messageMapper.toDTO(savedMessage);
    }

    /**
     * Get conversation between two users
     */
    @Cacheable(value = "conversations", key = "#userId1 + '_' + #userId2")
    @Transactional(readOnly = true)
    public List<MessageDTO> getConversation(Long userId1, Long userId2) {
        log.info("Fetching conversation between users {} and {}", userId1, userId2);

        // Validate users exist
        if (!userRepository.existsById(userId1)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId1);
        }
        if (!userRepository.existsById(userId2)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId2);
        }

        List<MessageEntity> messages = messageRepository.findMessagesBetweenUsers(userId1, userId2);

        // Mark messages as read if current user is recipient
        List<MessageEntity> messagesToUpdate = messages.stream()
                .filter(msg -> msg.getMsgRecipient().getUserId().equals(userId1) && !msg.isRead())
                .collect(Collectors.toList());

        if (!messagesToUpdate.isEmpty()) {
            messagesToUpdate.forEach(msg -> msg.setRead(true));
            messageRepository.saveAll(messagesToUpdate);
        }

        return messageMapper.toDTOs(messages);
    }

    /**
     * Get messages by conversation ID
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getMessagesByConversationId(String conversationId, Long requestingUserId) {
        log.info("Fetching messages for conversation {} by user {}", conversationId, requestingUserId);

        if (conversationId == null || conversationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Conversation ID cannot be null or empty");
        }

        if (!userRepository.existsById(requestingUserId)) {
            throw new ResourceNotFoundException("User not found with ID: " + requestingUserId);
        }

        List<MessageEntity> messages = messageRepository.findByConversationIdAndUserId(conversationId, requestingUserId);

        // Verify that requesting user is part of this conversation
        if (!messages.isEmpty()) {
            MessageEntity firstMessage = messages.get(0);
            if (!firstMessage.getMsgSender().getUserId().equals(requestingUserId) &&
                    !firstMessage.getMsgRecipient().getUserId().equals(requestingUserId)) {
                throw new UnauthorizedException("You are not authorized to view this conversation");
            }
        }

        // Mark messages as read
        List<MessageEntity> messagesToUpdate = messages.stream()
                .filter(msg -> msg.getMsgRecipient().getUserId().equals(requestingUserId) && !msg.isRead())
                .collect(Collectors.toList());

        if (!messagesToUpdate.isEmpty()) {
            messagesToUpdate.forEach(msg -> msg.setRead(true));
            messageRepository.saveAll(messagesToUpdate);
        }

        return messageMapper.toDTOs(messages);
    }

    /**
     * Get recent conversations for a user
     */
    @Cacheable(value = "conversations", key = "'recent_' + #userId")
    @Transactional(readOnly = true)
    public List<ConversationSummaryDTO> getRecentConversations(Long userId) {
        log.info("Fetching recent conversations for user {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        List<MessageEntity> recentMessages = messageRepository.findRecentMessagesForUser(userId);

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
                        // Add profile picture if available
                        if (lastMessage.getMsgRecipient().getUserProfileEntity() != null) {
                            summary.setOtherUserProfilePicture(
                                    lastMessage.getMsgRecipient().getUserProfileEntity().getProfilePictureUrl());
                        }
                    } else {
                        summary.setOtherUserId(lastMessage.getMsgSender().getUserId());
                        summary.setOtherUserNickname(lastMessage.getMsgSender().getNickName());
                        // Add profile picture if available
                        if (lastMessage.getMsgSender().getUserProfileEntity() != null) {
                            summary.setOtherUserProfilePicture(
                                    lastMessage.getMsgSender().getUserProfileEntity().getProfilePictureUrl());
                        }
                    }

                    // Count unread messages in this conversation
                    long unreadCount = entry.getValue().stream()
                            .filter(msg -> msg.getMsgRecipient().getUserId().equals(userId) && !msg.isRead())
                            .count();
                    summary.setUnreadCount(unreadCount);

                    // For now, set isOnline to false - you can implement real online status later
                    summary.setOnline(false);

                    return summary;
                })
                .sorted((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()))
                .collect(Collectors.toList());
    }

    /**
     * Mark messages as read
     */
    @CacheEvict(value = {"conversations", "messageCount"}, allEntries = true)
    public void markMessagesAsRead(String conversationId, Long userId) {
        log.info("Marking messages as read in conversation {} for user {}", conversationId, userId);

        if (conversationId == null || conversationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Conversation ID cannot be null or empty");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        int updatedCount = messageRepository.markMessagesAsReadInConversation(conversationId, userId);
        log.info("Marked {} messages as read in conversation {} for user {}", updatedCount, conversationId, userId);
    }

    /**
     * Delete a message for a user
     */
    @CacheEvict(value = {"conversations", "messageCount"}, allEntries = true)
    public void deleteMessage(Long messageId, Long userId) {
        log.info("Deleting message {} for user {}", messageId, userId);

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

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
            log.info("Message {} completely deleted from database", messageId);
        } else {
            messageRepository.save(message);
            log.info("Message {} marked as deleted for user {}", messageId, userId);
        }
    }

    /**
     * Edit a message
     */
    @CacheEvict(value = {"conversations", "messageCount"}, allEntries = true)
    public MessageDTO editMessage(Long messageId, String newContent, Long userId) {
        log.info("Editing message {} by user {}", messageId, userId);

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

        // Only sender can edit
        if (!message.getMsgSender().getUserId().equals(userId)) {
            throw new UnauthorizedException("Only sender can edit the message");
        }

        // Check if message is too old to edit (e.g., 24 hours)
        if (message.getTimeStamp().plusHours(24).isBefore(OffsetDateTime.now())) {
            throw new UnauthorizedException("Message is too old to edit");
        }

        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        message.setCntMessage(newContent.trim());
        message.setEditedAt(OffsetDateTime.now());

        MessageEntity savedMessage = messageRepository.save(message);
        log.info("Message {} edited successfully", messageId);
        
        return messageMapper.toDTO(savedMessage);
    }

    /**
     * Get unread message count for a user
     */
    @Cacheable(value = "messageCount", key = "'unread_' + #userId")
    @Transactional(readOnly = true)
    public Long getUnreadMessageCount(Long userId) {
        log.debug("Getting unread message count for user {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        return messageRepository.countUnreadMessagesForUser(userId);
    }

    /**
     * Get new messages since a timestamp (for polling/real-time updates)
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getNewMessages(String conversationId, OffsetDateTime lastCheckTime, Long userId) {
        log.debug("Getting new messages in conversation {} since {} for user {}", 
                 conversationId, lastCheckTime, userId);

        if (conversationId == null || conversationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Conversation ID cannot be null or empty");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        List<MessageEntity> newMessages = messageRepository.findNewMessagesInConversation(
                conversationId, lastCheckTime, userId);

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
     * Get paginated messages for conversation
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getConversationMessages(String conversationId, Long userId, int page, int size) {
        log.debug("Getting paginated messages for conversation {} page {} size {} user {}", 
                 conversationId, page, size, userId);

        if (conversationId == null || conversationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Conversation ID cannot be null or empty");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<MessageEntity> messagesPage = messageRepository.findByConversationIdAndUserIdPaged(
                conversationId, userId, pageable);

        return messageMapper.toDTOs(messagesPage.getContent());
    }

    /**
     * Check if conversation exists between two users
     */
    @Transactional(readOnly = true)
    public boolean conversationExists(Long userId1, Long userId2) {
        log.debug("Checking if conversation exists between users {} and {}", userId1, userId2);

        if (userId1 == null || userId2 == null) {
            return false;
        }

        String conversationId = generateConversationId(userId1, userId2);
        return messageRepository.existsByConversationId(conversationId);
    }

    /**
     * Get conversation ID for two users
     */
    @Transactional(readOnly = true)
    public String getConversationId(Long userId1, Long userId2) {
        log.debug("Getting conversation ID between users {} and {}", userId1, userId2);

        if (userId1 == null || userId2 == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }

        if (!userRepository.existsById(userId1)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId1);
        }

        if (!userRepository.existsById(userId2)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId2);
        }

        return generateConversationId(userId1, userId2);
    }

    /**
     * Helper method to generate conversation ID
     */
    private String generateConversationId(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }
        
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Cannot create conversation with same user");
        }

        return userId1 < userId2 ? 
                String.format("%d_%d", userId1, userId2) : 
                String.format("%d_%d", userId2, userId1);
    }
}