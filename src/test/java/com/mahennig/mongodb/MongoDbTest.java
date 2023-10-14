package com.mahennig.mongodb;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;

@QuarkusTest
public class MongoDbTest {

    static MongoDBContainer mongo =
            new MongoDBContainer("mongo:4.4")
                    .withExposedPorts(27017);


    @BeforeAll
    public static void init() {
        mongo.start();
    }

    @Test
    public void testMongoDB() {
        // Your test logic to interact with MongoDB here
        String mongoHost = mongo.getContainerIpAddress();
        Integer mongoPort = mongo.getMappedPort(27017);
        // Connect to MongoDB and perform tests
    }

}
