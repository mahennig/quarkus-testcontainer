package com.mahennig.lifecycle;

import com.mahennig.support.MongoUserApiContract;
import com.mahennig.support.TestUsers;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.testcontainers.mongodb.MongoDBContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Pattern (b): explicit containers via {@link QuarkusTestResource}; see
 * {@link PostgresLifecycleManagerTest} for why both resources are declared and restricted.
 */
@QuarkusTest
@QuarkusTestResource(value = PostgresTestResource.class, restrictToAnnotatedClass = true)
@QuarkusTestResource(value = MongoTestResource.class, restrictToAnnotatedClass = true)
class MongoLifecycleManagerTest extends MongoUserApiContract {

    @InjectContainer
    MongoDBContainer mongo;

    @Test
    void userIsStoredInTheContainerStartedByTheTestResource() {
        String email = TestUsers.uniqueEmail("mongo-direct");
        String id = createUser("Carol", email);

        try (MongoClient client = MongoClients.create(mongo.getConnectionString())) {
            Document stored = client.getDatabase("app").getCollection("users")
                    .find(Filters.eq("_id", new ObjectId(id)))
                    .first();
            assertNotNull(stored, "document written by the application must be in this container");
            assertEquals(email, stored.getString("email"));
        }
    }
}
