package ru.graduatework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Статья c автором")
public class ArticleWithAuthorModel {
    @Schema(description = "ID")
    private UUID id;

    @Schema(description = "Название статьи")
    private String title;

    @Schema(description = "Текст статьи")
    private String textArticle;

    @Schema(description = "Время последнего изменения")
    private OffsetDateTime timeModification;

    private AuthorShortModel authorShortModel;

}
