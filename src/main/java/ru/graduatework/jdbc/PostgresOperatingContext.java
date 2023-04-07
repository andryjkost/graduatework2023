package ru.graduatework.jdbc;

import org.jooq.DSLContext;

public interface PostgresOperatingContext {
    DSLContext dsl();
}
