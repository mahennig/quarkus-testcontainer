package com.mahennig.plain;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pattern (c): plain Testcontainers with the JUnit 5 extension, no Quarkus involved.
 *
 * <p>{@link Testcontainers @Testcontainers} activates the extension; it starts every
 * {@link Container @Container} field before the tests run and stops it afterwards. A static field
 * is shared by all test methods of the class, an instance field would be restarted per method.
 * {@code @Container} without {@code @Testcontainers} does nothing.
 *
 * <p>The test applies the project's real Flyway migrations and checks the schema with JDBC.
 */
@Testcontainers
class PlainPostgresJdbcTest {

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18");

    @BeforeAll
    static void migrate() {
        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    private static long insertUser(Connection connection, String name, String email) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO users (name, email) VALUES (?, ?) RETURNING id")) {
            insert.setString(1, name);
            insert.setString(2, email);
            try (ResultSet generated = insert.executeQuery()) {
                generated.next();
                return generated.getLong(1);
            }
        }
    }

    @Test
    void identityColumnGeneratesIdsAndRowCanBeReadBack() throws SQLException {
        String email = "plain-" + UUID.randomUUID() + "@example.com";
        try (Connection connection = connect()) {
            long id = insertUser(connection, "Alice", email);
            assertTrue(id > 0);

            try (PreparedStatement query = connection.prepareStatement("SELECT name, email FROM users WHERE id = ?")) {
                query.setLong(1, id);
                try (ResultSet row = query.executeQuery()) {
                    assertTrue(row.next());
                    assertEquals("Alice", row.getString("name"));
                    assertEquals(email, row.getString("email"));
                }
            }
        }
    }

    @Test
    void uniqueConstraintRejectsDuplicateEmail() throws SQLException {
        String email = "plain-dup-" + UUID.randomUUID() + "@example.com";
        try (Connection connection = connect()) {
            insertUser(connection, "Bob", email);

            SQLException duplicate = assertThrows(SQLException.class, () -> insertUser(connection, "Bob again", email));
            assertEquals("23505", duplicate.getSQLState(), "unique_violation");
        }
    }

    @Test
    void seedDataIsNotPartOfTheVersionedMigrations() throws SQLException {
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet row = statement.executeQuery(
                     "SELECT count(*) FROM users WHERE email = 'johndoe@example.com'")) {
            row.next();
            assertEquals(0, row.getInt(1));
        }
    }
}
