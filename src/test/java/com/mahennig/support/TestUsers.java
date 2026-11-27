package com.mahennig.support;

import java.util.UUID;

/** Small helpers for test data. Unique emails keep tests independent of each other. */
public final class TestUsers {

    private TestUsers() {
    }

    public static String uniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID() + "@example.com";
    }

    public static String json(String name, String email) {
        return "{\"name\":\"" + name + "\",\"email\":\"" + email + "\"}";
    }
}
