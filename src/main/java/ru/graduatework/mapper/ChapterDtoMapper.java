package ru.graduatework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.graduatework.controller.dto.ChapterRequestDto;
import ru.graduatework.controller.dto.ChapterResponseDto;
import ru.graduatework.jooq.tables.records.ChapterRecord;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class})
public interface ChapterDtoMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "courseId", source = "courseId")
    ChapterRecord mapForCreate(ChapterRequestDto dto, UUID courseId);

    ChapterResponseDto map(ChapterRecord chapterRecord);
    List<ChapterResponseDto> map(List<ChapterRecord> chapterRecord);
}
