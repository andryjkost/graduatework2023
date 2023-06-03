package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ru.graduatework.common.NetworkingEventStatus;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateNetworkingEventRequestDto {

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


    @Schema(description = "Время начала мероприятия, Отправлять Строкой но в виде 2023-06-02T19:53:13.719071+03:00")
    private String startTime;

    @Schema(description = "Максимальное кол-во участников")
    private Long maximumNumberOfParticipants;

    @Schema(description = "Кол-во свободных мест")
    private Long numberOfAvailableSeats;

    @Schema(description = "Avatar")
    MultipartFile image;
}
