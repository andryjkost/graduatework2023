package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.entity.Role;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.RoleRecord;
import ru.graduatework.jooq.tables.records.UserRolesRecord;

import java.util.UUID;

import static ru.graduatework.jooq.Tables.ROLE;
import static ru.graduatework.jooq.Tables.USER_ROLES;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRoleRepository {

    public void setRoleForUser(PostgresOperatingContext ctx, Role roleName, Long userId) {
        var roleRecord = ctx.dsl().selectFrom(ROLE).where(ROLE.NAME.eq(roleName.toString())).fetchOne();
        ctx.dsl().insertInto(USER_ROLES).set(new UserRolesRecord(userId, roleRecord.getId())).execute();
    }

    public void dropRoleForUser(PostgresOperatingContext ctx, Long roleId, Long userId) {
        ctx.dsl().deleteFrom(USER_ROLES).where(USER_ROLES.ROLE_ID.eq(roleId).and(USER_ROLES.USER_ID.eq(userId))).execute();
    }

}
