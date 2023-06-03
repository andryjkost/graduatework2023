package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.graduatework.common.NetworkingEventPaginatedFilter;
import ru.graduatework.common.NetworkingEventStatus;
import ru.graduatework.controller.dto.*;
import ru.graduatework.service.NetworkingEventService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/networking_event")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(description = "Работа с мероприятиями", name = "NetworkingEventController")
public class NetworkingEventController {

    private final NetworkingEventService service;

    @Operation(summary = "Получение списка мероприятий c фильтрацией и пагинацией")
    @GetMapping("")
    Mono<PaginatedResponseDto<NetworkingEventResponseDto>> getPaginatedByParams(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @Parameter(description = "Отступ - число элементов, пропущенных от начала списка результатов", allowEmptyValue = true)
            @RequestParam(required = false, defaultValue = "0") int offset,
            @Parameter(description = "Ограничение на число показанных результатов", allowEmptyValue = true)
            @RequestParam(required = false, defaultValue = "10") int limit,
            @Parameter(description = "Фильтр по статусам", allowEmptyValue = true)
            @RequestParam(required = false) NetworkingEventStatus status,
            @RequestParam(required = false) @Parameter(description = "Выдать мероприятия на которые подписан пользователь( true- подписан, false- нет, null - все)") Boolean eventSubscriptionFlag
    ) {
        var filter = NetworkingEventPaginatedFilter.builder()
                .offset(offset)
                .limit(limit)
                .status(status)
                .eventSubscriptionFlag(eventSubscriptionFlag)
                .build();

        return service.getPaginatedListOfEvents(filter, authToken);
    }

    //Если будет время сделать разделить методы в репозиториях
    @Operation(summary = "Создать мероприятие")
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Mono<NetworkingEventResponseDto> createNetworkingEvent(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @ModelAttribute NetworkingEventRequestDto requestDto
    ) {
        return service.createNetworkingEvent(authToken, requestDto, requestDto.getImage());
    }

    @Operation(summary = "Редактирование мероприятий тут если поле нулл - то зануллит")
    @PutMapping("")
    Mono<Boolean> updateNetworkingEvent(
            @RequestBody UpdateNetworkingEventRequestDto requestDto
    ) {

        return service.update(requestDto);
    }

    @Operation(summary = "Получение мероприятия по id")
    @GetMapping("/{id}")
    Mono<NetworkingEventResponseDto> getNetworkingEventById(
            @Parameter(description = "Идентификатор мероприятия") @PathVariable UUID id
    ) {

        return service.getById(id);
    }

    @Operation(summary = "Получение Аватарки")
    @GetMapping(value = "/avatar/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    Mono<FileSystemResource> getAvatar(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @Parameter(description = "Идентификатор мероприятия") @PathVariable UUID id
    ){
        return service.getAvatar(authToken, id);
    }

    @Operation(summary = "Получение мероприятий за период")
    @GetMapping("/date")
    Mono<PaginatedResponseDto<NetworkingEventResponseDto>> getNetworkingEventByDate() {

        return Mono.empty();
    }

}
