package MyApp.BE.dto.mapper;

import MyApp.BE.dto.PrivateUserDTO;
import MyApp.BE.dto.RegistrationDTO;
import MyApp.BE.entity.UserEntity;
import MyApp.BE.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "userProfileEntity", ignore = true)
    @Mapping(target = "password", source = "passwordHash")
    UserEntity toEntity(PrivateUserDTO dto);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "userProfileEntity", ignore = true)
    @Mapping(target = "password", source = "passwordHash")
    UserEntity toEntity(RegistrationDTO dto);

    @Mapping(target = "regions", ignore = true)
    UserDTO toDTO(UserEntity entity);

    List<UserDTO> toDTOs(List<UserEntity> userEntities);
}