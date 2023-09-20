package cz.cvut.fel.camunda.workshops.developer.handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresHandler {
    private final Connection connection;

    public PostgresHandler() throws SQLException {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/hackathon-czm";
        String username = "postgres";
        String password = "root";

        connection = DriverManager.getConnection(jdbcUrl, username, password);
    }

    public Connection getConnection() {
        return connection;
    }
}
