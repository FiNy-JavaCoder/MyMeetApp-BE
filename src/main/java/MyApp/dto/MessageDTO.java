package MyApp.dto;

import MyApp.enums.GenderType;
import MyApp.enums.Regions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Long senderId;
    private Long recipientId;
    private String cntMessage;
    private String conversationId;



}
