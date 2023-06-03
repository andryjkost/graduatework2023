package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.AuthorRecord;

import java.util.UUID;

import static ru.graduatework.jooq.Tables.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuthorRepository {

    public AuthorRecord createAuthor(PostgresOperatingContext ctx, AuthorRecord authorRecord){
        return ctx.dsl().insertInto(AUTHOR).set(authorRecord).returning().fetchOne();
    }

    public AuthorRecord getByUserId(PostgresOperatingContext ctx, UUID userId) {
        return ctx.dsl().selectFrom(AUTHOR)
                .where(AUTHOR.USER_ID.eq(userId))
                .fetchOneInto(AuthorRecord.class);
    }

}
