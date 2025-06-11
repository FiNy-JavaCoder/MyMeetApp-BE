package MyApp.dto.mapper;

import MyApp.dto.MessageDTO;

import MyApp.dto.UserDTO;
import MyApp.entity.MessageEntity;
import MyApp.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "msgSender", ignore = true)
    @Mapping(target = "msgRecipient", ignore = true)
    MessageEntity toEntity(MessageDTO dto);

    @Mapping(source = "msgRecipient.userId", target = "recipientId")
    @Mapping(source = "msgSender.userId", target = "senderId")
    MessageDTO toDTO(MessageEntity entity);

    @Mapping(source = "msgRecipient.userId", target = "recipientId")
    @Mapping(source = "msgSender.userId", target = "senderId")
    List<MessageDTO> toDTOs(List<MessageEntity> MessageEntities);

}
