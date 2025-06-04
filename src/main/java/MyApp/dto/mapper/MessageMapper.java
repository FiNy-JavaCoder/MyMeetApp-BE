package MyApp.dto.mapper;

import MyApp.dto.MessageDTO;

import MyApp.entity.MessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "msgSender", ignore = true)
    @Mapping(target = "msgRecipient", ignore = true)
    @Mapping(target = "timeStamp", ignore = true)
    MessageEntity toEntity(MessageDTO dto);

    MessageDTO toDTO(MessageEntity entity);

}
