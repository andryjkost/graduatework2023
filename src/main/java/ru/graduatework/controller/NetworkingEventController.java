package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.graduatework.common.NetworkingEventPaginatedFilter;
import ru.graduatework.common.NetworkingEventStatus;
import ru.graduatework.controller.dto.NetworkingEventRequestDto;
import ru.graduatework.controller.dto.NetworkingEventResponseDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.services.NetworkingEventService;

@RestController
@RequestMapping("/api/v1/networking_event")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
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
            @RequestParam(required = false) NetworkingEventStatus status
    ) {
        var filter = NetworkingEventPaginatedFilter.builder()
                .offset(offset)
                .limit(limit)
                .status(status)
                .build();

        return service.getPaginatedListOfEvents(filter, authToken);
    }

    @Operation(summary = "Создать мероприятие")
    @PostMapping("")
    Mono<Void> createNetworkingEvent(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @RequestBody NetworkingEventRequestDto requestDto
            ) {
        return service.createNetworkingEvent(authToken, requestDto);
    }

    @Operation(summary = "")
    @PutMapping("")
    Mono<Void> updateNetworkingEvent() {
        return Mono.empty();
    }

    @Operation(summary = "")
    @GetMapping("/id")
    Mono<Void> getNetworkingEventById() {
        return Mono.empty();
    }

    @Operation(summary = "")
    @GetMapping("/date")
    Mono<Void> getNetworkingEventByDate() {
        return Mono.empty();
    }

}
