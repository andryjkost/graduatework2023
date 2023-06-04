package ru.graduatework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.graduatework.common.Role;
import ru.graduatework.config.JwtService;
import ru.graduatework.error.CommonException;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.repository.AuthorArticleRepository;
import ru.graduatework.repository.AuthorCourseRepository;
import ru.graduatework.repository.CourseArticleRepository;
import ru.graduatework.repository.SubscriptionRepository;

import java.util.UUID;

import static ru.graduatework.error.Code.NOT_AUTHOR_THIS_ARTICLE;
import static ru.graduatework.error.Code.NOT_AUTHOR_THIS_COURSE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final JwtService jwtService;
    private final UserService userService;
    private final AuthorService authorService;

    private final PostgresOperatingDb db;
    private final AuthorArticleRepository authorArticleRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AuthorCourseRepository authorCourseRepository;
    private final CourseArticleRepository courseArticleRepository;

    public Mono<Boolean> linkingArticleWithCourse(String authToken, UUID articleId, UUID courseId) {
        var idByToken = jwtService.getUserIdByToken(authToken.substring(7));
        return db.execAsync(ctx -> {
            var userByToken = userService.getById(idByToken);
            if (Role.AUTHOR.equals(userByToken.getRole())) {
                var author = authorService.getByUserId(idByToken);
                if (authorCourseRepository.getByAuthorIdAndCourseId(ctx, author.getId(), courseId) == null) {
                    log.error("The author with id:{} is not the creator of the course with id:{}", author.getId(), courseId);
                    throw CommonException.builder()
                            .code(NOT_AUTHOR_THIS_COURSE)
                            .userMessage("Автор не может связывать не свой курс")
                            .techMessage("The author cannot assign not to his course")
                            .httpStatus(HttpStatus.BAD_REQUEST).build();
                }
                if (authorArticleRepository.getByAuthorIdAndCourseId(ctx, author.getId(), articleId) == null) {
                    log.error("The author with id:{} is not the creator of the article with id:{}", author.getId(), articleId);
                    throw CommonException.builder()
                            .code(NOT_AUTHOR_THIS_ARTICLE)
                            .userMessage("Автор не может связывать не свою статью")
                            .techMessage("The author cannot assign not to his article")
                            .httpStatus(HttpStatus.BAD_REQUEST).build();
                }
            }

            var checkLinking = courseArticleRepository.checkLinking(ctx, articleId, courseId);

            if (checkLinking) {
                return courseArticleRepository.create(ctx, articleId, courseId);
            }
            log.info("The article with id: {} is already linked to the course with id: {}", articleId, courseId);
            return null;
        });
    }


    public Mono<Boolean> assignToCourseByUserId(String authToken, UUID userId, UUID courseId) {
        var idByToken = jwtService.getUserIdByToken(authToken.substring(7));
        return db.execAsync(ctx -> {
            var userByToken = userService.getById(idByToken);
            if (Role.AUTHOR.equals(userByToken.getRole())) {
                var author = authorService.getByUserId(idByToken);
                if (authorCourseRepository.getByAuthorIdAndCourseId(ctx, author.getId(), courseId) == null) {
                    log.error("The author with id:{} is not the creator of the course with id:{}", author.getId(), courseId);
                    throw CommonException.builder()
                            .code(NOT_AUTHOR_THIS_COURSE)
                            .userMessage("Автор не может назначать не на свой курс")
                            .techMessage("The author cannot assign not to his course")
                            .httpStatus(HttpStatus.BAD_REQUEST).build();
                }
            }

            var checkSubscription = subscriptionRepository.checkSubscription(ctx, userId, courseId);

            if (checkSubscription) {
                return subscriptionRepository.subscribe(ctx, userId, courseId);
            }
            log.info("User with id:{} has already been assigned a course with id:{}", userId, courseId);
            return null;
        });
    }

}
