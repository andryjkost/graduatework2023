package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.common.Role;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.UserRecord;
import ru.graduatework.jooq.tables.records.UserRoleRecord;

import static ru.graduatework.jooq.Tables.ROLE;
import static ru.graduatework.jooq.Tables.USER_ROLE;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRoleRepository {

    public void setRoleForUser(PostgresOperatingContext ctx, Role roleName, Long userId) {
        var roleRecord = ctx.dsl().selectFrom(ROLE).where(ROLE.NAME.eq(roleName.toString())).fetchOne();
        ctx.dsl().insertInto(USER_ROLE).set(new UserRoleRecord(userId, roleRecord.getId())).execute();
    }

    public void dropRoleForUser(PostgresOperatingContext ctx, Long roleId, Long userId) {
        ctx.dsl().deleteFrom(USER_ROLE).where(USER_ROLE.ROLE_ID.eq(roleId).and(USER_ROLE.USER_ID.eq(userId))).execute();
    }

}
