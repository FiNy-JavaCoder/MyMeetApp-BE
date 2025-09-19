package MyApp.BE.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    @NotNull(message = "ID odesílatele je povinné")
    private Long senderId;
    
    @NotNull(message = "ID příjemce je povinné")
    private Long recipientId;
    
    @NotBlank(message = "Obsah zprávy nesmí být prázdný")
    @Size(max = 1000, message = "Zpráva může mít maximálně 1000 znaků")
    private String cntMessage;
    
    private String conversationId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime timeStamp;

    // Helper method to get conversation ID
    public String getGeneratedConversationId() {
        if (senderId != null && recipientId != null) {
            if (senderId.compareTo(recipientId) < 0) {
                return String.format("%d_%d", senderId, recipientId);
            } else {
                return String.format("%d_%d", recipientId, senderId);
            }
        }
        return conversationId;
    }
}