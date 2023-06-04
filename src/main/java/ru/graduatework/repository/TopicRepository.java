package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.TopicRecord;

import static ru.graduatework.jooq.tables.Topic.TOPIC;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TopicRepository {

    public void create(PostgresOperatingContext ctx, TopicRecord topicRecord){
        ctx.dsl().insertInto(TOPIC).set(topicRecord).execute();
    }
}
