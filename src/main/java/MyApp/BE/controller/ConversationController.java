package MyApp.BE.controller;

import MyApp.BE.dto.ConversationDTO;
import MyApp.BE.dto.ErrorDTO;
import MyApp.BE.service.Conversation.ConversationService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    /**
     * Get all conversations for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserConversations(@PathVariable @NotNull Long userId) {
        try {
            List<ConversationDTO> conversations = conversationService.getUserConversations(userId);
            return ResponseEntity.ok(conversations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorDTO("Neplatné ID uživatele: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst konverzace: " + e.getMessage()));
        }
    }

    /**
     * Mark conversation as read
     */
    @PutMapping("/{conversationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String conversationId,
                                       @RequestParam @NotNull Long userId) {
        try {
            conversationService.markConversationAsRead(conversationId, userId);
            return ResponseEntity.ok("Konverzace označena jako přečtená");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se označit konverzaci jako přečtenou: " + e.getMessage()));
        }
    }

    /**
     * Get conversation statistics
     */
    @GetMapping("/{conversationId}/stats")
    public ResponseEntity<?> getConversationStats(@PathVariable String conversationId) {
        try {
            Map<String, Object> stats = conversationService.getConversationStats(conversationId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst statistiky: " + e.getMessage()));
        }
    }
}