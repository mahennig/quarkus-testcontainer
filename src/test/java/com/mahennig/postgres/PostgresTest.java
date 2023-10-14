package com.mahennig.postgres;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class PostgresTest {

    @Container
    private static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("tododb")
            .withUsername("todouser")
            .withPassword("todopw");

    @BeforeAll
    public static void init() {
        db.start();
    }

    @Test
    public void testDatabaseConnection() {
        given()
                .when()
                .get("/postgres/users") // Replace with the actual endpoint URL
                .then()
                .statusCode(200) // Expect a 200 OK response
                .body("size()", greaterThan(0)); // Ensure there ar
    }

}
