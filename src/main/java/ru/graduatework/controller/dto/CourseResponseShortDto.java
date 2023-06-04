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
@Schema(description = "Курс")
public class CourseResponseShortDto {
    @Schema(description = "id курса")
    private UUID id;

    @Schema(description = "Название курса")
    private String title;

    @Schema(description = "Краткое описание курса")
    private String description;

    @Schema(description = "Категория курса", example = "SMM")
    private String category;

    @Schema(description = "Картинка")
    private byte[] image;

    @Schema(description = "Инфа об авторе")
    private AuthorShortModel authorShortModel;
}
