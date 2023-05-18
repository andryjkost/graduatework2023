package ru.graduatework.mapper;


import org.mapstruct.Mapper;
import ru.graduatework.controller.dto.UserWithFieldsOfActivityResponseDto;
import ru.graduatework.controller.dto.UserWithRoleResponseDto;
import ru.graduatework.controller.dto.UserWithRolesResponseDto;
import ru.graduatework.jooq.tables.records.UserRecord;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    UserWithRolesResponseDto map(UserRecord user);

    UserWithRoleResponseDto mapW(UserRecord user);

    UserWithFieldsOfActivityResponseDto mapById(UserRecord user);

//    UserDetails map(UserWithRoleResponseDto user);
}
