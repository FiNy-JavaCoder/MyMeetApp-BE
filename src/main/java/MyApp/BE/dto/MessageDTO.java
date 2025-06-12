package MyApp.BE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Long senderId;
    private Long recipientId;
    private String cntMessage;
    private String conversationId;
    private OffsetDateTime timeStamp;
}
