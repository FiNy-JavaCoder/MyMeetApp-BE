package MyApp.BE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypingIndicatorDTO {
    private Long senderId;
    private Long recipientId;
    private String conversationId;
    private boolean isTyping;
}