package com.mahennig.devservices;

import com.mahennig.support.PostgresUserApiContract;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Pattern (a): Quarkus Dev Services.
 *
 * <p>There is no container code at all. Because the test profile configures neither
 * {@code quarkus.datasource.jdbc.url} nor credentials, Quarkus starts a PostgreSQL container
 * (image from {@code quarkus.datasource.devservices.image-name}) before the application boots
 * and wires the datasource to it. Flyway then migrates it at startup.
 */
@QuarkusTest
class PostgresDevServicesTest extends PostgresUserApiContract {
}
