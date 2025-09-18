package MyApp.BE.service.message;

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

import java.util.List;

@Service
public class MessageService implements IMessageService {

    private final IMessageRepository messageRepository;
    private final IUserRepository userRepository;
    private final MessageMapper messageMapper;
    private final IZoneTimeService zoneTimeService;

    @Autowired
    public MessageService(IMessageRepository messageRepository,
                          MessageMapper messageMapper,
                          IUserRepository userRepository,
                          IZoneTimeService zoneTimeService ) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.userRepository = userRepository;
        this.zoneTimeService = zoneTimeService;
    }

    public List<MessageDTO> getConversationById(String conversationId) {
        return messageMapper.toDTOs(messageRepository.findByConversationId(conversationId));
    }



    public void sendMessage(MessageDTO messageDTO) {
        messageRepository.save(messageMapper.toEntity(messageDTO));
    }

    public ResponseEntity<MessageDTO> createMessage(MessageDTO messageDTO) {
        if (messageDTO.getCntMessage() == null || messageDTO.getCntMessage().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        UserEntity sender = userRepository.findById(messageDTO.getSenderId()).orElseThrow(() -> new RuntimeException("Sender not found"));
        UserEntity recipient = userRepository.findById(messageDTO.getRecipientId()).orElseThrow(() -> new RuntimeException("Recipient not found"));

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMsgSender(sender);
        messageEntity.setMsgRecipient(recipient);
        messageEntity.setCntMessage(messageDTO.getCntMessage().trim());
        messageEntity.setTimeStamp(zoneTimeService.setZoneTime("Europe/Prague"));
        messageEntity.setConversationId(generateCanonicalConversationId(messageDTO.getSenderId(),
                                                                        messageDTO.getRecipientId()));
        MessageEntity savedMessageEntity = messageRepository.save(messageEntity);
        MessageDTO responseMessageDTO = messageMapper.toDTO(savedMessageEntity);

        return new ResponseEntity<>(responseMessageDTO, HttpStatus.CREATED);
    }
    private String generateCanonicalConversationId(Long id1, Long id2) {
        if (id1.compareTo(id2) < 0) {
            return String.format("%d_%d", id1, id2);
        } else {
            return String.format("%d_%d", id2, id1);
        }
    }
}