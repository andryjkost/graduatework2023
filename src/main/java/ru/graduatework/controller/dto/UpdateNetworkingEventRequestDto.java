package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.graduatework.common.NetworkingEventStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateNetworkingEventRequestDto{

    @Schema(description = "id")
    private UUID id;

    @Schema(description = "Название мероприятия")
    private String title;

    @Schema(description = "Краткое описание мероприятия")
    private String description;

    @Schema(description = "Ссылка на мероприятие")
    private String link;

    @Schema(description = "Статус мероприятия")
    private NetworkingEventStatus status;


    @Schema(description = "Время начала мероприятия")
    private OffsetDateTime startTime;

    @Schema(description = "Максимальное кол-во участников")
    private Long maximumNumberOfParticipants;

    @Schema(description = "Кол-во свободных мест")
    private Long numberOfAvailableSeats;
}
