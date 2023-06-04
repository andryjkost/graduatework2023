package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.ChapterRecord;

import java.util.List;
import java.util.UUID;

import static ru.graduatework.jooq.tables.Chapter.CHAPTER;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChapterRepository {

    public boolean create(PostgresOperatingContext ctx, ChapterRecord chapterRecord) {
        return ctx.dsl().insertInto(CHAPTER).set(chapterRecord).execute() == 1;
    }

    public List<ChapterRecord> getByCourseId(PostgresOperatingContext ctx, UUID courseId){
        return ctx.dsl().selectFrom(CHAPTER)
                .where(CHAPTER.COURSE_ID.eq(courseId))
                .orderBy(CHAPTER.PRIORITY_NUMBER_OF_THE_SECTION_IN_THE_COURSE.asc())
                .fetch();
    }

}
