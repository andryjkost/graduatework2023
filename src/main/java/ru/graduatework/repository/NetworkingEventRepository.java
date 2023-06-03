package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import ru.graduatework.common.NetworkingEventPaginatedFilter;
import ru.graduatework.common.NetworkingEventStatus;
import ru.graduatework.common.Utils;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.AuthorNetworkingEventRecord;
import ru.graduatework.jooq.tables.records.NetworkingEventRecord;
import ru.graduatework.model.AuthorShortModel;
import ru.graduatework.model.NetworkingEventModel;

import javax.persistence.Tuple;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static ru.graduatework.common.NetworkingEventStatus.*;
import static ru.graduatework.jooq.Tables.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NetworkingEventRepository {


    public String getAvatarPathById(PostgresOperatingContext ctx, UUID id) {
        return ctx.dsl().select(NETWORKING_EVENT.PATH_AVATAR)
                .from(NETWORKING_EVENT)
                .where(NETWORKING_EVENT.ID.eq(id))
                .fetchOneInto(String.class);
    }

    public NetworkingEventModel getById(PostgresOperatingContext ctx, UUID id) {
        return ctx.dsl().select(NETWORKING_EVENT.asterisk(), AUTHOR.ID, AUTHOR.LAST_NAME, AUTHOR.FIRST_NAME)
                .from(NETWORKING_EVENT
                        .leftJoin(AUTHOR_NETWORKING_EVENT).on(NETWORKING_EVENT.ID.eq(AUTHOR_NETWORKING_EVENT.NETWORKING_EVENT_ID))
                        .leftJoin(AUTHOR).on(AUTHOR_NETWORKING_EVENT.AUTHOR_ID.eq(AUTHOR.ID)))
                .where(NETWORKING_EVENT.ID.eq(id))
                .fetchOne(record -> NetworkingEventModel.builder()
                        .id((UUID) record.get(0))
                        .title((String) record.get(1))
                        .description((String) record.get(2))
                        .link((String) record.get(3))
                        .startTime((OffsetDateTime) record.get(4))
                        .status(NetworkingEventStatus.valueOf((String) record.get(5)))
                        .maximumNumberOfParticipants((Long) record.get(6))
                        .numberOfAvailableSeats((Long) record.get(7))
                        .pathToAvatar((String) record.get(8))
                        .authorShortModel(AuthorShortModel.builder()
                                .id((UUID) record.get(9))
                                .firstLastName(Utils.getFullName((String) record.get(10), (String) record.get(11)))
                                .build())
                        .build());
    }

    public Tuple2<Integer, List<NetworkingEventModel>> getPaginatedListOfEvents(PostgresOperatingContext ctx, NetworkingEventPaginatedFilter filter) {
        var filterCondition = DSL.noCondition();
        if (filter.getStatus() != null) {
            filterCondition = filterCondition.and(NETWORKING_EVENT.STATUS.eq(filter.getStatus().name()));
        }

        if (filter.getEventSubscriptionFlag() != null) {
            if (filter.getEventSubscriptionFlag()) {
                filterCondition = filterCondition.and(USER_NETWORKING_EVENT.USER_ID.eq(filter.getUserId()));
            } else {
                filterCondition = filterCondition.and(USER_NETWORKING_EVENT.USER_ID.notEqual(filter.getUserId()));
            }
        }

        var selectQuery = ctx.dsl().select(NETWORKING_EVENT.asterisk(), AUTHOR.ID, AUTHOR.LAST_NAME, AUTHOR.FIRST_NAME)
                .from(NETWORKING_EVENT
                        .leftJoin(AUTHOR_NETWORKING_EVENT).on(NETWORKING_EVENT.ID.eq(AUTHOR_NETWORKING_EVENT.NETWORKING_EVENT_ID))
                        .leftJoin(AUTHOR).on(AUTHOR_NETWORKING_EVENT.AUTHOR_ID.eq(AUTHOR.ID))
                        .leftJoin(USER_NETWORKING_EVENT).on(NETWORKING_EVENT.ID.eq(USER_NETWORKING_EVENT.NETWORKING_EVENT_ID)));

        var totalCount = ctx.dsl()
                .selectCount()
                .from(selectQuery)
                .where(filterCondition)
                .fetchOneInto(Integer.class);

        var listNetworkingEvent = selectQuery
                .where(filterCondition)
                .orderBy(
                        NETWORKING_EVENT.START_TIME.asc(),
                        DSL.case_()
                                .when(NETWORKING_EVENT.STATUS.eq(PASSED.name()), 0)
                                .when(NETWORKING_EVENT.STATUS.eq(IN_PROCESS.name()), 1)
                                .otherwise(2)
                )
                .offset(filter.getOffset())
                .limit(filter.getLimit() > 0 ? filter.getLimit() : null)
                .fetch().map(record -> NetworkingEventModel.builder()
                        .id((UUID) record.get(0))
                        .title((String) record.get(1))
                        .description((String) record.get(2))
                        .link((String) record.get(3))
                        .startTime((OffsetDateTime) record.get(4))
                        .status(NetworkingEventStatus.valueOf((String) record.get(5)))
                        .maximumNumberOfParticipants((Long) record.get(6))
                        .numberOfAvailableSeats((Long) record.get(7))
                        .pathToAvatar((String) record.get(8))
                        .authorShortModel(AuthorShortModel.builder()
                                .id((UUID) record.get(9))
                                .firstLastName(Utils.getFullName((String) record.get(10), (String) record.get(11)))
                                .build())
                        .build());

        return Tuples.of(totalCount, listNetworkingEvent);
    }

    public void createNetworkingEvent(PostgresOperatingContext ctx, NetworkingEventRecord networkingEventRecord, UUID authorId) {

        var newNetworkingEvent = ctx.dsl().insertInto(NETWORKING_EVENT).set(networkingEventRecord).returning().fetchOne();


        AuthorNetworkingEventRecord newAuthorNetworkingEventRecord = new AuthorNetworkingEventRecord();
        newAuthorNetworkingEventRecord.setId(UUID.randomUUID());
        newAuthorNetworkingEventRecord.setAuthorId(authorId);
        newAuthorNetworkingEventRecord.setNetworkingEventId(newNetworkingEvent.getId());

        ctx.dsl().insertInto(AUTHOR_NETWORKING_EVENT).set(newAuthorNetworkingEventRecord).execute();
    }

    public void addAvatar(PostgresOperatingContext ctx, String path, UUID id) {
        ctx.dsl().update(NETWORKING_EVENT)
                .set(NETWORKING_EVENT.PATH_AVATAR, path)
                .where(NETWORKING_EVENT.ID.eq(id)).execute();

    }

    public boolean update(PostgresOperatingContext ctx, NetworkingEventRecord networkingEventRecord, UUID id) {
        return ctx.dsl().update(NETWORKING_EVENT)
                .set(networkingEventRecord)
                .where(NETWORKING_EVENT.ID.eq(id))
                .execute() == 1;
    }
}
