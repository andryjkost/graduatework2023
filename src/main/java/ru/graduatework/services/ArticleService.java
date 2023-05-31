package ru.graduatework.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.graduatework.config.JwtService;
import ru.graduatework.controller.dto.ArticleRequestDto;
import ru.graduatework.controller.dto.ArticleResponseDto;
import ru.graduatework.controller.dto.ArticleShortResponseDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.mapper.ArticleDtoMapper;
import ru.graduatework.repository.ArticleRepository;
import ru.graduatework.repository.AuthorArticleRepository;
import ru.graduatework.repository.AuthorRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final PostgresOperatingDb db;
    private final ArticleRepository articleRepository;
    private final AuthorRepository authorRepository;
    private final AuthorArticleRepository authorArticleRepository;

    private final JwtService jwtService;

    private final ArticleDtoMapper articleDtoMapper;

    public Mono<PaginatedResponseDto<ArticleShortResponseDto>> getPaginatedShortArticle(int offset, int limit) {
        return db.execAsync(ctx -> articleRepository.getPaginatedShortArticle(ctx, offset, limit));

    }

    public Mono<ArticleResponseDto> getArticleById(Long articleId) {
        return db.execAsync(ctx -> articleRepository.getById(ctx, articleId)).map(articleDtoMapper::map);
    }

    public Mono<Void> createArticle(String authToken, ArticleRequestDto requestDto) {
        var jwt = authToken.substring(7);
        var userId = Long.parseLong(jwtService.getUserIdFromJwt(jwt));

        return db.execAsync(ctx -> {
            var newArticleId = articleRepository.createArticle(ctx, articleDtoMapper.mapToCreate(requestDto)).getId();
            var authorId = authorRepository.getByUserId(ctx, userId).getId();
            authorArticleRepository.createAuthorArticle(ctx, authorId, newArticleId);
            return null;
        });

    }
}
