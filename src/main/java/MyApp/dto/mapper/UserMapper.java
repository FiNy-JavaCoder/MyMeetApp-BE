package MyApp.dto.mapper;

import MyApp.entity.UserEntity;
import MyApp.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "profilePictureUrl", ignore = true)
    UserEntity toEntity(UserDTO dto);

    UserDTO toDTO(UserEntity entity);
}

