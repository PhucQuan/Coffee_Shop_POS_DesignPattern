package com.coffeeshop.service;

import com.coffeeshop.domain.model.User;
import com.coffeeshop.infrastructure.Repository;

import java.util.List;

public class UserService {
    private final Repository repository;

    public UserService(Repository repository) {
        this.repository = repository;
    }

    public List<User> getUsers() {
        return repository.getUsers();
    }

    public User addUser(String username, String password, String role) {
        String normalizedUsername = requireText(username, "Username");
        String normalizedPassword = requireText(password, "Password");
        String normalizedRole = requireRole(role);
        boolean exists = repository.getUsers().stream()
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(normalizedUsername));
        if (exists) {
            throw new IllegalArgumentException("Username already exists.");
        }
        User user = new User(repository.nextUserId(), normalizedUsername, normalizedPassword, normalizedRole, true);
        repository.getUsers().add(user);
        return user;
    }

    public void setActive(User user, boolean active) {
        if (user == null) {
            throw new IllegalArgumentException("Select a user first.");
        }
        user.setActive(active);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private String requireRole(String role) {
        String normalized = requireText(role, "Role").toUpperCase();
        if (!normalized.equals("ADMIN") && !normalized.equals("CASHIER") && !normalized.equals("KITCHEN")) {
            throw new IllegalArgumentException("Role must be ADMIN, CASHIER, or KITCHEN.");
        }
        return normalized;
    }
}
