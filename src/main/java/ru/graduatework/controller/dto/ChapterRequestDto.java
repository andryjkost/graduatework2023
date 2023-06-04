package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Раздел")
public class ChapterRequestDto {

    @Schema(description = "Название раздела")
    @NotNull
    private String title;

    @Schema(description = "Краткое описание раздела")
    @NotNull
    private String description;

    private Integer priorityNumberOfTheSectionInTheCourse;

    private List<TopicRequestDto> topics;
}
