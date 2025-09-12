package MyApp.BE.dto.mapper;

import MyApp.BE.dto.PrivateUserDTO;
import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.dto.UserProfilePrivateDTO;
import MyApp.BE.entity.UserProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "userId", ignore = true)
    UserProfileEntity toEntity(PrivateUserDTO dto);

    @Mapping(source = "userId", target = "userId")
    UserProfileDTO toPublicDTO(UserProfileEntity userProfileEntity);

    @Mapping(source = "userId", target = "userId")
    UserProfilePrivateDTO toPrivateDTO(UserProfileEntity userProfileEntity);


}
