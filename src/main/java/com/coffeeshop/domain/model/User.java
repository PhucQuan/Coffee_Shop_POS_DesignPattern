package com.coffeeshop.domain.model;

public class User {
    private final int id;
    private final String username;
    private final String passwordHash;
    private final String role;
    private boolean active;

    public User(int id, String username, String passwordHash, String role, boolean active) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
