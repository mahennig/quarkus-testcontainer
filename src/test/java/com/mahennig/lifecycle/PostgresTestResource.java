package com.mahennig.lifecycle;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Map;

/**
 * Pattern (b): a Testcontainers container managed by a Quarkus test resource.
 *
 * <p>Quarkus calls {@link #start()} before the application starts. The returned map is added to
 * the application's configuration, so the datasource points at this container and Dev Services
 * for the datasource are not started. {@link #stop()} runs after the last test that uses it.
 */
public class PostgresTestResource implements QuarkusTestResourceLifecycleManager {

    private PostgreSQLContainer postgres;

    @Override
    public Map<String, String> start() {
        postgres = new PostgreSQLContainer("postgres:18")
                .withDatabaseName("app")
                .withUsername("app")
                .withPassword("app");
        postgres.start();
        return Map.of(
                "quarkus.datasource.jdbc.url", postgres.getJdbcUrl(),
                "quarkus.datasource.username", postgres.getUsername(),
                "quarkus.datasource.password", postgres.getPassword());
    }

    @Override
    public void inject(TestInjector testInjector) {
        testInjector.injectIntoFields(postgres,
                new TestInjector.AnnotatedAndMatchesType(InjectContainer.class, PostgreSQLContainer.class));
    }

    @Override
    public void stop() {
        if (postgres != null) {
            postgres.stop();
        }
    }
}
