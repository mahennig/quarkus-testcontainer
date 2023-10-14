package com.mahennig.mongodb;

import com.mahennig.common.UserRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class Service {

    public User getUser(String id) {
        return User.findById(id);
    }

    public List<User> getAll() {
        return User.listAll();
    }

    @Transactional
    public void create(UserRequest request) {
        User user = map(request);
        user.persist();
    }

    private User map(UserRequest request) {
        User user = new User();
        user.email = request.email;
        user.name = request.name;
        return user;
    }
}
