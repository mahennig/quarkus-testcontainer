package com.mahennig.mongodb;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;

/**
 * Runs against the MongoDB container that Quarkus Dev Services starts, because no
 * connection string is configured for the test profile.
 */
@QuarkusTest
public class MongoDbTest {

    @Test
    public void createdUserCanBeReadBack() {
        String email = "mongo-" + UUID.randomUUID() + "@example.com";

        ExtractableResponse<Response> created = given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Alice\",\"email\":\"" + email + "\"}")
                .when().post("/mongodb/users")
                .then()
                .statusCode(201)
                .body("id", matchesPattern("[0-9a-f]{24}"))
                .extract();
        String id = created.path("id");
        assertThat(created.header("Location"), endsWith("/mongodb/users/" + id));

        given()
                .when().get("/mongodb/users/{id}", id)
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("name", is("Alice"))
                .body("email", is(email));
    }

    @Test
    public void duplicateEmailIsRejectedByUniqueIndex() {
        String body = "{\"name\":\"Bob\",\"email\":\"mongo-dup-" + UUID.randomUUID() + "@example.com\"}";

        given().contentType(ContentType.JSON).body(body)
                .when().post("/mongodb/users")
                .then().statusCode(201);

        given().contentType(ContentType.JSON).body(body)
                .when().post("/mongodb/users")
                .then().statusCode(409);
    }

    @Test
    public void unknownOrMalformedIdReturns404() {
        given().when().get("/mongodb/users/{id}", "0123456789abcdef01234567")
                .then().statusCode(404);
        given().when().get("/mongodb/users/{id}", "not-an-object-id")
                .then().statusCode(404);
    }
}
