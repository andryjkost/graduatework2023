package ru.graduatework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.graduatework.controller.dto.*;
import ru.graduatework.service.ArticleService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/article")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(description = "Работа со статьями", name = "ArticleController")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "Получение списка статей c фильтрацией и пагинацией")
    @GetMapping("")
    Mono<PaginatedResponseDto<ArticleShortResponseDto>> getPaginatedShortArticle(
            @Parameter(description = "Отступ - число элементов, пропущенных от начала списка результатов", allowEmptyValue = true)
            @RequestParam(required = false, defaultValue = "0") int offset,
            @Parameter(description = "Ограничение на число показанных результатов", allowEmptyValue = true)
            @RequestParam(required = false, defaultValue = "10") int limit
    ) {
        return articleService.getPaginatedShortArticle(offset, limit);
    }

    @Operation(summary = "Получение статьи по id")
    @GetMapping("/{articleId}")
    Mono<ArticleResponseDto> getArticleById(
            @Parameter(description = "Идентификатор статьи") @PathVariable UUID articleId
    ) {
        return articleService.getArticleById(articleId);
    }

    @Operation(summary = "Создание статьи")
    @PostMapping("")
    Mono<Void> createArticle(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @RequestBody ArticleRequestDto requestDto
    ) {
        return articleService.createArticle(authToken, requestDto);
    }

    @Operation(summary = "Редактирование статьи по id(передавать null в поле если его не нужно редактировать)")
    @PutMapping("")
    Mono<Boolean> updateArticle(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authToken,
            @RequestBody UpdateArticleRequestDto requestDto) {

        return articleService.updateArticle(authToken, requestDto);
    }

}
