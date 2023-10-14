package com.mahennig.mongodb;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
public class MongoDbTest {

    static MongoDBContainer mongo =
            new MongoDBContainer("mongo:4.4")
                    .withExposedPorts(27017)
                    .waitingFor(Wait.forListeningPort());


    @BeforeAll
    public static void init() {
        System.setProperty("quarkus.mongodb.connection-string", "mongodb://" + mongo.getContainerIpAddress() + ":" + mongo.getFirstMappedPort());
        mongo.start();
    }

    @AfterAll
    public static void tearDown() {
        mongo.stop();
    }

    @Test
    public void testMongoDB() {
        given()
                .when()
                .get("/mongodb/users") // Replace with the actual endpoint URL
                .then()
                .statusCode(200) // Expect a 200 OK response
                .body("size()", greaterThan(0)); // Ensure there ar
    }

}
