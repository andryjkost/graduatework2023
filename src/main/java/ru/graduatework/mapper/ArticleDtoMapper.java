package ru.graduatework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.graduatework.controller.dto.ArticleRequestDto;
import ru.graduatework.controller.dto.ArticleResponseDto;
import ru.graduatework.controller.dto.ArticleShortResponseDto;
import ru.graduatework.controller.dto.UpdateArticleRequestDto;
import ru.graduatework.jooq.tables.records.ArticleRecord;
import ru.graduatework.model.ArticleWithAuthorModel;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, OffsetDateTime.class})
public interface ArticleDtoMapper {

    @Mapping(target = "timeOfCreationOrModification", expression = "java(OffsetDateTime.now())" )
    ArticleRecord mapToUpdate(UpdateArticleRequestDto updateArticleRequestDto);

    @Mapping(target = "id", expression = "java(UUID.randomUUID().getLeastSignificantBits())")
    ArticleRecord mapToCreate(ArticleRequestDto articleRequestDto);

    //    @Mapping(target = "timeModification", source = "timeOfCreationOrModification")
    ArticleResponseDto map(ArticleWithAuthorModel articleRecord);

    ArticleShortResponseDto mapShort(ArticleRecord articleRecord);

    List<ArticleShortResponseDto> mapShort(List<ArticleRecord> articleRecords);

}
