package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Нетворкинг мероприятие")
public class NetworkingEventRequestDto {

    @Schema(description = "Название мероприятия")
    @NotNull
    private String title;

    @Schema(description = "Краткое описание мероприятия")
    @NotNull
    private String description;

    @Schema(description = "Ссылка на мероприятие")
    @NotNull
    private String link;

    @Schema(description = "Время начала мероприятия")
    @NotNull
    private OffsetDateTime startTime;

    @Schema(description = "Максимальное кол-во участников")
    @NotNull
    private Long maximumNumberOfParticipants;
}
