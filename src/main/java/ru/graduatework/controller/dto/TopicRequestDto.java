package ru.graduatework.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Тема")
public class TopicRequestDto {

    @Schema(description = "Название темы")
    private String title;

    @Schema(description = "Краткое описание темы")
    private String description;

    @Schema(description = "Ссылка на видео")
    private String linkToVideo;

    private Integer priorityNumberOfTheSection;
}
