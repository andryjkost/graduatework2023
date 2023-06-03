package ru.graduatework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.graduatework.controller.dto.FieldsOfActivityRequestDto;
import ru.graduatework.controller.dto.UpdateFieldsOfActivityRequestDto;
import ru.graduatework.jooq.tables.records.FieldOfActivityRecord;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class})
public interface FieldOfActivityMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    FieldOfActivityRecord mapForCreate(FieldsOfActivityRequestDto requestDto);

    FieldOfActivityRecord mapForUpdate(UpdateFieldsOfActivityRequestDto requestDto);
}
