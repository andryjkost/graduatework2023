package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.CourseResponseDto;

@RestController
@RequestMapping("/api/v1/author")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Tag(description = "Работа с авторами", name = "AuthorController")
public class AuthorController {
    @Operation(summary = "Получение авторов")
    @GetMapping("")
    Mono<CourseResponseDto> getAuthor() {
        return Mono.just(new CourseResponseDto());
    }

    @Operation(summary = "")
    @PutMapping("")
    Mono<Void> updateAuthor() {
        return Mono.empty();
    }
}
