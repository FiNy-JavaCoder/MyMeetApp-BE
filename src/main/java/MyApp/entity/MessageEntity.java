package MyApp.entity;

import MyApp.dto.mapper.converters.RegionsSetConverter;
import MyApp.enums.GenderType;
import MyApp.enums.Regions;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;


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
    private LocalDateTime timeStamp = LocalDateTime.now();

    @Column (name = "conversationId")
    private String conversationId;





}
