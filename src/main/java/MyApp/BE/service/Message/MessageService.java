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
    public List<MessageDTO> getConversationById(String conversationId) {
        List<MessageEntity> messages = messageRepository.findByConversationId(conversationId);
        return messageMapper.toDTOs(messages);
    }

    @Override
    public void sendMessage(MessageDTO messageDTO) {
        MessageEntity entity = messageMapper.toEntity(messageDTO);
        messageRepository.save(entity);
    }

    public ResponseEntity<MessageDTO> createMessage(MessageDTO messageDTO) {
        try {
            // Validace zprávy
            if (messageDTO.getCntMessage() == null || 
                messageDTO.getCntMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            if (messageDTO.getSenderId() == null || messageDTO.getRecipientId() == null) {
                return ResponseEntity.badRequest().build();
            }

            // Ověření existence uživatelů
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

            // Vytvoření nové zprávy
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMsgSender(sender);
            messageEntity.setMsgRecipient(recipient);
            messageEntity.setCntMessage(messageDTO.getCntMessage().trim());
            messageEntity.setTimeStamp(zoneTimeService.setZoneTime("Europe/Prague"));
            messageEntity.setConversationId(generateCanonicalConversationId(
                messageDTO.getSenderId(), messageDTO.getRecipientId()));

            MessageEntity savedMessageEntity = messageRepository.save(messageEntity);
            MessageDTO responseMessageDTO = messageMapper.toDTO(savedMessageEntity);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessageDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String generateCanonicalConversationId(Long id1, Long id2) {
        if (id1.compareTo(id2) < 0) {
            return String.format("%d_%d", id1, id2);
        } else {
            return String.format("%d_%d", id2, id1);
        }
    }

    public List<MessageDTO> getRecentConversations(Long userId) {
        // Implementujte logiku pro získání posledních konverzací uživatele
        // Toto bude potřebovat komplexnější SQL dotaz
        return List.of(); // Placeholder
    }
}
