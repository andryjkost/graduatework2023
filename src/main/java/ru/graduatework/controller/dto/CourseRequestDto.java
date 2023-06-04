package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Курс")
public class CourseRequestDto {
    @Schema(description = "Название курса")
    @NotNull
    private String title;

    @Schema(description = "Краткое описание курса")
    private String description;

    @Schema(description = "Категория курса", example = "SMM")
    @NotNull
    private String category;

    @Schema(description = "Фичи курса")
    @NotNull
    private List<String> features;

    @Schema(description = "Ссылка для оплаты")
    @NotNull
    private String linkPayment;

    private List<ChapterRequestDto> chapters;

    private List<TopicRequestDto> topics;
}
