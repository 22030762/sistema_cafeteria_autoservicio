package main.sistema_cafeteria_autoservicio.Utils.Dao;

import main.sistema_cafeteria_autoservicio.Models.Usuario;
import main.sistema_cafeteria_autoservicio.Utils.Connection.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDao {

    public int create(Usuario usuario) throws SQLException {
        String query = "INSERT INTO usuarios (nombre, email, password) VALUES (?, ?, ?) RETURNING id_usuario";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, usuario.nombre);
            statement.setString(2, usuario.email);
            statement.setString(3, usuario.password);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_usuario");
                }
            }
        }

        throw new SQLException("No se pudo crear el usuario");
    }

    public Optional<Usuario> findById(int idUsuario) throws SQLException {
        String query = "SELECT * FROM usuarios WHERE id_usuario = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUsuario);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Usuario> findByEmail(String email) throws SQLException {
        String query = "SELECT * FROM usuarios WHERE email = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, email);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public List<Usuario> findAll() throws SQLException {
        String query = "SELECT * FROM usuarios ORDER BY id_usuario";
        List<Usuario> usuarios = new ArrayList<>();

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                usuarios.add(mapUser(resultSet));
            }
        }

        return usuarios;
    }

    public boolean update(Usuario usuario) throws SQLException {
        String query = "UPDATE usuarios SET nombre = ?, email = ?, password = ? WHERE id_usuario = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, usuario.nombre);
            statement.setString(2, usuario.email);
            statement.setString(3, usuario.password);
            statement.setInt(4, usuario.id);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int idUsuario) throws SQLException {
        String query = "DELETE FROM usuarios WHERE id_usuario = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUsuario);
            return statement.executeUpdate() > 0;
        }
    }

    private Usuario mapUser(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.id = resultSet.getInt("id_usuario");
        usuario.nombre = resultSet.getString("nombre");
        usuario.email = resultSet.getString("email");
        usuario.password = resultSet.getString("password");

        var fechaRegistro = resultSet.getTimestamp("fecha_registro");
        if (fechaRegistro != null) {
            usuario.fecha_registro = new java.util.Date(fechaRegistro.getTime());
        }

        return usuario;
    }
}

