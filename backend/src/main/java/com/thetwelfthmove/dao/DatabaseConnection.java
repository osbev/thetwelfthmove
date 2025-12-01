package com.thetwelfthmove.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String NAME = "thetwelfthmove";
    private static final String URL = "jdbc:mysql://localhost:3306/" + NAME + "?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    // Initialize database and tables if not exist
    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306?serverTimezone=UTC", USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            // Create database if not exists
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + NAME);
            // Use the database
            stmt.execute("USE " + NAME);
            // Create players table if not exists
            stmt.execute("CREATE TABLE IF NOT EXISTS players (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "games_played INT DEFAULT 0," +
                "games_won INT DEFAULT 0," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");
            System.out.println("Database and tables initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    // Get connection singleton
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to MySQL database successfully.");
            } catch (SQLException e) {
                System.err.println("Failed to connect to database: " + e.getMessage());
                throw new RuntimeException("Failed to connect to database", e);
            }
        }
        return connection;
    }

    // Close connection
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Failed to close database connection: " + e.getMessage());
            }
        }
    }
}