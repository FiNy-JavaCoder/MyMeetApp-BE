package MyApp.BE.dto.mapper;

import MyApp.BE.dto.PrivateUserDTO;
import MyApp.BE.dto.UserDTO;
import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.entity.UserEntity;
import MyApp.BE.entity.UserProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "userId", ignore = true)
    UserProfileEntity toEntity(PrivateUserDTO dto);

    @Mapping(source = "user.nickName", target = "nickName")
    UserProfileDTO toDTO(UserProfileEntity userProfileEntity);

    List<UserProfileDTO> toDTOs(List<UserProfileEntity> userProfileEntities);

}
