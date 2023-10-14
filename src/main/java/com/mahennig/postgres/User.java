package com.mahennig.postgres;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.persistence.Column;

@MongoEntity(collection = "users")
public class User extends PanacheMongoEntity  {

    @Column(nullable = false)
    public String name;

    @Column(nullable = false, unique = true)
    public String email;

}
