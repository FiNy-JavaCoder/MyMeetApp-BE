
// Aktualizovaný MessageController s lepším error handlingem
// src/main/java/MyApp/BE/controller/MessageController.java
package MyApp.BE.controller;

import MyApp.BE.dto.MessageDTO;
import MyApp.BE.dto.ErrorDTO;
import MyApp.BE.service.Message.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/findConversation-id/{conversationId}")
    public ResponseEntity<?> findConversation(@PathVariable String conversationId) {
        try {
            List<MessageDTO> messages = messageService.getConversationById(conversationId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst konverzaci"));
        }
    }

    @PostMapping("/msgr-sn")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDTO messageDTO) {
        try {
            ResponseEntity<MessageDTO> result = messageService.createMessage(messageDTO);
            return result;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se odeslat zprávu"));
        }
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<?> getUserConversations(@PathVariable Long userId) {
        try {
            // Implementujte logiku pro získání všech konverzací uživatele
            // List<ConversationDTO> conversations = messageService.getUserConversations(userId);
            // return ResponseEntity.ok(conversations);
            return ResponseEntity.ok().build(); // Placeholder
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst konverzace"));
        }
    }
}