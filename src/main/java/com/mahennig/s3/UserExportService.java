package com.mahennig.s3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahennig.postgres.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

/** Exports the PostgreSQL users as a JSON file to S3 and reads such a file back. */
@ApplicationScoped
public class UserExportService {

    private static final TypeReference<List<ExportedUser>> USER_LIST = new TypeReference<>() {
    };

    @Inject
    S3Client s3;

    @Inject
    AwsConfig config;

    @Inject
    ObjectMapper objectMapper;

    /** Writes all users to a new object and returns its key. */
    public String exportUsers() {
        List<ExportedUser> users = User.<User>listAll().stream()
                .map(user -> new ExportedUser(user.id, user.name, user.email))
                .toList();
        String key = "users-" + UUID.randomUUID() + ".json";
        ensureBucketExists();
        s3.putObject(put -> put.bucket(config.exportBucket()).key(key).contentType("application/json"),
                RequestBody.fromBytes(toJson(users)));
        return key;
    }

    /** Reads an export; throws {@code NoSuchKeyException} if the key does not exist. */
    public List<ExportedUser> loadExport(String key) {
        try (InputStream content = s3.getObject(get -> get.bucket(config.exportBucket()).key(key))) {
            return objectMapper.readValue(content, USER_LIST);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void ensureBucketExists() {
        try {
            s3.headBucket(head -> head.bucket(config.exportBucket()));
        } catch (NoSuchBucketException e) {
            s3.createBucket(create -> create.bucket(config.exportBucket()));
        }
    }

    private byte[] toJson(List<ExportedUser> users) {
        try {
            return objectMapper.writeValueAsBytes(users);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
