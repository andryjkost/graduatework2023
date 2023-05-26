package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.graduatework.common.NetworkingEventPaginatedFilter;
import ru.graduatework.controller.dto.NetworkingEventRequestDto;
import ru.graduatework.controller.dto.NetworkingEventResponseDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.AuthorNetworkingEventRecord;
import ru.graduatework.jooq.tables.records.NetworkingEventRecord;
import ru.graduatework.mapper.NetworkingEventDtoMapper;

import java.util.UUID;

import static ru.graduatework.common.NetworkingEventStatus.*;
import static ru.graduatework.jooq.Tables.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NetworkingEventRepository {

    private final NetworkingEventDtoMapper mapper;

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

        var selectQuery = ctx.dsl().select(NETWORKING_EVENT.asterisk())
                .from(NETWORKING_EVENT
//                        .leftJoin(AUTHOR_NETWORKING_EVENT).on(NETWORKING_EVENT.ID.eq(AUTHOR_NETWORKING_EVENT.NETWORKING_EVENT_ID))
//                        .leftJoin(AUTHOR).on(AUTHOR_NETWORKING_EVENT.AUTHOR_ID.eq(AUTHOR.ID))
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
                .fetchInto(NetworkingEventRecord.class);

        //доделать добавление инфы автора
        var networkingEventIdWithAuthorId = ctx.dsl()
                .select(NETWORKING_EVENT.ID.as("networking_id"), AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .from(NETWORKING_EVENT)
                .leftJoin(AUTHOR_NETWORKING_EVENT).on(NETWORKING_EVENT.ID.eq(AUTHOR_NETWORKING_EVENT.NETWORKING_EVENT_ID))
                .leftJoin(AUTHOR).on(AUTHOR.ID.eq(AUTHOR_NETWORKING_EVENT.AUTHOR_ID))
                .fetch();

        var result = mapper.map(listNetworkingEvent);

        return PaginatedResponseDto.<NetworkingEventResponseDto>builder()
                .totalCount(totalCount)
                .count(result.size())
                .result(result)
                .build();
    }

    public NetworkingEventRecord createNetworkingEvent(PostgresOperatingContext ctx, NetworkingEventRequestDto requestDto, Long authorId){

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
