package com.mahennig.lifecycle;

import com.mahennig.support.PostgresUserApiContract;
import com.mahennig.support.TestUsers;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pattern (b): explicit containers via {@link QuarkusTestResource}.
 *
 * <p>{@code restrictToAnnotatedClass = true} matters: without it a test resource is global and
 * would also replace the Dev Services databases of every other {@code @QuarkusTest}. The
 * application needs both databases, so both resources are declared; otherwise Dev Services would
 * still start the missing one.
 */
@QuarkusTest
@QuarkusTestResource(value = PostgresTestResource.class, restrictToAnnotatedClass = true)
@QuarkusTestResource(value = MongoTestResource.class, restrictToAnnotatedClass = true)
class PostgresLifecycleManagerTest extends PostgresUserApiContract {

    @InjectContainer
    PostgreSQLContainer postgres;

    @Test
    void userIsStoredInTheContainerStartedByTheTestResource() throws SQLException {
        String email = TestUsers.uniqueEmail("pg-direct");
        long id = createUser("Carol", email);

        try (Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             PreparedStatement query = connection.prepareStatement("SELECT email FROM users WHERE id = ?")) {
            query.setLong(1, id);
            try (ResultSet row = query.executeQuery()) {
                assertTrue(row.next(), "row written by the application must be in this container");
                assertEquals(email, row.getString("email"));
            }
        }
    }
}
