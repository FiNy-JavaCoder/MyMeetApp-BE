package MyApp.BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_conversation_id", columnList = "conversation_id"),
    @Index(name = "idx_sender_id", columnList = "sender_id"),
    @Index(name = "idx_recipient_id", columnList = "recipient_id"),
    @Index(name = "idx_timestamp", columnList = "time_stamp")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity msgSender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserEntity msgRecipient;

    @Column(name = "cnt_message", columnDefinition = "TEXT", nullable = false)
    private String cntMessage;

    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @Column(name = "time_stamp", nullable = false)
    private OffsetDateTime timeStamp;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "is_deleted_by_sender")
    private boolean isDeletedBySender = false;

    @Column(name = "is_deleted_by_recipient")
    private boolean isDeletedByRecipient = false;

    @Column(name = "edited_at")
    private OffsetDateTime editedAt;

    @PrePersist
    protected void onCreate() {
        if (timeStamp == null) {
            timeStamp = OffsetDateTime.now();
        }
        if (conversationId == null) {
            // Generate conversation ID based on user IDs (smaller ID first for consistency)
            Long id1 = msgSender.getUserId();
            Long id2 = msgRecipient.getUserId();
            conversationId = id1 < id2 ? 
                "conv_" + id1 + "_" + id2 : 
                "conv_" + id2 + "_" + id1;
        }
    }
}