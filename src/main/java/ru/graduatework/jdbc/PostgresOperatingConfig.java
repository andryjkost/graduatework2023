package ru.graduatework.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.sql.DataSource;
import java.util.concurrent.Executors;

@Configuration
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
public class PostgresOperatingConfig {

    private final Integer connectionPoolSize;

    public PostgresOperatingConfig(
            @Value("${spring.datasource.maximum-pool-size}") Integer connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    @Bean
    @Qualifier("pgOdbDataSourceProperties")
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties pgOdbDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Qualifier("pgOdbDataSource")
    public DataSource pgOdbDataSource(@Qualifier("pgOdbDataSourceProperties") DataSourceProperties dataSourceProperties) {
        HikariDataSource build = (HikariDataSource) dataSourceProperties
                .initializeDataSourceBuilder()
                .build();

        build.setMaxLifetime(600000);
        return build;
    }

    @Bean
    @Qualifier("pgOdbConnectionProvider")
    public DataSourceConnectionProvider pgOdbConnectionProvider(@Qualifier("pgOdbDataSource") DataSource pgDataSource) {
        return new DataSourceConnectionProvider(
                new TransactionAwareDataSourceProxy(pgDataSource));
    }

    @Bean
    @Qualifier("pgOdbConfiguration")
    public DefaultConfiguration pgOdbConfiguration(@Qualifier("pgOdbConnectionProvider") DataSourceConnectionProvider dataSourceConnectionProvider) {
        DefaultConfiguration config = new DefaultConfiguration();
        config.set(dataSourceConnectionProvider);
        config.set(SQLDialect.POSTGRES);
        config.set(new Settings().
                withRenderNameStyle(RenderNameStyle.QUOTED ));
        config.set(new DefaultExecuteListenerProvider(
                new JooqExceptionTranslator() ));
        return config;
    }

    @Bean
    @Qualifier("pgOdbDslContext")
    public DSLContext pgOperatingDsl(@Qualifier("pgOdbConfiguration") DefaultConfiguration defaultConfiguration) {
        return new DefaultDSLContext(defaultConfiguration);
    }

    @Bean
    @Qualifier("jdbcPgOdbScheduler")
    public Scheduler jdbcPgOperatingScheduler() {
        if (connectionPoolSize == null || connectionPoolSize == 0) {
            throw new IllegalArgumentException("Ноль потоков для jdbcPgOdbScheduler");
        }
        return Schedulers.fromExecutor(
                Executors.newFixedThreadPool(connectionPoolSize, new CustomizableThreadFactory("jdbcPgOdbScheduler-")));
    }

    @Bean
    @Qualifier("odbLiquibaseProperties")
    @ConfigurationProperties(prefix = "spring.datasource.odb.liquibase")
    public LiquibaseProperties operatingLiquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean
    @Qualifier("odbLiquibase")
    @ConditionalOnProperty(prefix="spring.liquibase", name="enabled")
    public SpringLiquibase operatingLiquibase(@Qualifier("pgOdbDataSource") DataSource dataSource,
                                     @Qualifier("operatingLiquibaseProperties") LiquibaseProperties liquibaseProperties) {
        SpringLiquibase liquibase = new SpringLiquibase();

        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog_master.xml");
        liquibase.setLiquibaseSchema("graduatework");
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        return liquibase;
    }
}
