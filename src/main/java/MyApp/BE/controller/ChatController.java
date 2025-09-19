package MyApp.BE.controller;

import MyApp.BE.dto.ConversationSummaryDTO;
import MyApp.BE.dto.ErrorDTO;
import MyApp.BE.dto.MessageDTO;
import MyApp.BE.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat", description = "Chat messaging endpoints")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    @Operation(summary = "Send a new message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<?> sendMessage(@Valid @RequestBody MessageDTO messageDTO) {
        try {
            MessageDTO sentMessage = chatService.sendMessage(messageDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(sentMessage);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid message data: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        } catch (Exception e) {
            log.error("Error sending message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se odeslat zprávu"));
        }
    }

    @GetMapping("/conversation/{otherUserId}")
    @Operation(summary = "Get conversation with another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<?> getConversation(
            @Parameter(description = "ID of the other user in conversation")
            @PathVariable Long otherUserId,
            @RequestParam Long currentUserId) {
        try {
            List<MessageDTO> messages = chatService.getConversation(currentUserId, otherUserId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Error getting conversation between {} and {}", currentUserId, otherUserId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst konverzaci"));
        }
    }

    @GetMapping("/conversation")
    @Operation(summary = "Get messages by conversation ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<?> getMessagesByConversationId(
            @Parameter(description = "Conversation ID")
            @RequestParam String conversationId,
            @RequestParam Long userId) {
        try {
            List<MessageDTO> messages = chatService.getMessagesByConversationId(conversationId, userId);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting messages for conversation {}", conversationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst zprávy"));
        }
    }

    @GetMapping("/conversations/recent")
    @Operation(summary = "Get recent conversations for the current user")
    @ApiResponse(responseCode = "200", description = "Conversations retrieved successfully")
    public ResponseEntity<?> getRecentConversations(@RequestParam Long userId) {
        try {
            List<ConversationSummaryDTO> conversations = chatService.getRecentConversations(userId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("Error getting recent conversations for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst konverzace"));
        }
    }

    @PutMapping("/mark-read/{conversationId}")
    @Operation(summary = "Mark all messages in a conversation as read")
    @ApiResponse(responseCode = "204", description = "Messages marked as read")
    public ResponseEntity<?> markMessagesAsRead(
            @Parameter(description = "Conversation ID")
            @PathVariable String conversationId,
            @RequestParam Long userId) {
        try {
            chatService.markMessagesAsRead(conversationId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error marking messages as read in conversation {}", conversationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se označit zprávy jako přečtené"));
        }
    }

    @DeleteMapping("/message/{messageId}")
    @Operation(summary = "Delete a message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Message not found",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized to delete",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<?> deleteMessage(
            @Parameter(description = "Message ID")
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        try {
            chatService.deleteMessage(messageId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting message {}", messageId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se smazat zprávu"));
        }
    }

    @PutMapping("/message/{messageId}")
    @Operation(summary = "Edit a message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message edited successfully"),
            @ApiResponse(responseCode = "404", description = "Message not found",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized to edit",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<?> editMessage(
            @Parameter(description = "Message ID")
            @PathVariable Long messageId,
            @RequestBody Map<String, String> request,
            @RequestParam Long userId) {
        try {
            String newContent = request.get("content");
            MessageDTO editedMessage = chatService.editMessage(messageId, newContent, userId);
            return ResponseEntity.ok(editedMessage);
        } catch (Exception e) {
            log.error("Error editing message {}", messageId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se upravit zprávu"));
        }
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread message count for current user")
    @ApiResponse(responseCode = "200", description = "Unread count retrieved successfully")
    public ResponseEntity<?> getUnreadMessageCount(@RequestParam Long userId) {
        try {
            Long unreadCount = chatService.getUnreadMessageCount(userId);
            return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
        } catch (Exception e) {
            log.error("Error getting unread count for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst počet nepřečtených zpráv"));
        }
    }

    @GetMapping("/new-messages")
    @Operation(summary = "Get new messages since last check (for polling)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New messages retrieved"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<?> getNewMessages(
            @RequestParam String conversationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime lastCheckTime,
            @RequestParam Long userId) {
        try {
            List<MessageDTO> newMessages = chatService.getNewMessages(conversationId, lastCheckTime, userId);
            return ResponseEntity.ok(newMessages);
        } catch (Exception e) {
            log.error("Error getting new messages for conversation {}", conversationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst nové zprávy"));
        }
    }

    @GetMapping("/conversation/{conversationId}/messages")
    @Operation(summary = "Get paginated messages for conversation")
    public ResponseEntity<?> getConversationMessages(
            @PathVariable String conversationId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) int size) {
        try {
            List<MessageDTO> messages = chatService.getConversationMessages(conversationId, userId, page, size);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting paginated messages for conversation {}", conversationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se načíst zprávy"));
        }
    }

    @GetMapping("/conversation-exists")
    @Operation(summary = "Check if conversation exists between two users")
    public ResponseEntity<?> conversationExists(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        try {
            boolean exists = chatService.conversationExists(userId1, userId2);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            log.error("Error checking if conversation exists between {} and {}", userId1, userId2, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se ověřit existenci konverzace"));
        }
    }

    @GetMapping("/conversation-id")
    @Operation(summary = "Get conversation ID for two users")
    public ResponseEntity<?> getConversationId(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        try {
            String conversationId = chatService.getConversationId(userId1, userId2);
            return ResponseEntity.ok(Map.of("conversationId", conversationId));
        } catch (Exception e) {
            log.error("Error getting conversation ID between {} and {}", userId1, userId2, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDTO("Nepodařilo se získat ID konverzace"));
        }
    }
}