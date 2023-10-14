package com.mahennig.postgres;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class Test {

    static PostgreSQLContainer<?> db =
            new PostgreSQLContainer<>("postgres:13")
                    .withDatabaseName("tododb")
                    .withUsername("todouser")
                    .withPassword("todopw");


}
