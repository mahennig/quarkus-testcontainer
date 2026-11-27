package com.mahennig.postgres;

import com.mahennig.common.UserRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class Service {

    public Optional<User> getUser(Long id) {
        return User.findByIdOptional(id);
    }

    public List<User> getAll() {
        return User.listAll();
    }

    /**
     * Flushes inside the transaction so that a duplicate email surfaces here as a
     * Hibernate ConstraintViolationException rather than at commit time.
     */
    @Transactional
    public User create(UserRequest request) {
        User user = new User();
        user.name = request.name;
        user.email = request.email;
        user.persistAndFlush();
        return user;
    }
}
