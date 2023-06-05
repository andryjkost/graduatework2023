package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.CourseTopicRecord;

import java.util.UUID;

import static ru.graduatework.jooq.tables.CourseTopic.COURSE_TOPIC;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CourseTopicRepository {
    public void create(PostgresOperatingContext ctx, UUID topicId, UUID courseId) {
        ctx.dsl().insertInto(COURSE_TOPIC).set(new CourseTopicRecord(UUID.randomUUID(), topicId, courseId)).execute();
    }
}
