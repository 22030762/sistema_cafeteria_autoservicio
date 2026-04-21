package main.sistema_cafeteria_autoservicio.Utils.Dao;

import main.sistema_cafeteria_autoservicio.Models.Pago;
import main.sistema_cafeteria_autoservicio.Utils.Connection.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PagoDao {

    public int create(Pago pago) throws SQLException {
        try (var connection = DatabaseConnection.getInstance().getConnection()) {
            return create(connection, pago);
        }
    }

    public int create(Connection connection, Pago pago) throws SQLException {
        String query = "INSERT INTO pagos (id_pedido, id_metodo, monto, referencia) VALUES (?, ?, ?, ?) RETURNING id_pago";

        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, pago.getIdPedido());
            statement.setInt(2, pago.getIdMetodo());
            statement.setBigDecimal(3, pago.getMonto());
            statement.setString(4, pago.getReferencia());

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_pago");
                }
            }
        }

        throw new SQLException("No se pudo registrar el pago");
    }

    public Optional<Pago> findById(int idPago) throws SQLException {
        String query = "SELECT * FROM pagos WHERE id_pago = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idPago);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapPago(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public List<Pago> findByPedido(int idPedido) throws SQLException {
        String query = "SELECT * FROM pagos WHERE id_pedido = ? ORDER BY fecha";
        List<Pago> pagos = new ArrayList<>();

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idPedido);

            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    pagos.add(mapPago(resultSet));
                }
            }
        }

        return pagos;
    }

    public BigDecimal totalPagadoPorPedido(int idPedido) throws SQLException {
        String query = "SELECT COALESCE(SUM(monto), 0) total_pagado FROM pagos WHERE id_pedido = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idPedido);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("total_pagado");
                }
            }
        }

        return BigDecimal.ZERO;
    }

    public boolean delete(int idPago) throws SQLException {
        String query = "DELETE FROM pagos WHERE id_pago = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idPago);
            return statement.executeUpdate() > 0;
        }
    }

    private Pago mapPago(ResultSet resultSet) throws SQLException {
        Pago pago = new Pago();
        pago.setIdPago(resultSet.getInt("id_pago"));
        pago.setIdPedido(resultSet.getInt("id_pedido"));
        pago.setIdMetodo(resultSet.getInt("id_metodo"));
        pago.setMonto(resultSet.getBigDecimal("monto"));
        pago.setFecha(resultSet.getTimestamp("fecha"));
        pago.setReferencia(resultSet.getString("referencia"));
        return pago;
    }
}

