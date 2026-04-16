package main.sistema_cafeteria_autoservicio.Utils.Dao;

import main.sistema_cafeteria_autoservicio.Models.Pedido;
import main.sistema_cafeteria_autoservicio.Utils.Connection.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoDao {

    public int create(Pedido pedido) throws SQLException {
        String query = "INSERT INTO pedidos (id_usuario, total, estado) VALUES (?, ?, ?) RETURNING id_pedido";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, pedido.getIdUsuario());
            statement.setBigDecimal(2, pedido.getTotal());
            statement.setString(3, pedido.getEstado());

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_pedido");
                }
            }
        }

        throw new SQLException("No se pudo crear el pedido");
    }

    public Optional<Pedido> findById(int idPedido) throws SQLException {
        String query = "SELECT * FROM pedidos WHERE id_pedido = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idPedido);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapPedido(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public List<Pedido> findByUsuario(int idUsuario) throws SQLException {
        String query = "SELECT * FROM pedidos WHERE id_usuario = ? ORDER BY fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUsuario);

            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    pedidos.add(mapPedido(resultSet));
                }
            }
        }

        return pedidos;
    }

    public List<Pedido> findAll() throws SQLException {
        String query = "SELECT * FROM pedidos ORDER BY fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                pedidos.add(mapPedido(resultSet));
            }
        }

        return pedidos;
    }

    public boolean updateEstado(int idPedido, String estado) throws SQLException {
        String query = "UPDATE pedidos SET estado = ? WHERE id_pedido = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, estado);
            statement.setInt(2, idPedido);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateTotal(int idPedido, java.math.BigDecimal total) throws SQLException {
        String query = "UPDATE pedidos SET total = ? WHERE id_pedido = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setBigDecimal(1, total);
            statement.setInt(2, idPedido);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int idPedido) throws SQLException {
        String query = "DELETE FROM pedidos WHERE id_pedido = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idPedido);
            return statement.executeUpdate() > 0;
        }
    }

    private Pedido mapPedido(ResultSet resultSet) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(resultSet.getInt("id_pedido"));
        pedido.setIdUsuario(resultSet.getInt("id_usuario"));
        pedido.setFecha(resultSet.getTimestamp("fecha"));
        pedido.setTotal(resultSet.getBigDecimal("total"));
        pedido.setEstado(resultSet.getString("estado"));
        return pedido;
    }
}

