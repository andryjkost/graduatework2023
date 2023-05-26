package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.graduatework.common.NetworkingEventPaginatedFilter;
import ru.graduatework.controller.dto.NetworkingEventResponseDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.NetworkingEventRecord;
import ru.graduatework.mapper.NetworkingEventDtoMapper;

import static ru.graduatework.common.NetworkingEventStatus.IN_PROCESS;
import static ru.graduatework.common.NetworkingEventStatus.PASSED;
import static ru.graduatework.jooq.Tables.NETWORKING_EVENT;

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

        var selectQuery = ctx.dsl().selectFrom(NETWORKING_EVENT);

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
                .limit(filter.getLimit() > 0 ? filter.getLimit() : null).fetchInto(NetworkingEventRecord.class);

        var result = mapper.map(listNetworkingEvent);

        return PaginatedResponseDto.<NetworkingEventResponseDto>builder()
                .totalCount(totalCount)
                .count(result.size())
                .result(result)
                .build();
    }
}
