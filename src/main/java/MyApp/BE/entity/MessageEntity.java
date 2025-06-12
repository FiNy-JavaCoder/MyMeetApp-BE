package MyApp.BE.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;


@Data
@Entity(name = "Message")
public class MessageEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity msgSender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserEntity msgRecipient;

    @Column(name = "cntMessage", columnDefinition = "TEXT", nullable = false)
    private String cntMessage;

    @Column(name = "timeStamp", nullable = false)
    private OffsetDateTime timeStamp;

    @Column (name = "conversationId")
    private String conversationId;





}
