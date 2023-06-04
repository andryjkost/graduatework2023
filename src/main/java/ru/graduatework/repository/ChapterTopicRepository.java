package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.ChapterTopicRecord;

import java.util.UUID;

import static ru.graduatework.jooq.tables.ChapterTopic.CHAPTER_TOPIC;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChapterTopicRepository {

    public void create(PostgresOperatingContext ctx, UUID topicId, UUID chapterId){
        ctx.dsl().insertInto(CHAPTER_TOPIC).set(new ChapterTopicRecord(UUID.randomUUID(), topicId, chapterId)).execute();
    }
}
