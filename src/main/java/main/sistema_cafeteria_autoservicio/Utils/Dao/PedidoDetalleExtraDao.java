package main.sistema_cafeteria_autoservicio.Utils.Dao;

import main.sistema_cafeteria_autoservicio.Models.PedidoDetalleExtra;
import main.sistema_cafeteria_autoservicio.Utils.Connection.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoDetalleExtraDao {

    public int create(PedidoDetalleExtra detalleExtra) throws SQLException {
        String query = "INSERT INTO pedido_detalle_extras (id_detalle, id_extra, precio_extra) VALUES (?, ?, ?) RETURNING id_detalle_extra";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, detalleExtra.getIdDetalle());
            statement.setInt(2, detalleExtra.getIdExtra());
            statement.setBigDecimal(3, detalleExtra.getPrecioExtra());

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_detalle_extra");
                }
            }
        }

        throw new SQLException("No se pudo crear el extra del detalle");
    }

    public Optional<PedidoDetalleExtra> findById(int idDetalleExtra) throws SQLException {
        String query = "SELECT * FROM pedido_detalle_extras WHERE id_detalle_extra = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idDetalleExtra);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapDetalleExtra(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public List<PedidoDetalleExtra> findByDetalle(int idDetalle) throws SQLException {
        String query = "SELECT * FROM pedido_detalle_extras WHERE id_detalle = ? ORDER BY id_detalle_extra";
        List<PedidoDetalleExtra> extras = new ArrayList<>();

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idDetalle);

            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    extras.add(mapDetalleExtra(resultSet));
                }
            }
        }

        return extras;
    }

    public boolean update(PedidoDetalleExtra detalleExtra) throws SQLException {
        String query = "UPDATE pedido_detalle_extras SET id_extra = ?, precio_extra = ? WHERE id_detalle_extra = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, detalleExtra.getIdExtra());
            statement.setBigDecimal(2, detalleExtra.getPrecioExtra());
            statement.setInt(3, detalleExtra.getIdDetalleExtra());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int idDetalleExtra) throws SQLException {
        String query = "DELETE FROM pedido_detalle_extras WHERE id_detalle_extra = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idDetalleExtra);
            return statement.executeUpdate() > 0;
        }
    }

    private PedidoDetalleExtra mapDetalleExtra(ResultSet resultSet) throws SQLException {
        PedidoDetalleExtra detalleExtra = new PedidoDetalleExtra();
        detalleExtra.setIdDetalleExtra(resultSet.getInt("id_detalle_extra"));
        detalleExtra.setIdDetalle(resultSet.getInt("id_detalle"));
        detalleExtra.setIdExtra(resultSet.getInt("id_extra"));
        detalleExtra.setPrecioExtra(resultSet.getBigDecimal("precio_extra"));
        return detalleExtra;
    }
}

