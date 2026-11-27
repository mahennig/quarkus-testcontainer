package com.mahennig.support;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;

/**
 * Behaviour of {@code /mongodb/users} that must hold no matter how MongoDB is provided.
 * Each testing pattern extends this class and only differs in how MongoDB is started.
 */
public abstract class MongoUserApiContract {

    /** Creates a user through the API and returns its ObjectId as hex string. */
    protected String createUser(String name, String email) {
        ExtractableResponse<Response> created = given()
                .contentType(ContentType.JSON)
                .body(TestUsers.json(name, email))
                .when().post("/mongodb/users")
                .then()
                .statusCode(201)
                .body("id", matchesPattern("[0-9a-f]{24}"))
                .extract();
        String id = created.path("id");
        assertThat(created.header("Location"), endsWith("/mongodb/users/" + id));
        return id;
    }

    @Test
    void createdUserCanBeReadBack() {
        String email = TestUsers.uniqueEmail("mongo");
        String id = createUser("Alice", email);

        given()
                .when().get("/mongodb/users/{id}", id)
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("name", is("Alice"))
                .body("email", is(email));
    }

    @Test
    void duplicateEmailIsRejectedByUniqueIndex() {
        String email = TestUsers.uniqueEmail("mongo-dup");
        createUser("Bob", email);

        given()
                .contentType(ContentType.JSON)
                .body(TestUsers.json("Bob again", email))
                .when().post("/mongodb/users")
                .then()
                .statusCode(409);
    }

    @Test
    void unknownOrMalformedIdReturns404() {
        given().when().get("/mongodb/users/{id}", "0123456789abcdef01234567")
                .then().statusCode(404);
        given().when().get("/mongodb/users/{id}", "not-an-object-id")
                .then().statusCode(404);
    }
}
