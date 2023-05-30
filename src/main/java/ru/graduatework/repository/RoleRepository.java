package ru.graduatework.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.RoleRecord;
import ru.graduatework.common.Role;


import static ru.graduatework.jooq.Tables.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RoleRepository {

    public Role getRoleByUserId(PostgresOperatingContext ctx, Long id) {
        var role = ctx.dsl()
                .selectFrom(ROLE.join(USER_ROLE).on(ROLE.ID.eq(USER_ROLE.ROLE_ID).and(USER_ROLE.USER_ID.eq(id))))
                .fetchOneInto(RoleRecord.class);
        return Role.valueOf(role.getName());
    }
}
