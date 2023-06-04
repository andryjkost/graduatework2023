package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.ChapterRecord;

import static ru.graduatework.jooq.tables.Chapter.CHAPTER;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChapterRepository {

    public boolean create(PostgresOperatingContext ctx, ChapterRecord chapterRecord) {
        return ctx.dsl().insertInto(CHAPTER).set(chapterRecord).execute() == 1;
    }

}
