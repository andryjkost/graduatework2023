package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.CourseResponseDto;

@RestController
@RequestMapping("/api/v1/networking_event")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Tag(description = "Работа с мероприятиями", name = "NetworkingEventController")
public class NetworkingEventController {
    @Operation(summary = "Получение мероприятий")
    @GetMapping("")
    Mono<CourseResponseDto> getArticle(){
        return Mono.just(new CourseResponseDto());
    }
}
