package MyApp.BE.controller;

import MyApp.BE.dto.MessageDTO;
import MyApp.BE.dto.ErrorDTO;
import MyApp.BE.service.Message.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Get all messages in a conversation
     */
    @GetMapping("/findConversation-id/{conversationId}")
    public ResponseEntity<?> findConversation(@PathVariable @NotBlank String conversationId) {
        try {
            List<MessageDTO> messages = messageService.getConversationById(conversationId);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Neplatné ID konverzace: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst konverzaci: " + e.getMessage()));
        }
    }

    /**
     * Send a new message
     */
    @PostMapping("/msgr-sn")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody MessageDTO messageDTO) {
        try {
            return messageService.createMessage(messageDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Neplatná zpráva: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se odeslat zprávu: " + e.getMessage()));
        }
    }

    /**
     * Get all conversation IDs for a user
     */
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<?> getUserConversations(@PathVariable @NotNull Long userId) {
        try {
            List<String> conversationIds = messageService.getUserConversationIds(userId);
            return ResponseEntity.ok(conversationIds);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Neplatné ID uživatele: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst konverzace: " + e.getMessage()));
        }
    }

    /**
     * Check if conversation exists between two users
     */
    @GetMapping("/conversation-exists/{userId1}/{userId2}")
    public ResponseEntity<?> conversationExists(@PathVariable @NotNull Long userId1, 
                                               @PathVariable @NotNull Long userId2) {
        try {
            boolean exists = messageService.conversationExists(userId1, userId2);
            return ResponseEntity.ok(exists);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Neplatné ID uživatelů: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se ověřit konverzaci: " + e.getMessage()));
        }
    }

    /**
     * Get message count in conversation
     */
    @GetMapping("/conversation/{conversationId}/count")
    public ResponseEntity<?> getMessageCount(@PathVariable @NotBlank String conversationId) {
        try {
            long count = messageService.getMessageCount(conversationId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst počet zpráv: " + e.getMessage()));
        }
    }

    /**
     * Delete conversation
     */
    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity<?> deleteConversation(@PathVariable @NotBlank String conversationId) {
        try {
            messageService.deleteConversation(conversationId);
            return ResponseEntity.ok("Konverzace byla smazána");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se smazat konverzaci: " + e.getMessage()));
        }
    }

    /**
     * Get paginated messages for conversation
     */
    @GetMapping("/conversation/{conversationId}/messages")
    public ResponseEntity<?> getConversationMessages(
            @PathVariable @NotBlank String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            List<MessageDTO> messages = messageService.getConversationMessages(conversationId, page, size);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Neplatné parametry: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst zprávy: " + e.getMessage()));
        }
    }
}