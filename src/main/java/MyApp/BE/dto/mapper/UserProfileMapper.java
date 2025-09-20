package MyApp.BE.dto.mapper;

import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.dto.UserProfilePrivateDTO;
import MyApp.BE.entity.UserProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "userEntity", ignore = true)
    UserProfileEntity toEntity(UserProfileDTO dto);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "userEntity.nickName", target = "nickName")
    @Mapping(target = "heightCm", constant = "0")
    @Mapping(target = "weightKg", constant = "0")
    @Mapping(target = "birthYear", source = "birthDate", qualifiedByName = "getYearFromDate")
    @Mapping(target = "birthMonth", source = "birthDate", qualifiedByName = "getMonthFromDate")
    @Mapping(target = "age", source = "birthDate", qualifiedByName = "calculateAge")
    @Mapping(source = "searchTypeRelationShip", target = "searchTypeRelationShip")
    UserProfileDTO toPublicDTO(UserProfileEntity userProfileEntity);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "userEntity.nickName", target = "nickName")
    @Mapping(target = "heightCm", constant = "0")
    @Mapping(target = "weightKg", constant = "0")
    @Mapping(target = "birthYear", source = "birthDate", qualifiedByName = "getYearFromDate")
    @Mapping(target = "birthMonth", source = "birthDate", qualifiedByName = "getMonthFromDate")
    @Mapping(target = "age", source = "birthDate", qualifiedByName = "calculateAge")
    @Mapping(target = "favoriteFilter", ignore = true)
    @Mapping(source = "searchTypeRelationShip", target = "searchTypeRelationShip")
    UserProfilePrivateDTO toPrivateDTO(UserProfileEntity userProfileEntity);

    @Named("calculateAge")
    default int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    @Named("getYearFromDate")
    default int getYearFromDate(LocalDate birthDate) {
        return birthDate != null ? birthDate.getYear() : 0;
    }

    @Named("getMonthFromDate")
    default int getMonthFromDate(LocalDate birthDate) {
        return birthDate != null ? birthDate.getMonthValue() : 0;
    }
}