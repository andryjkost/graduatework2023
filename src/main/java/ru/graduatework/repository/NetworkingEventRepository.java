package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
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
        return ctx.dsl().select(
                        NETWORKING_EVENT.ID,
                        NETWORKING_EVENT.TITLE,
                        NETWORKING_EVENT.DESCRIPTION,
                        NETWORKING_EVENT.LINK,
                        NETWORKING_EVENT.START_TIME,
                        NETWORKING_EVENT.STATUS,
                        NETWORKING_EVENT.MAXIMUM_NUMBER_OF_PARTICIPANTS,
                        NETWORKING_EVENT.NUMBER_OF_AVAILABLE_SEATS,
                        NETWORKING_EVENT.PATH_AVATAR,
                        AUTHOR.ID, AUTHOR.LAST_NAME, AUTHOR.FIRST_NAME)
                .from(NETWORKING_EVENT
                        .leftJoin(AUTHOR_NETWORKING_EVENT).on(NETWORKING_EVENT.ID.eq(AUTHOR_NETWORKING_EVENT.NETWORKING_EVENT_ID))
                        .leftJoin(AUTHOR).on(AUTHOR_NETWORKING_EVENT.AUTHOR_ID.eq(AUTHOR.ID)))
                .where(NETWORKING_EVENT.ID.eq(id))
                .fetchOne(record -> NetworkingEventModel.builder()
                        .id(record.get(NETWORKING_EVENT.ID))
                        .title(record.get(NETWORKING_EVENT.TITLE))
                        .description(record.get(NETWORKING_EVENT.DESCRIPTION))
                        .link(record.get(NETWORKING_EVENT.LINK))
                        .startTime(record.get(NETWORKING_EVENT.START_TIME))
                        .status(NetworkingEventStatus.valueOf((record.get(NETWORKING_EVENT.STATUS))))
                        .maximumNumberOfParticipants(record.get(NETWORKING_EVENT.MAXIMUM_NUMBER_OF_PARTICIPANTS))
                        .numberOfAvailableSeats(record.get(NETWORKING_EVENT.NUMBER_OF_AVAILABLE_SEATS))
                        .pathToAvatar((record.get(NETWORKING_EVENT.PATH_AVATAR)))
                        .authorShortModel(AuthorShortModel.builder()
                                .id(record.get(AUTHOR.ID))
                                .firstLastName(Utils.getFullName(record.get(AUTHOR.LAST_NAME), record.get(AUTHOR.FIRST_NAME)))
                                .build())
                        .build());
    }

    public Tuple2<Integer, List<NetworkingEventModel>> getPaginatedListOfEvents(PostgresOperatingContext ctx, NetworkingEventPaginatedFilter filter) {

        var userIdAlias = "USER_ID";

        var filterCondition = DSL.noCondition();

        var selectQuery = ctx.dsl()
                .select(NETWORKING_EVENT.ID,
                        NETWORKING_EVENT.TITLE, NETWORKING_EVENT.DESCRIPTION, NETWORKING_EVENT.LINK,
                        NETWORKING_EVENT.START_TIME, NETWORKING_EVENT.DURATION_OF_EVENT,
                        NETWORKING_EVENT.STATUS, NETWORKING_EVENT.STATUS,
                        NETWORKING_EVENT.MAXIMUM_NUMBER_OF_PARTICIPANTS, NETWORKING_EVENT.NUMBER_OF_AVAILABLE_SEATS,
                        NETWORKING_EVENT.PATH_AVATAR,
                        DSL.arrayAggDistinct(USER_NETWORKING_EVENT.USER_ID).as(userIdAlias),
                        AUTHOR.ID, AUTHOR.LAST_NAME, AUTHOR.FIRST_NAME)
                .from(NETWORKING_EVENT
                        .leftJoin(AUTHOR_NETWORKING_EVENT).on(NETWORKING_EVENT.ID.eq(AUTHOR_NETWORKING_EVENT.NETWORKING_EVENT_ID))
                        .leftJoin(AUTHOR).on(AUTHOR_NETWORKING_EVENT.AUTHOR_ID.eq(AUTHOR.ID))
                        .leftJoin(USER_NETWORKING_EVENT).on(NETWORKING_EVENT.ID.eq(USER_NETWORKING_EVENT.NETWORKING_EVENT_ID)));


        if (filter.getStatus() != null) {
            filterCondition = filterCondition.and(NETWORKING_EVENT.STATUS.eq(filter.getStatus().name()));
        }

        if (filter.getEventSubscriptionFlag() != null) {
            if (filter.getEventSubscriptionFlag()) {
                filterCondition = filterCondition.and(PostgresDSL.arrayOverlap(selectQuery.field(userIdAlias, UUID[].class), new UUID[]{filter.getUserId()}));
            } else {
                filterCondition = filterCondition.and(DSL.not(PostgresDSL.arrayOverlap(selectQuery.field(userIdAlias, UUID[].class), new UUID[]{filter.getUserId()})));
            }
        }


        var totalCount = ctx.dsl()
                .selectCount()
                .from(selectQuery.where(filterCondition).groupBy(NETWORKING_EVENT.ID, AUTHOR.ID))
                .fetchOneInto(Integer.class);

        var listNetworkingEvent = selectQuery
                .where(filterCondition)
                .groupBy(NETWORKING_EVENT.ID, AUTHOR.ID)
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
                        .id(record.get(NETWORKING_EVENT.ID))
                        .title(record.get(NETWORKING_EVENT.TITLE))
                        .description(record.get(NETWORKING_EVENT.DESCRIPTION))
                        .link(record.get(NETWORKING_EVENT.LINK))
                        .startTime(record.get(NETWORKING_EVENT.START_TIME))
                        .status(NetworkingEventStatus.valueOf(record.get(NETWORKING_EVENT.STATUS)))
                        .maximumNumberOfParticipants(record.get(NETWORKING_EVENT.MAXIMUM_NUMBER_OF_PARTICIPANTS))
                        .numberOfAvailableSeats(record.get(NETWORKING_EVENT.NUMBER_OF_AVAILABLE_SEATS))
                        .pathToAvatar(record.get(NETWORKING_EVENT.PATH_AVATAR))
                        .authorShortModel(AuthorShortModel.builder()
                                .id(record.get(AUTHOR.ID))
                                .firstLastName(Utils.getFullName(record.get(AUTHOR.LAST_NAME), record.get(AUTHOR.FIRST_NAME)))
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
