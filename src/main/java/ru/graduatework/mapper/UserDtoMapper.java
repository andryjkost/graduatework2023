package ru.graduatework.mapper;


import org.mapstruct.Mapper;
import org.springframework.security.core.userdetails.UserDetails;
import ru.graduatework.controller.dto.UserWithFieldsOfActivityResponseDto;
import ru.graduatework.controller.dto.UserWithRoleResponseDto;
import ru.graduatework.jooq.tables.records.UserRecord;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    UserWithRoleResponseDto map(UserRecord user);

    UserWithFieldsOfActivityResponseDto mapById(UserRecord user);

//    UserDetails map(UserWithRoleResponseDto user);
}
