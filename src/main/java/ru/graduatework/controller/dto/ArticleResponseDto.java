package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.graduatework.model.AuthorShortModel;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Статья")
public class ArticleResponseDto {

    @Schema(description = "ID")
    private UUID id;

    @Schema(description = "Название статьи")
    private String title;

    @Schema(description = "Текст статьи")
    private String textArticle;

    @Schema(description = "Время последнего изменения")
    private OffsetDateTime timeModification;

    @Schema(description = "Связанные курсы")
    private List<CourseInfoShortForArticleResponseDto> courseInfoShortForArticleResponseDto;

    @Schema(description = "Автор")
    private AuthorShortModel authorShortModel;
}
