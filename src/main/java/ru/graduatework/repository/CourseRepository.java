package ru.graduatework.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.CourseRecord;

import java.util.UUID;

import static ru.graduatework.jooq.Tables.COURSE;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CourseRepository {

    public void addAvatar(PostgresOperatingContext ctx, String path, UUID id) {
        ctx.dsl().update(COURSE)
                .set(COURSE.PATH_AVATAR, path)
                .where(COURSE.ID.eq(id)).execute();

    }

    public Boolean create(PostgresOperatingContext ctx, CourseRecord courseRecord){
        return ctx.dsl().insertInto(COURSE)
                .set(courseRecord)
                .execute() == 1;

    }

    public CourseRecord getById(PostgresOperatingContext ctx, UUID id){
        return ctx.dsl().selectFrom(COURSE)
                .where(COURSE.ID.eq(id))
                .fetchOneInto(CourseRecord.class);
    }
}
