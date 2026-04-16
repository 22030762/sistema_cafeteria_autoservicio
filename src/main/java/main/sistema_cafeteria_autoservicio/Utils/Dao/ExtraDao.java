package main.sistema_cafeteria_autoservicio.Utils.Dao;

import main.sistema_cafeteria_autoservicio.Models.Extra;
import main.sistema_cafeteria_autoservicio.Utils.Connection.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExtraDao {

    public int create(Extra extra) throws SQLException {
        String query = "INSERT INTO extras (nombre, precio, activo) VALUES (?, ?, ?) RETURNING id_extra";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, extra.getNombre());
            statement.setBigDecimal(2, extra.getPrecio());
            statement.setBoolean(3, extra.isActivo());

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_extra");
                }
            }
        }

        throw new SQLException("No se pudo crear el extra");
    }

    public Optional<Extra> findById(int idExtra) throws SQLException {
        String query = "SELECT * FROM extras WHERE id_extra = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idExtra);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapExtra(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public List<Extra> findAll() throws SQLException {
        String query = "SELECT * FROM extras ORDER BY id_extra";
        List<Extra> extras = new ArrayList<>();

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                extras.add(mapExtra(resultSet));
            }
        }

        return extras;
    }

    public List<Extra> findActivos() throws SQLException {
        String query = "SELECT * FROM extras WHERE activo = TRUE ORDER BY nombre";
        List<Extra> extras = new ArrayList<>();

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                extras.add(mapExtra(resultSet));
            }
        }

        return extras;
    }

    public boolean update(Extra extra) throws SQLException {
        String query = "UPDATE extras SET nombre = ?, precio = ?, activo = ? WHERE id_extra = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, extra.getNombre());
            statement.setBigDecimal(2, extra.getPrecio());
            statement.setBoolean(3, extra.isActivo());
            statement.setInt(4, extra.getIdExtra());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int idExtra) throws SQLException {
        String query = "DELETE FROM extras WHERE id_extra = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idExtra);
            return statement.executeUpdate() > 0;
        }
    }

    private Extra mapExtra(ResultSet resultSet) throws SQLException {
        Extra extra = new Extra();
        extra.setIdExtra(resultSet.getInt("id_extra"));
        extra.setNombre(resultSet.getString("nombre"));
        extra.setPrecio(resultSet.getBigDecimal("precio"));
        extra.setActivo(resultSet.getBoolean("activo"));
        return extra;
    }
}

