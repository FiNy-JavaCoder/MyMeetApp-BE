package MyApp.BE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDTO {
    
    private String conversationId;
    private Long otherUserId;
    private String otherUserNickName;
    private String otherUserProfilePicture;
    private String lastMessage;
    private OffsetDateTime lastMessageTime;
    private Long unreadCount;
    private boolean isOnline;
}