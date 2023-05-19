package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.CourseResponseDto;

@RestController
@RequestMapping("/api/v1/networking_event")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Tag(description = "Работа с мероприятиями", name = "NetworkingEventController")
public class NetworkingEventController {
    @Operation(summary = "Получение списка мероприятий")
    @GetMapping("")
    Mono<CourseResponseDto> getArticle(){
        return Mono.just(new CourseResponseDto());
    }

    @Operation(summary = "")
    @PostMapping("")
    Mono<Void> createNetworkingEvent(){
        return Mono.empty();
    }

    @Operation(summary = "")
    @PutMapping("")
    Mono<Void> updateNetworkingEvent(){
        return Mono.empty();
    }

    @Operation(summary = "")
    @GetMapping("")
    Mono<Void> getNetworkingEventById(){
        return Mono.empty();
    }

    @Operation(summary = "")
    @GetMapping("")
    Mono<Void> getNetworkingEventByDate(){
        return Mono.empty();
    }

}
