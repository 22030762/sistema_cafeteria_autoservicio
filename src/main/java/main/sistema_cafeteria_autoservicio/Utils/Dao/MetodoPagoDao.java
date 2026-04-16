package main.sistema_cafeteria_autoservicio.Utils.Dao;

import main.sistema_cafeteria_autoservicio.Models.MetodoPago;
import main.sistema_cafeteria_autoservicio.Utils.Connection.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MetodoPagoDao {

    public int create(MetodoPago metodoPago) throws SQLException {
        String query = "INSERT INTO metodos_pago (nombre) VALUES (?) RETURNING id_metodo";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, metodoPago.getNombre());

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_metodo");
                }
            }
        }

        throw new SQLException("No se pudo crear el metodo de pago");
    }

    public Optional<MetodoPago> findById(int idMetodo) throws SQLException {
        String query = "SELECT * FROM metodos_pago WHERE id_metodo = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idMetodo);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapMetodo(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public List<MetodoPago> findAll() throws SQLException {
        String query = "SELECT * FROM metodos_pago ORDER BY id_metodo";
        List<MetodoPago> metodos = new ArrayList<>();

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                metodos.add(mapMetodo(resultSet));
            }
        }

        return metodos;
    }

    public boolean update(MetodoPago metodoPago) throws SQLException {
        String query = "UPDATE metodos_pago SET nombre = ? WHERE id_metodo = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, metodoPago.getNombre());
            statement.setInt(2, metodoPago.getIdMetodo());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int idMetodo) throws SQLException {
        String query = "DELETE FROM metodos_pago WHERE id_metodo = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idMetodo);
            return statement.executeUpdate() > 0;
        }
    }

    private MetodoPago mapMetodo(ResultSet resultSet) throws SQLException {
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setIdMetodo(resultSet.getInt("id_metodo"));
        metodoPago.setNombre(resultSet.getString("nombre"));
        return metodoPago;
    }
}

