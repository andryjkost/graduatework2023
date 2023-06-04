package ru.graduatework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.graduatework.controller.dto.CourseRequestDto;
import ru.graduatework.jooq.tables.records.CourseRecord;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class})
public interface CourseDtoMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    CourseRecord mapForCreate(CourseRequestDto dto);
}
