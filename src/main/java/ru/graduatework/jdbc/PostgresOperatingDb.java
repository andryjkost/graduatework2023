package ru.graduatework.jdbc;

import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface PostgresOperatingDb {
    <T> Mono<T> execAsync(Function<PostgresOperatingContext, T> function);

    <T> Mono<T> transactionalExecAsync(Function<PostgresOperatingContext, T> function);

    <T> T execute(Function<PostgresOperatingContext, T> function);

    <T> T transactionalExecute(Function<PostgresOperatingContext, T> function);
}
