package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.TopicRecord;

import java.util.List;
import java.util.UUID;

import static ru.graduatework.jooq.tables.ChapterTopic.CHAPTER_TOPIC;
import static ru.graduatework.jooq.tables.CourseTopic.COURSE_TOPIC;
import static ru.graduatework.jooq.tables.Topic.TOPIC;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TopicRepository {

    public void create(PostgresOperatingContext ctx, TopicRecord topicRecord) {
        ctx.dsl().insertInto(TOPIC).set(topicRecord).execute();
    }

    public List<TopicRecord> getByChapterId(PostgresOperatingContext ctx, UUID chapterId) {
        return ctx.dsl().select(TOPIC.asterisk())
                .from(TOPIC
                        .leftJoin(CHAPTER_TOPIC).on(TOPIC.ID.eq(CHAPTER_TOPIC.TOPIC_ID)))
                .where(CHAPTER_TOPIC.CHAPTER_ID.eq(chapterId))
                .orderBy(TOPIC.PRIORITY_NUMBER_OF_THE_SECTION.asc())
                .fetchInto(TopicRecord.class);
    }

    public List<TopicRecord> getByCourseId(PostgresOperatingContext ctx, UUID courseId) {
        return ctx.dsl().select(TOPIC.asterisk())
                .from(TOPIC
                        .leftJoin(COURSE_TOPIC).on(TOPIC.ID.eq(COURSE_TOPIC.TOPIC_ID)))
                .where(COURSE_TOPIC.COURSE_ID.eq(courseId))
                .orderBy(TOPIC.PRIORITY_NUMBER_OF_THE_SECTION.asc())
                .fetchInto(TopicRecord.class);
    }
}
