package ru.graduatework.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.controller.dto.FieldsOfActivityResponseDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.FieldOfActivityRecord;

import java.util.List;

import static ru.graduatework.jooq.Tables.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FieldOfActivityRepository {

    public List<FieldsOfActivityResponseDto> getListFieldOfActivityByUserId(PostgresOperatingContext ctx, Long userId) {
        return ctx.dsl()
                .selectFrom(FIELD_OF_ACTIVITY
                        .join(USER_FIELD_OF_ACTIVITY)
                        .on(FIELD_OF_ACTIVITY.ID
                                .eq(USER_FIELD_OF_ACTIVITY.FIELD_OF_ACTIVITY_ID)
                                .and(USER_FIELD_OF_ACTIVITY.USER_ID.eq(userId))))
                .fetchInto(FieldsOfActivityResponseDto.class);
    }

    public PaginatedResponseDto<FieldsOfActivityResponseDto> getAll(PostgresOperatingContext ctx, int offset, int limit) {
        var selectQuery = ctx.dsl()
                .selectFrom(FIELD_OF_ACTIVITY)
                .offset(offset);

        List<FieldsOfActivityResponseDto> result = limit != 0
                ? selectQuery.limit(limit).fetchInto(FieldsOfActivityResponseDto.class)
                : selectQuery.fetchInto(FieldsOfActivityResponseDto.class);

        return PaginatedResponseDto.<FieldsOfActivityResponseDto>builder()
                .result(result)
                .totalCount(result.size())
                .count(result.size())
                .build();
    }

    public Boolean create(PostgresOperatingContext ctx, FieldOfActivityRecord record) {
        return ctx.dsl().insertInto(FIELD_OF_ACTIVITY).set(record).execute() == 1;
    }

    public Boolean update(PostgresOperatingContext ctx, FieldOfActivityRecord record, Long id) {
        return ctx.dsl().update(FIELD_OF_ACTIVITY)
                .set(record)
                .where(FIELD_OF_ACTIVITY.ID.eq(id)).execute() == 1;
    }

    public FieldOfActivityRecord getById(PostgresOperatingContext ctx, Long id) {
        return ctx.dsl().selectFrom(FIELD_OF_ACTIVITY).where(FIELD_OF_ACTIVITY.ID.eq(id)).fetchOneInto(FieldOfActivityRecord.class);
    }
}
