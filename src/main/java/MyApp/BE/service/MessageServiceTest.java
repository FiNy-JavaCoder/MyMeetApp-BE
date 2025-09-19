package MyApp.BE.service;

import MyApp.BE.dto.MessageDTO;
import MyApp.BE.dto.mapper.MessageMapper;
import MyApp.BE.entity.MessageEntity;
import MyApp.BE.entity.UserEntity;
import MyApp.BE.entity.repository.IMessageRepository;
import MyApp.BE.entity.repository.IUserRepository;
import MyApp.BE.service.Message.MessageService;
import MyApp.BE.service.ZoneTime.IZoneTimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private IMessageRepository messageRepository;
    
    @Mock
    private IUserRepository userRepository;
    
    @Mock
    private MessageMapper messageMapper;
    
    @Mock
    private IZoneTimeService zoneTimeService;

    @InjectMocks
    private MessageService messageService;

    private UserEntity senderUser;
    private UserEntity recipientUser;
    private MessageDTO messageDTO;
    private MessageEntity messageEntity;

    @BeforeEach
    void setUp() {
        senderUser = new UserEntity();
        senderUser.setUserId(1L);
        senderUser.setNickName("sender");

        recipientUser = new UserEntity();
        recipientUser.setUserId(2L);
        recipientUser.setNickName("recipient");

        messageDTO = new MessageDTO();
        messageDTO.setSenderId(1L);
        messageDTO.setRecipientId(2L);
        messageDTO.setCntMessage("Test message");

        messageEntity = new MessageEntity();
        messageEntity.setMessageId(1L);
        messageEntity.setMsgSender(senderUser);
        messageEntity.setMsgRecipient(recipientUser);
        messageEntity.setCntMessage("Test message");
        messageEntity.setConversationId("1_2");
        messageEntity.setTimeStamp(OffsetDateTime.now());
    }

    @Test
    void testCreateMessage_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(senderUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(recipientUser));
        when(zoneTimeService.setZoneTime(anyString())).thenReturn(OffsetDateTime.now());
        when(messageRepository.save(any(MessageEntity.class))).thenReturn(messageEntity);
        when(messageMapper.toDTO(any(MessageEntity.class))).thenReturn(messageDTO);

        // When
        ResponseEntity<MessageDTO> result = messageService.createMessage(messageDTO);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(messageRepository).save(any(MessageEntity.class));
    }

    @Test
    void testCreateMessage_EmptyMessage() {
        // Given
        messageDTO.setCntMessage("");

        // When
        ResponseEntity<MessageDTO> result = messageService.createMessage(messageDTO);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        verify(messageRepository, never()).save(any(MessageEntity.class));
    }

    @Test
    void testCreateMessage_SenderNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<MessageDTO> result = messageService.createMessage(messageDTO);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(messageRepository, never()).save(any(MessageEntity.class));
    }

    @Test
    void testGetConversationById_Success() {
        // Given
        String conversationId = "1_2";
        List<MessageEntity> entities = List.of(messageEntity);
        List<MessageDTO> dtos = List.of(messageDTO);
        
        when(messageRepository.findByConversationId(conversationId)).thenReturn(entities);
        when(messageMapper.toDTOs(entities)).thenReturn(dtos);

        // When
        List<MessageDTO> result = messageService.getConversationById(conversationId);

        // Then
        assertEquals(1, result.size());
        assertEquals(messageDTO, result.get(0));
    }

    @Test
    void testGetConversationById_EmptyConversationId() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> messageService.getConversationById(""));
    }
}