package MyApp.BE.service.Message;

import MyApp.BE.dto.MessageDTO;
import MyApp.BE.dto.mapper.MessageMapper;
import MyApp.BE.service.ZoneTime.IZoneTimeService;
import MyApp.BE.entity.MessageEntity;
import MyApp.BE.entity.UserEntity;
import MyApp.BE.entity.repository.IMessageRepository;
import MyApp.BE.entity.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MessageService implements IMessageService {

    private final IMessageRepository messageRepository;
    private final IUserRepository userRepository;
    private final MessageMapper messageMapper;
    private final IZoneTimeService zoneTimeService;

    @Autowired
    public MessageService(IMessageRepository messageRepository,
                          MessageMapper messageMapper,
                          IUserRepository userRepository,
                          IZoneTimeService zoneTimeService) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.userRepository = userRepository;
        this.zoneTimeService = zoneTimeService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDTO> getConversationById(String conversationId) {
        if (conversationId == null || conversationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Conversation ID cannot be null or empty");
        }
        
        List<MessageEntity> messages = messageRepository.findByConversationId(conversationId);
        return messageMapper.toDTOs(messages);
    }

    @Override
    @Transactional
    public void sendMessage(MessageDTO messageDTO) {
        if (messageDTO == null) {
            throw new IllegalArgumentException("Message DTO cannot be null");
        }
        
        // Validate required fields
        if (messageDTO.getSenderId() == null || messageDTO.getRecipientId() == null) {
            throw new IllegalArgumentException("Sender ID and Recipient ID are required");
        }
        
        if (messageDTO.getCntMessage() == null || messageDTO.getCntMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        // Get users
        UserEntity sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found with ID: " + messageDTO.getSenderId()));
        
        UserEntity recipient = userRepository.findById(messageDTO.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found with ID: " + messageDTO.getRecipientId()));

        // Create message entity
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMsgSender(sender);
        messageEntity.setMsgRecipient(recipient);
        messageEntity.setCntMessage(messageDTO.getCntMessage().trim());
        messageEntity.setTimeStamp(zoneTimeService.setZoneTime("Europe/Prague"));
        messageEntity.setConversationId(generateCanonicalConversationId(
                messageDTO.getSenderId(), messageDTO.getRecipientId()));

        messageRepository.save(messageEntity);
    }

    @Transactional
    public ResponseEntity<MessageDTO> createMessage(MessageDTO messageDTO) {
        try {
            // Validation
            if (messageDTO == null) {
                return ResponseEntity.badRequest().build();
            }

            if (messageDTO.getCntMessage() == null || messageDTO.getCntMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            if (messageDTO.getSenderId() == null || messageDTO.getRecipientId() == null) {
                return ResponseEntity.badRequest().build();
            }

            // Check if users exist
            Optional<UserEntity> senderOpt = userRepository.findById(messageDTO.getSenderId());
            Optional<UserEntity> recipientOpt = userRepository.findById(messageDTO.getRecipientId());

            if (senderOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            if (recipientOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            UserEntity sender = senderOpt.get();
            UserEntity recipient = recipientOpt.get();

            // Create message entity
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMsgSender(sender);
            messageEntity.setMsgRecipient(recipient);
            messageEntity.setCntMessage(messageDTO.getCntMessage().trim());
            messageEntity.setTimeStamp(zoneTimeService.setZoneTime("Europe/Prague"));
            messageEntity.setConversationId(generateCanonicalConversationId(
                    messageDTO.getSenderId(), messageDTO.getRecipientId()));

            // Save message
            MessageEntity savedMessageEntity = messageRepository.save(messageEntity);
            
            // Convert back to DTO
            MessageDTO responseMessageDTO = messageMapper.toDTO(savedMessageEntity);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessageDTO);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate canonical conversation ID from two user IDs
     * Always puts smaller ID first to ensure consistency
     */
    private String generateCanonicalConversationId(Long id1, Long id2) {
        if (id1 == null || id2 == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }
        
        if (id1.equals(id2)) {
            throw new IllegalArgumentException("Cannot create conversation with same user");
        }
        
        if (id1.compareTo(id2) < 0) {
            return String.format("%d_%d", id1, id2);
        } else {
            return String.format("%d_%d", id2, id1);
        }
    }

    /**
     * Get recent conversations for a user
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getRecentConversations(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        // This would require a more complex query to get the latest message from each conversation
        // For now, return empty list - implement based on your needs
        return List.of();
    }

    /**
     * Get all conversations for a user
     */
    @Transactional(readOnly = true)
    public List<String> getUserConversationIds(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        // Get all unique conversation IDs where user is sender or recipient
        return messageRepository.findDistinctConversationIdsByUserId(userId);
    }

    /**
     * Check if conversation exists between two users
     */
    @Transactional(readOnly = true)
    public boolean conversationExists(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) {
            return false;
        }
        
        String conversationId = generateCanonicalConversationId(userId1, userId2);
        return messageRepository.existsByConversationId(conversationId);
    }

    /**
     * Get message count in conversation
     */
    @Transactional(readOnly = true)
    public long getMessageCount(String conversationId) {
        if (conversationId == null || conversationId.trim().isEmpty()) {
            return 0;
        }
        
        return messageRepository.countByConversationId(conversationId);
    }

    /**
     * Delete conversation
     */
    @Transactional
    public void deleteConversation(String conversationId) {
        if (conversationId != null && !conversationId.trim().isEmpty()) {
            messageRepository.deleteByConversationId(conversationId);
        }
    }

    /**
     * Get paginated messages for conversation
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> getConversationMessages(String conversationId, int page, int size) {
        if (conversationId == null || conversationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Conversation ID cannot be null or empty");
        }
        
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid page parameters");
        }
        
        // For now, return all messages - implement pagination as needed
        List<MessageEntity> messages = messageRepository.findByConversationIdOrderByTimeStampDesc(conversationId);
        return messageMapper.toDTOs(messages);
    }
}