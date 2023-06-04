package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jdk.dynalink.linker.LinkerServices;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Курс")
public class CourseResponseDto {

    @Schema(description = "id курса")
    private UUID id;

    @Schema(description = "Название курса")
    private String title;

    @Schema(description = "Краткое описание курса")
    private String description;

    @Schema(description = "Категория курса", example = "SMM")
    private String category;

    @Schema(description = "Фичи курса")
    private List<String> features;

    @Schema(description = "Картинка")
    private byte[] image;

    private List<ChapterResponseDto> chapters;

    private List<TopicResponseDto> topics;
}
