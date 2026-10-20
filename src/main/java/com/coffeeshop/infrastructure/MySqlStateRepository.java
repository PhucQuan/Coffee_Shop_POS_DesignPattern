package com.coffeeshop.infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Persists the React application's durable state in MySQL.
 *
 * The browser sends JSON but never owns the durable copy. MySQL validates the
 * JSON column and updates the single application-state row atomically.
 */
public final class MySqlStateRepository implements AutoCloseable {
    private static final String STATE_KEY = "coffee-pos";

    private final DatabaseConnection config;

    public MySqlStateRepository(DatabaseConnection config) throws SQLException {
        this.config = config;
        config.ensureDatabaseExists();
        initSchema();
    }

    private void initSchema() throws SQLException {
        try (Connection connection = config.openConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS web_app_state (
                        state_key VARCHAR(50) PRIMARY KEY,
                        payload JSON NOT NULL,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                            ON UPDATE CURRENT_TIMESTAMP
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                    """);
        }
    }

    public Optional<String> load() {
        try (Connection connection = config.openConnection();
             PreparedStatement statement = connection.prepareStatement(
                "SELECT CAST(payload AS CHAR) AS payload FROM web_app_state WHERE state_key = ?")) {
            statement.setString(1, STATE_KEY);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(result.getString("payload")) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to load application state from MySQL.", exception);
        }
    }

    public void save(String json) {
        if (json == null || json.isBlank() || !json.stripLeading().startsWith("{")) {
            throw new IllegalArgumentException("Application state must be a JSON object.");
        }
        try (Connection connection = config.openConnection();
             PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO web_app_state (state_key, payload)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE payload = ?
                """)) {
            statement.setString(1, STATE_KEY);
            statement.setString(2, json);
            statement.setString(3, json);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to save application state to MySQL.", exception);
        }
    }

    public void clear() {
        try (Connection connection = config.openConnection();
             PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM web_app_state WHERE state_key = ?")) {
            statement.setString(1, STATE_KEY);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to reset application state in MySQL.", exception);
        }
    }

    public boolean ping() {
        try (Connection connection = config.openConnection();
             Statement statement = connection.createStatement();
             ResultSet ignored = statement.executeQuery("SELECT 1")) {
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }

    @Override
    public void close() {
        // Connections are short-lived and closed after each operation.
    }
}
