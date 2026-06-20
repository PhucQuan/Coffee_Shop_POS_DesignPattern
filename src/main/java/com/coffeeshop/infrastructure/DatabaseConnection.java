package com.coffeeshop.infrastructure;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private final String databasePath = "pos_data.db";
    private final String connectionString = "jdbc:sqlite:" + databasePath;

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

    public String getDatabasePath() { return databasePath; }
    public String getConnectionString() { return connectionString; }
}
