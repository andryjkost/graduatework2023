package ru.graduatework.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.graduatework.common.NetworkingEventStatus;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Нетворкинг мероприятие")
public class NetworkingEventResponseDto {
    @Schema(description = "id мероприятия")
    private Long id;

    @Schema(description = "Название мероприятия")
    private String title;

    @Schema(description = "Краткое описание мероприятия")
    private String description;

    @Schema(description = "Ссылка на мероприятие")
    private String link;

    @Schema(description = "Время начала мероприятия")
    private OffsetDateTime startTime;

    @Schema(description = "Статус мероприятия")
    private NetworkingEventStatus status;

    @Schema(description = "Максимальное кол-во участников")
    private String maximumNumberOfParticipants;

    @Schema(description = "Кол-во свободных мест")
    private String numberOfAvailableSeats;

    @Schema(description = "ID автора")
    private Long author_id;

    @Schema(description = "Имя + фамилия автора")
    private String authorFirstLastNames;
}
