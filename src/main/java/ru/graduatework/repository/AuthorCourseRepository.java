package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.AuthorCourseRecord;

import java.util.UUID;

import static ru.graduatework.jooq.Tables.AUTHOR_COURSE;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuthorCourseRepository {

    public void createAuthorCourse(PostgresOperatingContext ctx, UUID authorId, UUID courseId) {
        ctx.dsl().insertInto(AUTHOR_COURSE).set(
                new AuthorCourseRecord(UUID.randomUUID(), authorId, courseId)
        ).execute();
    }

    public AuthorCourseRecord getByAuthorIdAndCourseId(PostgresOperatingContext ctx, UUID authorId, UUID courseId){
        return ctx.dsl().selectFrom(AUTHOR_COURSE)
                .where(AUTHOR_COURSE.AUTHOR_ID.eq(authorId), AUTHOR_COURSE.COURSE_ID.eq(courseId))
                .fetchOne();
    }
}
