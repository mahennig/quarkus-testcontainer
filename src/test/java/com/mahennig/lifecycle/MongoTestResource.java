package com.mahennig.lifecycle;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.mongodb.MongoDBContainer;

import java.util.Map;

/**
 * Pattern (b): a Testcontainers MongoDB container managed by a Quarkus test resource.
 *
 * <p>The container is started in {@link #start()}, i.e. before Quarkus boots, and its connection
 * string is handed to Quarkus as configuration. Setting a connection string also means that
 * Dev Services for MongoDB stay off.
 */
public class MongoTestResource implements QuarkusTestResourceLifecycleManager {

    private MongoDBContainer mongo;

    @Override
    public Map<String, String> start() {
        mongo = new MongoDBContainer("mongo:8.0");
        mongo.start();
        return Map.of("quarkus.mongodb.connection-string", mongo.getConnectionString());
    }

    @Override
    public void inject(TestInjector testInjector) {
        testInjector.injectIntoFields(mongo,
                new TestInjector.AnnotatedAndMatchesType(InjectContainer.class, MongoDBContainer.class));
    }

    @Override
    public void stop() {
        if (mongo != null) {
            mongo.stop();
        }
    }
}
