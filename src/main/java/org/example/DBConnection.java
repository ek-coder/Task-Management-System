package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/task_management"; // Update with your database name
    private static final String USER = "root"; // Replace with your DB username
    private static final String PASSWORD = "#"; // Replace with your DB password

    // Static method to get a connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
