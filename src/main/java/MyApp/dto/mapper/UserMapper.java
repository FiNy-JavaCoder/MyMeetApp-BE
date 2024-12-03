package MyApp.dto.mapper;

import MyApp.entity.UserEntity;
import MyApp.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(UserDTO source);
    UserDTO toDTO(UserEntity source);
}