package com.mahennig.support;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Behaviour of {@code /postgres/users} that must hold no matter how the database is provided.
 * Each testing pattern extends this class and only differs in how PostgreSQL is started.
 */
public abstract class PostgresUserApiContract {

    /** Creates a user through the API and returns its id. */
    protected long createUser(String name, String email) {
        ExtractableResponse<Response> created = given()
                .contentType(ContentType.JSON)
                .body(TestUsers.json(name, email))
                .when().post("/postgres/users")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract();
        long id = created.jsonPath().getLong("id");
        assertThat(created.header("Location"), endsWith("/postgres/users/" + id));
        return id;
    }

    @Test
    void createdUserCanBeReadBack() {
        String email = TestUsers.uniqueEmail("pg");
        long id = createUser("Alice", email);

        given()
                .when().get("/postgres/users/{id}", id)
                .then()
                .statusCode(200)
                .body("name", is("Alice"))
                .body("email", is(email));
    }

    @Test
    void duplicateEmailIsRejected() {
        String email = TestUsers.uniqueEmail("pg-dup");
        createUser("Bob", email);

        given()
                .contentType(ContentType.JSON)
                .body(TestUsers.json("Bob again", email))
                .when().post("/postgres/users")
                .then()
                .statusCode(409);
    }

    @Test
    void missingFieldsAreRejected() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"No Email\"}")
                .when().post("/postgres/users")
                .then()
                .statusCode(400);
    }

    @Test
    void unknownIdReturns404() {
        given()
                .when().get("/postgres/users/{id}", Long.MAX_VALUE)
                .then()
                .statusCode(404);
    }

    @Test
    void seedDataIsLoadedInTestProfile() {
        given()
                .when().get("/postgres/users")
                .then()
                .statusCode(200)
                .body("email", hasItem("johndoe@example.com"));
    }
}
