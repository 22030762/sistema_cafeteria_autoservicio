package main.sistema_cafeteria_autoservicio.Utils.Dao;

import main.sistema_cafeteria_autoservicio.Utils.Connection.DatabaseConnection;
import main.sistema_cafeteria_autoservicio.Models.Usuario;

import java.util.Optional;

public class AuthDao {
    // Login básico
    public boolean login(String username, String password) {
        return authenticate(username, password).isPresent();
    }

    public Optional<Usuario> authenticate(String username, String password) {
        String query = "SELECT * FROM usuarios WHERE email = ? AND password = ?";
        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Usuario usuario = new Usuario();
                usuario.id = resultSet.getInt("id_usuario");
                usuario.nombre = resultSet.getString("nombre");
                usuario.email = resultSet.getString("email");
                usuario.password = resultSet.getString("password");
                var fechaRegistro = resultSet.getTimestamp("fecha_registro");
                if (fechaRegistro != null) {
                    usuario.fecha_registro = new java.util.Date(fechaRegistro.getTime());
                }
                return Optional.of(usuario);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
