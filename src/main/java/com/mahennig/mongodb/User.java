package com.mahennig.mongodb;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

/**
 * MongoDB document in the "users" collection. The inherited {@code id} is an
 * {@link org.bson.types.ObjectId}. JPA annotations such as {@code @Column} have no effect
 * on MongoDB, so uniqueness of {@code email} is enforced by an index (see {@link Service}).
 */
@MongoEntity(collection = "users")
public class User extends PanacheMongoEntity {

    public String name;

    public String email;
}
