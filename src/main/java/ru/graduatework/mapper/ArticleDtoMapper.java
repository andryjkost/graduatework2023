package ru.graduatework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.graduatework.controller.dto.ArticleRequestDto;
import ru.graduatework.controller.dto.ArticleResponseDto;
import ru.graduatework.controller.dto.ArticleShortResponseDto;
import ru.graduatework.jooq.tables.records.ArticleRecord;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class})
public interface ArticleDtoMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID().getLeastSignificantBits())")
    ArticleRecord mapToCreate(ArticleRequestDto articleRequestDto);

    @Mapping(target = "timeModification", source = "timeOfCreationOrModification")
    ArticleResponseDto map(ArticleRecord articleRecord);

    ArticleShortResponseDto mapShort(ArticleRecord articleRecord);

    List<ArticleShortResponseDto> mapShort(List<ArticleRecord> articleRecords);

}
