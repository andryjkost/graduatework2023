package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.graduatework.common.NetworkingEventPaginatedFilter;
import ru.graduatework.common.NetworkingEventStatus;
import ru.graduatework.common.Utils;
import ru.graduatework.controller.dto.NetworkingEventRequestDto;
import ru.graduatework.controller.dto.NetworkingEventResponseDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.AuthorNetworkingEventRecord;
import ru.graduatework.jooq.tables.records.NetworkingEventRecord;
import ru.graduatework.mapper.NetworkingEventDtoMapper;
import ru.graduatework.model.AuthorShortModel;

import java.time.OffsetDateTime;
import java.util.UUID;

import static ru.graduatework.common.NetworkingEventStatus.*;
import static ru.graduatework.jooq.Tables.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NetworkingEventRepository {

    public NetworkingEventResponseDto getById(PostgresOperatingContext ctx, Long id) {
        return ctx.dsl().select(NETWORKING_EVENT.asterisk(), AUTHOR.ID, AUTHOR.LAST_NAME, AUTHOR.FIRST_NAME)
                .from(NETWORKING_EVENT
                        .leftJoin(AUTHOR_NETWORKING_EVENT).on(NETWORKING_EVENT.ID.eq(AUTHOR_NETWORKING_EVENT.NETWORKING_EVENT_ID))
                        .leftJoin(AUTHOR).on(AUTHOR_NETWORKING_EVENT.AUTHOR_ID.eq(AUTHOR.ID)))
                .where(NETWORKING_EVENT.ID.eq(id))
                .fetchOne(record -> NetworkingEventResponseDto.builder()
                        .id((Long) record.get(0))
                        .title((String) record.get(1))
                        .description((String) record.get(2))
                        .link((String) record.get(3))
                        .startTime((OffsetDateTime) record.get(4))
                        .status(NetworkingEventStatus.valueOf((String) record.get(5)))
                        .maximumNumberOfParticipants((Long) record.get(6))
                        .numberOfAvailableSeats((Long) record.get(7))
                        .authorShortModel(AuthorShortModel.builder()
                                .id((Long) record.get(8))
                                .firstLastName(Utils.getFullName((String) record.get(9), (String) record.get(10)))
                                .build())
                        .build());
    }

    public PaginatedResponseDto<NetworkingEventResponseDto> getPaginatedListOfEvents(PostgresOperatingContext ctx, NetworkingEventPaginatedFilter filter) {
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
                .fetch().map(record -> NetworkingEventResponseDto.builder()
                        .id((Long) record.get(0))
                        .title((String) record.get(1))
                        .description((String) record.get(2))
                        .link((String) record.get(3))
                        .startTime((OffsetDateTime) record.get(4))
                        .status(NetworkingEventStatus.valueOf((String) record.get(5)))
                        .maximumNumberOfParticipants((Long) record.get(6))
                        .numberOfAvailableSeats((Long) record.get(7))
                        .authorShortModel(AuthorShortModel.builder()
                                .id((Long) record.get(8))
                                .firstLastName(Utils.getFullName((String) record.get(9), (String) record.get(10)))
                                .build())
                        .build());


        return PaginatedResponseDto.<NetworkingEventResponseDto>builder()
                .totalCount(totalCount)
                .count(listNetworkingEvent.size())
                .result(listNetworkingEvent)
                .build();
    }

    public NetworkingEventRecord createNetworkingEvent(PostgresOperatingContext ctx, NetworkingEventRequestDto requestDto, Long authorId) {

        NetworkingEventRecord newNetworkingEvent = new NetworkingEventRecord();

        newNetworkingEvent.setId(UUID.randomUUID().getLeastSignificantBits());
        newNetworkingEvent.setTitle(requestDto.getTitle());
        newNetworkingEvent.setDescription(requestDto.getDescription());
        newNetworkingEvent.setLink(requestDto.getLink());
        newNetworkingEvent.setStartTime(requestDto.getStartTime());

        newNetworkingEvent.setStatus(TO_BE.name());

        newNetworkingEvent.setMaximumNumberOfParticipants(requestDto.getMaximumNumberOfParticipants());
        newNetworkingEvent.setNumberOfAvailableSeats(requestDto.getMaximumNumberOfParticipants());


        newNetworkingEvent = ctx.dsl().insertInto(NETWORKING_EVENT).set(newNetworkingEvent).returning().fetchOne();


        AuthorNetworkingEventRecord newAuthorNetworkingEventRecord = new AuthorNetworkingEventRecord();

        newAuthorNetworkingEventRecord.setId(UUID.randomUUID().getLeastSignificantBits());
        newAuthorNetworkingEventRecord.setAuthorId(authorId);
        newAuthorNetworkingEventRecord.setNetworkingEventId(newNetworkingEvent.getId());

        ctx.dsl().insertInto(AUTHOR_NETWORKING_EVENT).set(newAuthorNetworkingEventRecord).execute();

        return newNetworkingEvent;

    }

}
