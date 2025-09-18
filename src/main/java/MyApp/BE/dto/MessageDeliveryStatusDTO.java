package MyApp.BE.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import MyApp.BE.enums.DeliveryStatus;
/**
 * DTO for message delivery status
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDeliveryStatusDTO {
    private Long messageId;
    private String conversationId;
    private DeliveryStatus status;
    private OffsetDateTime timestamp;
    private DeliveryStatus deliveryStatus;

}