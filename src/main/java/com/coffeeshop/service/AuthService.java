package com.coffeeshop.service;

import com.coffeeshop.domain.model.User;
import com.coffeeshop.infrastructure.InMemoryRepository;

import java.util.Optional;

public class AuthService {
    private final InMemoryRepository repository;

    public AuthService(InMemoryRepository repository) {
        this.repository = repository;
    }

    public Optional<User> login(String username, String password) {
        return repository.findUser(username, password);
    }
}
