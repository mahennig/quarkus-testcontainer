package com.mahennig.floci;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.net.URI;
import java.util.Map;

/**
 * Pattern (d): AWS services locally with Floci (https://floci.io), an MIT-licensed AWS emulator.
 *
 * <p>Started as a {@link GenericContainer}: Floci listens on port 4566 and the wait strategy polls
 * {@code /_floci/init} until it reports {@code "ready":true}, which is what the image's own
 * HEALTHCHECK does. The Docker socket is not mounted; Floci only needs it for services that run
 * containers (Lambda, RDS, ...), not for S3. Path-style access is required here: with a
 * {@code localhost} endpoint, virtual-hosted addressing would need {@code <bucket>.localhost} to
 * resolve, which it does not reliably.
 *
 * <p>The returned configuration points the application's S3 client at the container.
 */
public class FlociTestResource implements QuarkusTestResourceLifecycleManager {

    static final String IMAGE = "floci/floci:2.5.0";
    static final int PORT = 4566;
    static final String ACCESS_KEY = "test";
    static final String SECRET_KEY = "test";
    static final String REGION = "us-east-1";

    private GenericContainer<?> floci;

    @Override
    public Map<String, String> start() {
        floci = new GenericContainer<>(IMAGE)
                .withExposedPorts(PORT)
                .waitingFor(Wait.forHttp("/_floci/init")
                        .forPort(PORT)
                        .forStatusCode(200)
                        .forResponsePredicate(body -> body.contains("\"ready\":true")));
        floci.start();
        return Map.of(
                "app.aws.endpoint", endpoint().toString(),
                "app.aws.region", REGION,
                "app.aws.access-key-id", ACCESS_KEY,
                "app.aws.secret-access-key", SECRET_KEY,
                "app.aws.path-style-access", "true");
    }

    URI endpoint() {
        return URI.create("http://" + floci.getHost() + ":" + floci.getMappedPort(PORT));
    }

    @Override
    public void inject(TestInjector testInjector) {
        testInjector.injectIntoFields(endpoint(),
                new TestInjector.AnnotatedAndMatchesType(FlociEndpoint.class, URI.class));
    }

    @Override
    public void stop() {
        if (floci != null) {
            floci.stop();
        }
    }
}
