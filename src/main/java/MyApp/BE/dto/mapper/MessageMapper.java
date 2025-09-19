package MyApp.BE.dto.mapper;

import MyApp.BE.dto.MessageDTO;
import MyApp.BE.entity.MessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "messageId", ignore = true)
    @Mapping(target = "msgSender", ignore = true)
    @Mapping(target = "msgRecipient", ignore = true)
    @Mapping(target = "timeStamp", ignore = true)
    @Mapping(target = "isRead", ignore = true)
    @Mapping(target = "isDeletedBySender", ignore = true)
    @Mapping(target = "isDeletedByRecipient", ignore = true)
    @Mapping(target = "editedAt", ignore = true)
    MessageEntity toEntity(MessageDTO dto);

    @Mapping(source = "messageId", target = "messageId")
    @Mapping(source = "msgRecipient.userId", target = "recipientId")
    @Mapping(source = "msgSender.userId", target = "senderId")
    @Mapping(source = "cntMessage", target = "cntMessage")
    @Mapping(source = "conversationId", target = "conversationId")
    @Mapping(source = "timeStamp", target = "timeStamp")
    @Mapping(source = "isRead", target = "isRead")
    @Mapping(source = "editedAt", target = "editedAt")
    @Mapping(expression = "java(entity.getEditedAt() != null)", target = "isEdited")
    MessageDTO toDTO(MessageEntity entity);

    List<MessageDTO> toDTOs(List<MessageEntity> messageEntities);

    @Named("conversationIdFromUserIds")
    default String generateConversationId(Long senderId, Long recipientId) {
        if (senderId == null || recipientId == null) {
            return null;
        }
        if (senderId.compareTo(recipientId) < 0) {
            return String.format("%d_%d", senderId, recipientId);
        } else {
            return String.format("%d_%d", recipientId, senderId);
        }
    }
}