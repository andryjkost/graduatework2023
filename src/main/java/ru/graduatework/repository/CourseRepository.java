package ru.graduatework.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import ru.graduatework.common.Utils;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.CourseRecord;
import ru.graduatework.model.AuthorShortModel;
import ru.graduatework.model.CourseShortModel;

import java.util.List;
import java.util.UUID;

import static ru.graduatework.jooq.Tables.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CourseRepository {

    public void addAvatar(PostgresOperatingContext ctx, String path, UUID id) {
        ctx.dsl().update(COURSE)
                .set(COURSE.PATH_AVATAR, path)
                .where(COURSE.ID.eq(id)).execute();

    }

    public Boolean create(PostgresOperatingContext ctx, CourseRecord courseRecord) {
        return ctx.dsl().insertInto(COURSE)
                .set(courseRecord)
                .execute() == 1;

    }

    public CourseRecord getById(PostgresOperatingContext ctx, UUID id) {
        return ctx.dsl().selectFrom(COURSE)
                .where(COURSE.ID.eq(id))
                .fetchOneInto(CourseRecord.class);
    }

    public Tuple2<List<CourseShortModel>, Integer> getPaginated(PostgresOperatingContext ctx, int offset, int limit) {

        var filterCondition = DSL.noCondition();

        var selectQuery = ctx.dsl()
                .select(COURSE.ID, COURSE.TITLE, COURSE.DESCRIPTION, COURSE.CATEGORY,
                        COURSE.PATH_AVATAR, AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .from(COURSE
                        .leftJoin(AUTHOR_COURSE).on(COURSE.ID.eq(AUTHOR_COURSE.COURSE_ID))
                        .leftJoin(AUTHOR).on(AUTHOR_COURSE.AUTHOR_ID.eq(AUTHOR.ID)))
                .where(filterCondition);


        var totalCount = ctx.dsl()
                .selectCount()
                .from(selectQuery)
                .fetchOneInto(Integer.class);

        var result = selectQuery
                .offset(offset)
                .limit(limit > 0 ? limit : null)
                .fetch(record -> CourseShortModel.builder()
                .id(record.get(COURSE.ID))
                .title(record.get(COURSE.TITLE))
                .description(record.get(COURSE.DESCRIPTION))
                .category(record.get(COURSE.CATEGORY))
                .pathToAvatar(record.get(COURSE.PATH_AVATAR))
                .authorShortModel(AuthorShortModel.builder()
                        .id(record.get(AUTHOR.ID))
                        .firstLastName(Utils.getFullName(record.get(AUTHOR.LAST_NAME), record.get(AUTHOR.FIRST_NAME)))
                        .build())
                .build());

        return Tuples.of(result,totalCount);
    }
}
