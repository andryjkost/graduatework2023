package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.graduatework.model.AuthorShortModel;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Статья")
public class ArticleShortResponseDto {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "Название статьи")
    private String title;

    @Schema(description = "Информация об авторе статьи")
    private AuthorShortModel authorShortModel;
}
