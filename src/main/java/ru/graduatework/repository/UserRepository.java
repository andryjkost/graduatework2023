package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.UserRecord;
import static ru.graduatework.jooq.tables.User.USER;

import static java.util.Objects.isNull;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {

    public UserRecord save(PostgresOperatingContext ctx, UserRecord record) {
        return ctx.dsl().insertInto(USER).set(record).returning().fetchOne();
    }

    public UserRecord getById(PostgresOperatingContext ctx, Long id) {
        var result = ctx.dsl().selectFrom(USER)
                .where(USER.ID.eq(id))
                .fetchOne();
        if (isNull(result)) {
            log.error("User with id {} not found", id);
//            throw errorHelper.createSyncArgs(USER_NOT_FOUND, BusinessErrorParameter.create(UserErrorArg.ID, id.toString()));
        }
        return result;
    }

    public UserRecord getByEmail(PostgresOperatingContext ctx, String email) {
        return  ctx.dsl().selectFrom(USER)
                .where(USER.EMAIL.eq(email))
                .fetchOne();
    }

}
