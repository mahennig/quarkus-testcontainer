package com.mahennig.s3;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

/** Produces the AWS SDK v2 S3 client from {@link AwsConfig}. */
@ApplicationScoped
public class S3ClientProducer {

    @Produces
    @ApplicationScoped
    S3Client s3Client(AwsConfig config) {
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(config.region()))
                .forcePathStyle(config.pathStyleAccess());
        config.endpoint().ifPresent(builder::endpointOverride);
        if (config.accessKeyId().isPresent() && config.secretAccessKey().isPresent()) {
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(config.accessKeyId().get(), config.secretAccessKey().get())));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.builder().build());
        }
        return builder.build();
    }

    void close(@Disposes S3Client client) {
        client.close();
    }
}
