package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.AuthorArticleRecord;

import java.util.UUID;

import static ru.graduatework.jooq.Tables.AUTHOR_ARTICLE;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuthorArticleRepository {

    public void createAuthorArticle(PostgresOperatingContext ctx, UUID authorId, UUID articleId) {
        ctx.dsl().insertInto(AUTHOR_ARTICLE).set(
                new AuthorArticleRecord(UUID.randomUUID(), authorId, articleId)
        ).execute();
    }

    public AuthorArticleRecord getByAuthorIdAndCourseId(PostgresOperatingContext ctx, UUID authorId, UUID articleId) {
        return ctx.dsl().selectFrom(AUTHOR_ARTICLE)
                .where(AUTHOR_ARTICLE.AUTHOR_ID.eq(authorId).and(AUTHOR_ARTICLE.ARTICLE_ID.eq(articleId))).fetchOne();
    }
}
