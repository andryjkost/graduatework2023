package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.graduatework.model.AuthorShortModel;

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

    @Schema(description = "Связанные статьи")
    private List<ArticleinfoShortForCourseResponseDto> articleinfoShortForCourseResponseDtos;

    @Schema(description = "Картинка")
    private byte[] image;

    @Schema(description = "Флаг об оплате")
    private Boolean flagPayment;

    @Schema(description = "Ссылка для оплаты")
    private String linkPayment;

    private List<ChapterResponseDto> chapters;

    private List<TopicResponseDto> topics;

    private AuthorShortModel authorShortModel;
}
