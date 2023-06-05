package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.TokenRecord;

import java.util.UUID;

import static ru.graduatework.jooq.Tables.TOKEN;
import static ru.graduatework.jooq.Tables.USER;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TokenRepository {

    //У делита и гета добавить фильтр по истечению как в бд появится поле
    public void deleteByUserId(PostgresOperatingContext ctx, UUID userId){
        ctx.dsl().deleteFrom(TOKEN).where(TOKEN.USER_ID.eq(userId)).execute();
    }

    public TokenRecord getByToken(PostgresOperatingContext ctx, String token){
        return ctx.dsl().selectFrom(TOKEN).where(TOKEN.TOKEN_.eq(token)).fetchOne();
    }

    public TokenRecord getByUserEmail(PostgresOperatingContext ctx, String email){
        return ctx.dsl()
                .selectFrom(TOKEN.leftJoin(USER).on(TOKEN.USER_ID.eq(USER.ID).and(USER.EMAIL.eq(email))))
                .fetchOneInto(TokenRecord.class);
    }
    public TokenRecord save(PostgresOperatingContext ctx, TokenRecord record) {
        return ctx.dsl().insertInto(TOKEN).set(record).returning().fetchOne();
    }

}
