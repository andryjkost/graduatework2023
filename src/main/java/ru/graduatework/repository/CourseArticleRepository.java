package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.CourseArticleRecord;

import java.util.UUID;

import static ru.graduatework.jooq.tables.CourseArticle.COURSE_ARTICLE;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CourseArticleRepository {
    public boolean create(PostgresOperatingContext ctx, UUID courseId, UUID articleId) {
        return ctx.dsl().insertInto(COURSE_ARTICLE).set(new CourseArticleRecord(UUID.randomUUID(), courseId, articleId)).execute() == 1;
    }

    public boolean checkLinking(PostgresOperatingContext ctx, UUID courseId, UUID articleId) {
        return ctx.dsl().selectFrom(COURSE_ARTICLE)
                .where(COURSE_ARTICLE.COURSE_ID.eq(courseId).and(COURSE_ARTICLE.ARTICLE_ID.eq(articleId))).fetchOne() == null;
    }
}
