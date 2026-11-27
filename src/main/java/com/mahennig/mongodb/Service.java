package com.mahennig.mongodb;

import com.mahennig.common.UserRequest;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class Service {

    /** Creates the unique index on email at startup; a no-op if it already exists. */
    void createIndexes(@Observes StartupEvent event) {
        User.mongoCollection().createIndex(
                Indexes.ascending("email"),
                new IndexOptions().unique(true).name("email_unique"));
    }

    public Optional<User> getUser(String id) {
        if (!ObjectId.isValid(id)) {
            return Optional.empty();
        }
        return User.findByIdOptional(new ObjectId(id));
    }

    public List<User> getAll() {
        return User.listAll();
    }

    /** A single-document insert is atomic in MongoDB; no JTA transaction is involved. */
    public User create(UserRequest request) {
        User user = new User();
        user.name = request.name;
        user.email = request.email;
        user.persist();
        return user;
    }
}
