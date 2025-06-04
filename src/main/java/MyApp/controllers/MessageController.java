package MyApp.controllers;

import MyApp.dto.MessageDTO;
import MyApp.service.Message.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/findConversation-id")
    public List<MessageDTO> findConversation(@PathVariable int conversationId) {
        return messageService.getConversationById(conversationId);
    }
    @PostMapping("/msgr-sn")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO messageDTO) {
       return messageService.createMessage(messageDTO);

    }
}
