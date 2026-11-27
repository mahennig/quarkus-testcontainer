package com.mahennig.s3;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.net.URI;
import java.util.Optional;

/**
 * AWS client settings. Without an endpoint and static credentials the SDK defaults apply
 * (real AWS, default credentials provider chain); tests point the endpoint at Floci.
 */
@ConfigMapping(prefix = "app.aws")
public interface AwsConfig {

    /** Overrides the service endpoint, e.g. a local emulator. */
    Optional<URI> endpoint();

    @WithDefault("us-east-1")
    String region();

    /** Static credentials; if absent, the SDK's default credentials provider chain is used. */
    Optional<String> accessKeyId();

    Optional<String> secretAccessKey();

    /** Address buckets as {@code endpoint/bucket/key} instead of {@code bucket.endpoint/key}. */
    @WithDefault("false")
    boolean pathStyleAccess();

    /** Bucket that receives the user exports. */
    String exportBucket();
}
