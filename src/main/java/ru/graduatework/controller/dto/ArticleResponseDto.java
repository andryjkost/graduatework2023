package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Статья")
public class ArticleResponseDto {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "Название статьи")
    private String title;

    @Schema(description = "Текст статьи")
    private String textArticle;

    @Schema(description = "Время последнего изменения")
    private OffsetDateTime timeModification;
}
