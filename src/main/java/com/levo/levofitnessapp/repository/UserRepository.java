package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByAuth0Id(String auth0Id);
}

