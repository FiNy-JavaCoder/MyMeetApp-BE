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

    public List<MessageDTO> getConversationById(int conversationId) {
        List<MessageEntity> messageEntities = messageRepository.findByConversationId(conversationId);
        return messageEntities.stream()
                .map(messageMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void sendMessage(MessageDTO messageDTO) {
        messageRepository.save(messageMapper.toEntity(messageDTO));
    }

    public ResponseEntity<MessageDTO> createMessage(MessageDTO messageDTO) {
        MessageDTO message = new MessageDTO();
        UserEntity sender = userRepository.findById(messageDTO.getSenderId()).orElseThrow(() -> new RuntimeException("Sender not found"));
        UserEntity recipient = userRepository.findById(messageDTO.getRecipientId()).orElseThrow(() -> new RuntimeException("Recipient not found"));
        if (messageDTO.getCntMessage() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        else {
            message.setSenderId(messageDTO.getSenderId());
            message.setRecipientId(messageDTO.getRecipientId());
            message.setCntMessage(messageDTO.getCntMessage().trim());
            message.setConversationId(String.format("%.0f_%.0f", messageDTO.getSenderId(), messageDTO.getRecipientId()));
          MessageEntity savedMessage = messageRepository.save(messageMapper.toEntity(message));
            MessageDTO responseMessageDTO = new MessageDTO();
            responseMessageDTO.setSenderId(sender.getUserId());
            responseMessageDTO.setRecipientId(recipient.getUserId());
            responseMessageDTO.setCntMessage(savedMessage.getCntMessage());
            responseMessageDTO.setConversationId(savedMessage.getConversationId());
            return new ResponseEntity<>(responseMessageDTO, HttpStatus.CREATED);
        }


    }
}