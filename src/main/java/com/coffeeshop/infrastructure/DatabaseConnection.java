package com.coffeeshop.infrastructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private final String host;
    private final int port;
    private final String databaseName;
    private final String username;
    private final String password;
    private final String connectionString;
    private final boolean customUrl;

    private DatabaseConnection() {
        Properties localConfig = loadLocalConfig();
        host = setting(localConfig, "MYSQL_HOST", "mysql.host", "localhost");
        port = Integer.parseInt(setting(localConfig, "MYSQL_PORT", "mysql.port", "3306"));
        databaseName = requireIdentifier(setting(localConfig, "MYSQL_DATABASE", "mysql.database", "coffee_shop_pos"));
        username = setting(localConfig, "MYSQL_USER", "mysql.user", "root");
        password = setting(localConfig, "MYSQL_PASSWORD", "mysql.password", "");
        String configuredUrl = setting(localConfig, "MYSQL_URL", "mysql.url", "");
        customUrl = !configuredUrl.isBlank();
        connectionString = customUrl
                ? configuredUrl.trim()
                : "jdbc:mysql://" + host + ":" + port + "/" + databaseName
                    + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh";
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(connectionString, username, password);
    }

    public void ensureDatabaseExists() throws SQLException {
        if (customUrl) {
            return;
        }
        try (Connection ignored = openConnection()) {
            return;
        } catch (SQLException exception) {
            // MySQL error 1049 means the configured database does not exist.
            // Authentication/authorization failures should be reported unchanged.
            if (exception.getErrorCode() != 1049) {
                throw exception;
            }
        }
        String serverUrl = "jdbc:mysql://" + host + ":" + port
                + "/?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh";
        try (Connection connection = DriverManager.getConnection(serverUrl, username, password);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE IF NOT EXISTS `" + databaseName
                    + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
    }

    public String getDatabaseName() { return databaseName; }
    public String getUsername() { return username; }
    public String getConnectionString() { return connectionString; }

    private static Properties loadLocalConfig() {
        Path path = Path.of("config", "mysql.properties");
        Properties properties = new Properties();
        if (!Files.exists(path)) {
            return properties;
        }
        try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            properties.load(reader);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read " + path.toAbsolutePath(), exception);
        }
    }

    private static String setting(Properties properties, String environmentName, String propertyName, String fallback) {
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }
        String propertyValue = properties.getProperty(propertyName);
        return propertyValue == null ? fallback : propertyValue.trim();
    }

    private static String requireIdentifier(String value) {
        if (!value.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("MYSQL_DATABASE may contain only letters, numbers, and underscores.");
        }
        return value;
    }
}
