package com.mahennig.devservices;

import com.mahennig.support.MongoUserApiContract;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Pattern (a): Quarkus Dev Services.
 *
 * <p>The test profile sets neither {@code quarkus.mongodb.connection-string} nor
 * {@code quarkus.mongodb.hosts}, so Quarkus starts a MongoDB container (image from
 * {@code quarkus.mongodb.devservices.image-name}) and points the client at it.
 * Both Dev Services test classes share the same application start and containers.
 */
@QuarkusTest
class MongoDevServicesTest extends MongoUserApiContract {
}
