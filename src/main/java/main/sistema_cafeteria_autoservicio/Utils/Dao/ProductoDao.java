package main.sistema_cafeteria_autoservicio.Utils.Dao;

import main.sistema_cafeteria_autoservicio.Models.Producto;
import main.sistema_cafeteria_autoservicio.Utils.Connection.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoDao {

    public int create(Producto producto) throws SQLException {
        String query = "INSERT INTO productos (nombre, descripcion, precio_base, activo) VALUES (?, ?, ?, ?) RETURNING id_producto";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, producto.getNombre());
            statement.setString(2, producto.getDescripcion());
            statement.setBigDecimal(3, producto.getPrecioBase());
            statement.setBoolean(4, producto.isActivo());

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_producto");
                }
            }
        }

        throw new SQLException("No se pudo crear el producto");
    }

    public Optional<Producto> findById(int idProducto) throws SQLException {
        String query = "SELECT * FROM productos WHERE id_producto = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idProducto);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapProducto(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public List<Producto> findAll() throws SQLException {
        String query = "SELECT * FROM productos ORDER BY id_producto";
        List<Producto> productos = new ArrayList<>();

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                productos.add(mapProducto(resultSet));
            }
        }

        return productos;
    }

    public List<Producto> findActivos() throws SQLException {
        String query = "SELECT * FROM productos WHERE activo = TRUE ORDER BY nombre";
        List<Producto> productos = new ArrayList<>();

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                productos.add(mapProducto(resultSet));
            }
        }

        return productos;
    }

    public boolean update(Producto producto) throws SQLException {
        String query = "UPDATE productos SET nombre = ?, descripcion = ?, precio_base = ?, activo = ? WHERE id_producto = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, producto.getNombre());
            statement.setString(2, producto.getDescripcion());
            statement.setBigDecimal(3, producto.getPrecioBase());
            statement.setBoolean(4, producto.isActivo());
            statement.setInt(5, producto.getIdProducto());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int idProducto) throws SQLException {
        String query = "DELETE FROM productos WHERE id_producto = ?";

        try (var connection = DatabaseConnection.getInstance().getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, idProducto);
            return statement.executeUpdate() > 0;
        }
    }

    private Producto mapProducto(ResultSet resultSet) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(resultSet.getInt("id_producto"));
        producto.setNombre(resultSet.getString("nombre"));
        producto.setDescripcion(resultSet.getString("descripcion"));
        producto.setPrecioBase(resultSet.getBigDecimal("precio_base"));
        producto.setActivo(resultSet.getBoolean("activo"));
        return producto;
    }
}

