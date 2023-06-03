package ru.graduatework.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.graduatework.config.JwtService;
import ru.graduatework.controller.dto.*;
import ru.graduatework.error.CommonException;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.mapper.ArticleDtoMapper;
import ru.graduatework.repository.ArticleRepository;
import ru.graduatework.repository.AuthorArticleRepository;
import ru.graduatework.repository.AuthorRepository;

import java.util.Objects;
import java.util.UUID;

import static ru.graduatework.error.Code.NOT_AUTHOR_THIS_ARTICLE;

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

    public Mono<Boolean> updateArticle(String authToken, UpdateArticleRequestDto requestDto) {
        var userId = jwtService.getUserIdByToken(authToken.substring(7));
        var articleRecord = articleDtoMapper.mapToUpdate(requestDto);
        return db.execAsync(ctx -> {
            var authorId = authorRepository.getByUserId(ctx, userId).getId();
            var articleModel = articleRepository.getById(ctx, articleRecord.getId());
            if (authorId.equals(articleModel.getAuthorShortModel().getId())) {
                if (articleRecord.getTitle() == null || Objects.equals(articleRecord.getTitle(), "")) {
                    articleRecord.setTitle(articleModel.getTitle());
                }
                if (articleRecord.getTextArticle() == null || Objects.equals(articleRecord.getTextArticle(), "")) {
                    articleRecord.setTextArticle(articleModel.getTextArticle());
                }

                articleRepository.update(ctx, articleRecord, articleRecord.getId());
            } else {
                throw CommonException.builder().code(NOT_AUTHOR_THIS_ARTICLE).userMessage("Данный пользователь не является автором статьи и не может ее редактировать").techMessage("This user is not the author of the article and cannot edit it").httpStatus(HttpStatus.BAD_REQUEST).build();
            }
            return true;
        });
    }

    public Mono<PaginatedResponseDto<ArticleShortResponseDto>> getPaginatedShortArticle(int offset, int limit) {
        return db.execAsync(ctx -> articleRepository.getPaginatedShortArticle(ctx, offset, limit));

    }

    public Mono<ArticleResponseDto> getArticleById(UUID articleId) {
        return db.execAsync(ctx -> articleRepository.getById(ctx, articleId)).map(articleDtoMapper::map);
    }

    public Mono<Void> createArticle(String authToken, ArticleRequestDto requestDto) {
        var userId = jwtService.getUserIdByToken(authToken.substring(7));

        return db.execAsync(ctx -> {
            var newArticleId = articleRepository.createArticle(ctx, articleDtoMapper.mapToCreate(requestDto)).getId();
            var authorId = authorRepository.getByUserId(ctx, userId).getId();
            authorArticleRepository.createAuthorArticle(ctx, authorId, newArticleId);
            return null;
        });

    }
}
