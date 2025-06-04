package MyApp.dto.mapper;

import MyApp.dto.MessageDTO;

import MyApp.entity.MessageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageEntity toEntity(MessageDTO dto);

    MessageDTO toDTO(MessageEntity entity);

}
