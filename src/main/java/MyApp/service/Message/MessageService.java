package MyApp.service.Message;

import MyApp.dto.MessageDTO;
import MyApp.dto.mapper.MessageMapper;
import MyApp.dto.mapper.UserMapper;
import MyApp.entity.MessageEntity;
import MyApp.entity.UserEntity;
import MyApp.entity.repository.IMessageRepository;
import MyApp.entity.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService implements IMessageService {

    private final IMessageRepository messageRepository;
    private final IUserRepository userRepository;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Autowired
    public MessageService(IMessageRepository messageRepository,
                          MessageMapper messageMapper,
                          UserMapper userMapper,
                          IUserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    public List<MessageDTO> getConversationById(String conversationId) {
        List<MessageEntity> messageEntities = messageRepository.findByConversationId(conversationId);
        return messageEntities.stream()
                .map(messageMapper::toDTO)
                .collect(Collectors.toList());
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
        messageEntity.setConversationId(generateCanonicalConversationId(messageDTO.getSenderId(), messageDTO.getRecipientId()));
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