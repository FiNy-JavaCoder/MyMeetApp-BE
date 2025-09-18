package MyApp.BE.controller;

import MyApp.BE.dto.ErrorDTO;
import MyApp.BE.dto.MessageDTO;
import MyApp.BE.service.ChatService;
import MyApp.BE.service.ConversationSummaryDTO;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
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
    public ResponseEntity<MessageDTO> sendMessage(
            @Valid @RequestBody MessageDTO messageDTO,
            Authentication authentication) {
        
        // Set sender ID from authenticated user
        Long senderId = getUserIdFromAuthentication(authentication);
        messageDTO.setSenderId(senderId);
        
        MessageDTO sentMessage = chatService.sendMessage(messageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(sentMessage);
    }

    @GetMapping("/conversation/{otherUserId}")
    @Operation(summary = "Get conversation with another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<List<MessageDTO>> getConversation(
            @Parameter(description = "ID of the other user in conversation")
            @PathVariable Long otherUserId,
            Authentication authentication) {
        
        Long currentUserId = getUserIdFromAuthentication(authentication);
        List<MessageDTO> messages = chatService.getConversation(currentUserId, otherUserId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversation")
    @Operation(summary = "Get messages by conversation ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<List<MessageDTO>> getMessagesByConversationId(
            @Parameter(description = "Conversation ID")
            @RequestParam String conversationId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        List<MessageDTO> messages = chatService.getMessagesByConversationId(conversationId, userId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversations/recent")
    @Operation(summary = "Get recent conversations for the current user")
    @ApiResponse(responseCode = "200", description = "Conversations retrieved successfully")
    public ResponseEntity<List<ConversationSummaryDTO>> getRecentConversations(
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        List<ConversationSummaryDTO> conversations = chatService.getRecentConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @PutMapping("/mark-read/{conversationId}")
    @Operation(summary = "Mark all messages in a conversation as read")
    @ApiResponse(responseCode = "204", description = "Messages marked as read")
    public ResponseEntity<Void> markMessagesAsRead(
            @Parameter(description = "Conversation ID")
            @PathVariable String conversationId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        chatService.markMessagesAsRead(conversationId, userId);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "Message ID")
            @PathVariable Long messageId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        chatService.deleteMessage(messageId, userId);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<MessageDTO> editMessage(
            @Parameter(description = "Message ID")
            @PathVariable Long messageId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        String newContent = request.get("content");
        
        MessageDTO editedMessage = chatService.editMessage(messageId, newContent, userId);
        return ResponseEntity.ok(editedMessage);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread message count for current user")
    @ApiResponse(responseCode = "200", description = "Unread count retrieved successfully")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCount(
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Long unreadCount = chatService.getUnreadMessageCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
    }

    @GetMapping("/new-messages")
    @Operation(summary = "Get new messages since last check (for polling)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New messages retrieved"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<List<MessageDTO>> getNewMessages(
            @RequestParam String conversationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime lastCheckTime,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        List<MessageDTO> newMessages = chatService.getNewMessages(conversationId, lastCheckTime, userId);
        return ResponseEntity.ok(newMessages);
    }

    /**
     * Helper method to extract user ID from authentication
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // Implement based on your authentication setup
        // This is a placeholder - adjust according to your security configuration
        return Long.parseLong(authentication.getName());
    }
}