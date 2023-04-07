package ru.graduatework.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.entity.Role;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.RoleRecord;

import java.util.List;
import java.util.stream.Collectors;

import static ru.graduatework.jooq.Tables.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RoleRepository {

    public List<Role> getListRoleByUserId(PostgresOperatingContext ctx, Long id){
        return ctx.dsl()
                .selectFrom(ROLE.join(USER_ROLES).on(ROLE.ID.eq(USER_ROLES.ROLE_ID).and(USER_ROLES.USER_ID.eq(id))))
                .fetchInto(RoleRecord.class).stream().map(record -> Role.valueOf(record.getName())).collect(Collectors.toList());
    }
}
