package com.mahennig.postgres;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class Service {

    public User getUser(String id){
       return User.findById(id);
    }

    public List<User> getAll() {
        return User.listAll();
    }

    @Transactional
    public User create(User user) {
        user.persist();
        return user;
    }
}
