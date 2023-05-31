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
@RequestMapping("/api/v1/article")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(description = "Работа со статьями", name = "ArticleController")
public class ArticleController {
    @Operation(summary = "Получение статей")
    @GetMapping("")
    Mono<CourseResponseDto> getArticle(){
        return Mono.just(new CourseResponseDto());
    }
}
