package ru.graduatework.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.graduatework.common.NetworkingEventStatus;
import ru.graduatework.model.AuthorShortModel;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Нетворкинг мероприятие")
public class NetworkingEventResponseDto {
    @Schema(description = "id мероприятия")
    private UUID id;

    @Schema(description = "Название мероприятия")
    private String title;

    @Schema(description = "Краткое описание мероприятия")
    private String description;

    @Schema(description = "Ссылка на мероприятие")
    private String link;

    @Schema(description = "Время начала мероприятия")
    private OffsetDateTime startTime;

    @Schema(description = "Длительность мероприятия")
    private LocalTime durationOfEvent;

    @Schema(description = "Флаг подписки юзера на мероприятие")
    private Boolean eventSubscriptionFlag;

    @Schema(description = "Статус мероприятия")
    private NetworkingEventStatus status;

    @Schema(description = "Максимальное кол-во участников")
    private Long maximumNumberOfParticipants;

    @Schema(description = "Кол-во свободных мест")
    private Long numberOfAvailableSeats;

    @Schema(description = "Картинка")
    private byte[] image;

    @Schema(description = "Инфа об авторе")
    private AuthorShortModel authorShortModel;
}
