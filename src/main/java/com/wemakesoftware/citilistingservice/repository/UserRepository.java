package com.wemakesoftware.citilistingservice.repository;

import org.springframework.data.repository.CrudRepository;
import com.wemakesoftware.citilistingservice.model.security.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {
    Optional<User> findByEmail(String email);
}
