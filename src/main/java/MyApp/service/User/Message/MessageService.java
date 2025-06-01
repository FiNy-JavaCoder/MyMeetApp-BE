package MyApp.service.User.Message;

import MyApp.dto.MessageDTO;
import MyApp.dto.mapper.MessageMapper;
import MyApp.entity.MessageEntity;
import MyApp.entity.repository.IMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService implements IMessageService {

    private final IMessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Autowired
    public MessageService(IMessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
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

}