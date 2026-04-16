package main.sistema_cafeteria_autoservicio.Utils.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static  DatabaseConnection instance;
    private static final String URL = "jdbc:postgresql://localhost:5432/puntoventa";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    private DatabaseConnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Error al cargar el driver de PostgreSQL: " + e.getMessage());
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
