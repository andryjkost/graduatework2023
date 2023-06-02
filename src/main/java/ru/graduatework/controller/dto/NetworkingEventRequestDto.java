package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

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

    @Schema(description = "Время начала мероприятия, Отправлять Строкой но в виде 2023-06-02T19:53:13.719071+03:00")
    @NotNull
    private String startTime;

    @Schema(description = "Максимальное кол-во участников")
    @NotNull
    private Long maximumNumberOfParticipants;

    @Schema(description = "Avatar")
    @NotNull
    MultipartFile image;
}
