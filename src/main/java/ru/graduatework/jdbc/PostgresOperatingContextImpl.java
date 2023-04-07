package ru.graduatework.jdbc;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
public class PostgresOperatingContextImpl implements PostgresOperatingContext {

    private final DSLContext ctx;

    @Autowired
    public PostgresOperatingContextImpl(@Qualifier("pgOdbDslContext") DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public DSLContext dsl() {
        return ctx;
    }

}