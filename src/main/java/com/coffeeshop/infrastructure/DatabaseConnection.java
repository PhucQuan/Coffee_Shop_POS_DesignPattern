package com.coffeeshop.infrastructure;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private final String connectionString = "jdbc:sqlite:coffee_shop_pos.db";

    private DatabaseConnection() {}

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

    public String getConnectionString() { return connectionString; }
}
