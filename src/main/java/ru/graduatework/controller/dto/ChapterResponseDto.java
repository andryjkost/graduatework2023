package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Раздел")
public class ChapterResponseDto {
    @Schema(description = "id раздела")
    private UUID id;

    @Schema(description = "Название раздела")
    private String title;

    @Schema(description = "Краткое описание раздела")
    private String description;

    private List<TopicResponseDto> topics;
}
