package MyApp.BE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypingIndicatorDTO {
    private Long userId;
    private boolean isTyping;
    private OffsetDateTime timestamp;
}