
package MyApp.BE.service.message;

import MyApp.BE.dto.MessageDTO;

import java.util.List;

public interface IMessageService {

    void sendMessage(MessageDTO massageDTO);

    List<MessageDTO> getConversationById(String conversationId);

}

