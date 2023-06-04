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
@Schema(description = "Краткая информация о связанной статье")
public class ArticleinfoShortForCourseResponseDto {
    @Schema(description = "id статьи")
    private UUID id;

    @Schema(description = "Название статьи")
    private String title;

    private AuthorShortModel authorShortModel;
}
