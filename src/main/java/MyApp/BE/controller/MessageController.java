package MyApp.BE.controller;

import MyApp.BE.dto.MessageDTO;
import MyApp.BE.service.message.MessageService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/findConversation-id/{conversationId}")
    public List<MessageDTO> findConversation(@PathVariable String conversationId) {
        return messageService.getConversationById(conversationId);
    }

    @PostMapping("/msgr-sn")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO messageDTO) {
        return messageService.createMessage(messageDTO);

    }
}
