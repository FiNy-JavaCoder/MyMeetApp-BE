package MyApp.BE.dto.mapper;

import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.dto.UserProfilePrivateDTO;
import MyApp.BE.entity.UserProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "userEntity", ignore = true)
    UserProfileEntity toEntity(UserProfileDTO dto);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "userEntity.nickName", target = "nickName")
    @Mapping(target = "heightCm", ignore = true)
    @Mapping(target = "weightKg", ignore = true)
    @Mapping(target = "birthYear", ignore = true)
    @Mapping(target = "birthMonth", ignore = true)
    @Mapping(target = "age", ignore = true)
    UserProfileDTO toPublicDTO(UserProfileEntity userProfileEntity);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "userEntity.nickName", target = "nickName")
    @Mapping(target = "heightCm", ignore = true)
    @Mapping(target = "weightKg", ignore = true)
    @Mapping(target = "birthYear", ignore = true)
    @Mapping(target = "birthMonth", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "favoriteFilter", ignore = true)
    UserProfilePrivateDTO toPrivateDTO(UserProfileEntity userProfileEntity);
}