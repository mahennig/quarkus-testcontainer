package com.mahennig.postgres;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Runs against the PostgreSQL container that Quarkus Dev Services starts, because no JDBC URL
 * is configured for the test profile.
 */
@QuarkusTest
public class PostgresTest {

    @Test
    public void createdUserCanBeReadBack() {
        String email = "pg-" + UUID.randomUUID() + "@example.com";

        ExtractableResponse<Response> created = given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Alice\",\"email\":\"" + email + "\"}")
                .when().post("/postgres/users")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract();
        long id = created.jsonPath().getLong("id");
        assertThat(created.header("Location"), endsWith("/postgres/users/" + id));

        given()
                .when().get("/postgres/users/{id}", id)
                .then()
                .statusCode(200)
                .body("name", is("Alice"))
                .body("email", is(email));
    }

    @Test
    public void duplicateEmailIsRejected() {
        String body = "{\"name\":\"Bob\",\"email\":\"pg-dup-" + UUID.randomUUID() + "@example.com\"}";

        given().contentType(ContentType.JSON).body(body)
                .when().post("/postgres/users")
                .then().statusCode(201);

        given().contentType(ContentType.JSON).body(body)
                .when().post("/postgres/users")
                .then().statusCode(409);
    }

    @Test
    public void unknownIdReturns404() {
        given()
                .when().get("/postgres/users/{id}", Long.MAX_VALUE)
                .then()
                .statusCode(404);
    }

    @Test
    public void seedDataIsLoadedInTestProfile() {
        given()
                .when().get("/postgres/users")
                .then()
                .statusCode(200)
                .body("email", hasItem("johndoe@example.com"));
    }
}
