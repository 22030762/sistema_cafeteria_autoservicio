package main.sistema_cafeteria_autoservicio.Utils.Dao;

import main.sistema_cafeteria_autoservicio.Models.PedidoDetalle;
import main.sistema_cafeteria_autoservicio.Utils.Connection.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoDetalleDao {

    public int create(PedidoDetalle detalle) throws SQLException {
        String query = "INSERT INTO pedido_detalle (id_pedido, id_producto, cantidad, precio_base) VALUES (?, ?, ?, ?) RETURNING id_detalle";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, detalle.getIdPedido());
            statement.setInt(2, detalle.getIdProducto());
            statement.setInt(3, detalle.getCantidad());
            statement.setBigDecimal(4, detalle.getPrecioBase());

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_detalle");
                }
            }
        }

        throw new SQLException("No se pudo crear el detalle del pedido");
    }

    public Optional<PedidoDetalle> findById(int idDetalle) throws SQLException {
        String query = "SELECT * FROM pedido_detalle WHERE id_detalle = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idDetalle);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapDetalle(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public List<PedidoDetalle> findByPedido(int idPedido) throws SQLException {
        String query = "SELECT * FROM pedido_detalle WHERE id_pedido = ? ORDER BY id_detalle";
        List<PedidoDetalle> detalles = new ArrayList<>();

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idPedido);

            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    detalles.add(mapDetalle(resultSet));
                }
            }
        }

        return detalles;
    }

    public boolean update(PedidoDetalle detalle) throws SQLException {
        String query = "UPDATE pedido_detalle SET id_producto = ?, cantidad = ?, precio_base = ? WHERE id_detalle = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, detalle.getIdProducto());
            statement.setInt(2, detalle.getCantidad());
            statement.setBigDecimal(3, detalle.getPrecioBase());
            statement.setInt(4, detalle.getIdDetalle());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int idDetalle) throws SQLException {
        String query = "DELETE FROM pedido_detalle WHERE id_detalle = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idDetalle);
            return statement.executeUpdate() > 0;
        }
    }

    private PedidoDetalle mapDetalle(ResultSet resultSet) throws SQLException {
        PedidoDetalle detalle = new PedidoDetalle();
        detalle.setIdDetalle(resultSet.getInt("id_detalle"));
        detalle.setIdPedido(resultSet.getInt("id_pedido"));
        detalle.setIdProducto(resultSet.getInt("id_producto"));
        detalle.setCantidad(resultSet.getInt("cantidad"));
        detalle.setPrecioBase(resultSet.getBigDecimal("precio_base"));
        return detalle;
    }
}

