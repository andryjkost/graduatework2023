package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.graduatework.model.AuthorShortModel;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Краткая информация о связанном курсе")
public class CourseInfoShortForArticleResponseDto {
    @Schema(description = "id курса")
    private UUID id;

    @Schema(description = "Название курса")
    private String title;

    private AuthorShortModel authorShortModel;
}
