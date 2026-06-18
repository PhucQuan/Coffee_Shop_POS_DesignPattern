package com.coffeeshop.service;

import com.coffeeshop.domain.model.User;
import com.coffeeshop.infrastructure.Repository;

import java.util.Optional;

public class AuthService {
    private final Repository repository;

    public AuthService(Repository repository) {
        this.repository = repository;
    }

    public Optional<User> login(String username, String password) {
        return repository.findUser(username, password);
    }
}
