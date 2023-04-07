package ru.graduatework.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.function.Function;

@Component
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
public class PostgresOperatingDbImpl implements PostgresOperatingDb {
    private final PostgresOperatingContext postgresContext;

    private final Scheduler scheduler;

    public PostgresOperatingDbImpl(PostgresOperatingContext postgresContext, @Qualifier("jdbcPgOdbScheduler") Scheduler scheduler) {
        this.postgresContext = postgresContext;
        this.scheduler = scheduler;
    }

    @Override
    public <T> Mono<T> execAsync(Function<PostgresOperatingContext, T> function) {
        return Mono.fromCallable(() -> execute(function)).publishOn(scheduler);
    }

    @Override
    public <T> Mono<T> transactionalExecAsync(Function<PostgresOperatingContext, T> function) {
        return Mono.fromCallable(() -> transactionalExecute(function)).publishOn(scheduler);
    }

    @Override
    public <T> T execute(Function<PostgresOperatingContext, T> function) {
        return function.apply(postgresContext);
    }

    @Override
    public <T> T transactionalExecute(Function<PostgresOperatingContext, T> function) {
        return postgresContext.dsl().transactionResult(config -> {
            var transactionalContext = new PostgresOperatingContextImpl(config.dsl());
            return function.apply(transactionalContext);
        });
    }
}
