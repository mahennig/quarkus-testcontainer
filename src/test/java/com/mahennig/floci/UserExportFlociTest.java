package com.mahennig.floci;

import com.mahennig.support.TestUsers;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.matchesPattern;

/**
 * Pattern (d): S3 against Floci, combined with Dev Services.
 *
 * <p>Only S3 comes from a test resource; PostgreSQL and MongoDB are still provided by Dev Services
 * because this test resource does not configure them. Quarkus reuses Dev Services containers that
 * an earlier test class already started, so no new database containers appear in this class's log.
 */
@QuarkusTest
@QuarkusTestResource(value = FlociTestResource.class, restrictToAnnotatedClass = true)
class UserExportFlociTest {

    @FlociEndpoint
    URI flociEndpoint;

    @Test
    void exportWritesUsersToS3AndCanBeLoadedBack() {
        String email = TestUsers.uniqueEmail("export");
        given().contentType(ContentType.JSON).body(TestUsers.json("Exported", email))
                .when().post("/postgres/users")
                .then().statusCode(201);

        ExtractableResponse<Response> export = given()
                .when().post("/exports/users")
                .then()
                .statusCode(201)
                .body("key", matchesPattern("users-[0-9a-f-]{36}\\.json"))
                .extract();
        String key = export.path("key");
        assertThat(export.header("Location"), endsWith("/exports/users/" + key));

        // The object really is in Floci: read it with an independent client.
        try (S3Client s3 = S3Client.builder()
                .endpointOverride(flociEndpoint)
                .region(Region.of(FlociTestResource.REGION))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(FlociTestResource.ACCESS_KEY, FlociTestResource.SECRET_KEY)))
                .forcePathStyle(true)
                .build()) {
            String stored = s3.getObjectAsBytes(get -> get.bucket("user-exports").key(key))
                    .asString(StandardCharsets.UTF_8);
            assertThat(stored, containsString(email));
        }

        given()
                .when().get("/exports/users/{key}", key)
                .then()
                .statusCode(200)
                .body("email", hasItem(email));
    }

    @Test
    void unknownExportReturns404() {
        given().when().post("/exports/users").then().statusCode(201); // makes sure the bucket exists

        given()
                .when().get("/exports/users/{key}", "users-does-not-exist.json")
                .then()
                .statusCode(404);
    }
}
