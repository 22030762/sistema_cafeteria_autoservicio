package main.sistema_cafeteria_autoservicio.Utils.Dao;

import main.sistema_cafeteria_autoservicio.Utils.Connection.DatabaseConnection;

public class AuthDao {
    // Login básico
    public boolean login(String username, String password) {
        String query = "SELECT * FROM usuarios WHERE email = ? AND password = ?";
        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
