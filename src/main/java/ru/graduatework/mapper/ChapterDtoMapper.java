package ru.graduatework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.graduatework.controller.dto.ChapterRequestDto;
import ru.graduatework.jooq.tables.records.ChapterRecord;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class})
public interface ChapterDtoMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "courseId", source = "courseId")
    ChapterRecord mapForCreate(ChapterRequestDto dto, UUID courseId);
}
