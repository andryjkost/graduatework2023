package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.UserNetworkingEventRecord;

import java.util.UUID;

import static ru.graduatework.jooq.tables.UserNetworkingEvent.USER_NETWORKING_EVENT;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserNetworkingEventRepository {

    public boolean create(PostgresOperatingContext ctx, UUID networkingEventId, UUID userId) {
        return ctx.dsl().insertInto(USER_NETWORKING_EVENT)
                .set(new UserNetworkingEventRecord(UUID.randomUUID(), userId, networkingEventId))
                .execute() == 1;
    }
}
