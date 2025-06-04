
package MyApp.service.Message;

import MyApp.dto.MessageDTO;

import java.util.List;

public interface IMessageService {

    void sendMessage(MessageDTO massageDTO);

    List<MessageDTO> getConversationById(int conversationId);

}

