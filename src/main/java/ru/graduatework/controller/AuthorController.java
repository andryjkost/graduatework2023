package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.AuthenticationResponseDto;
import ru.graduatework.controller.dto.AuthorRequestDto;
import ru.graduatework.controller.dto.CourseResponseDto;
import ru.graduatework.services.AuthorService;

@RestController
@RequestMapping("/api/v1/author")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Tag(description = "Работа с авторами", name = "AuthorController")
public class AuthorController {

    private final AuthorService authorService;

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

    @Operation(summary = "Создать автора")
    @PostMapping("")
    Mono<AuthenticationResponseDto> createAuthor(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @RequestBody AuthorRequestDto requestDto
    ) {
        return authorService.createAuthor(authToken, requestDto);
    }

}
