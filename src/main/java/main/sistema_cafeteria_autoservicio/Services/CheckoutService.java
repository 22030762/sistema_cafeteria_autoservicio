package main.sistema_cafeteria_autoservicio.Services;

import main.sistema_cafeteria_autoservicio.Models.CarritoItem;
import main.sistema_cafeteria_autoservicio.Models.Pago;
import main.sistema_cafeteria_autoservicio.Models.Pedido;
import main.sistema_cafeteria_autoservicio.Models.PedidoDetalle;
import main.sistema_cafeteria_autoservicio.Models.PedidoDetalleExtra;
import main.sistema_cafeteria_autoservicio.Utils.Connection.DatabaseConnection;
import main.sistema_cafeteria_autoservicio.Utils.Dao.PagoDao;
import main.sistema_cafeteria_autoservicio.Utils.Dao.PedidoDao;
import main.sistema_cafeteria_autoservicio.Utils.Dao.PedidoDetalleDao;
import main.sistema_cafeteria_autoservicio.Utils.Dao.PedidoDetalleExtraDao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

public class CheckoutService {

    private final PedidoDao pedidoDao;
    private final PedidoDetalleDao pedidoDetalleDao;
    private final PedidoDetalleExtraDao pedidoDetalleExtraDao;
    private final PagoDao pagoDao;

    public CheckoutService() {
        this.pedidoDao = new PedidoDao();
        this.pedidoDetalleDao = new PedidoDetalleDao();
        this.pedidoDetalleExtraDao = new PedidoDetalleExtraDao();
        this.pagoDao = new PagoDao();
    }

    public CheckoutResult procesarCheckout(int idUsuario,
                                           Map<String, CarritoItem> carrito,
                                           int idMetodoPago,
                                           String referencia) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("Usuario de sesion invalido");
        }
        if (carrito == null || carrito.isEmpty()) {
            throw new IllegalArgumentException("El carrito esta vacio");
        }

        BigDecimal total = calcularTotal(carrito);

        try (var connection = DatabaseConnection.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                Pedido pedido = new Pedido();
                pedido.setIdUsuario(idUsuario);
                pedido.setTotal(total);
                pedido.setEstado("PENDIENTE");

                int idPedido = pedidoDao.create(connection, pedido);

                for (CarritoItem item : carrito.values()) {
                    PedidoDetalle detalle = new PedidoDetalle();
                    detalle.setIdPedido(idPedido);
                    detalle.setIdProducto(item.getProducto().getIdProducto());
                    detalle.setCantidad(item.getCantidad());
                    detalle.setPrecioBase(item.getProducto().getPrecioBase());

                    int idDetalle = pedidoDetalleDao.create(connection, detalle);

                    for (var extra : item.getExtras()) {
                        PedidoDetalleExtra detalleExtra = new PedidoDetalleExtra();
                        detalleExtra.setIdDetalle(idDetalle);
                        detalleExtra.setIdExtra(extra.getIdExtra());
                        detalleExtra.setPrecioExtra(extra.getPrecio());
                        pedidoDetalleExtraDao.create(connection, detalleExtra);
                    }
                }

                Pago pago = new Pago();
                pago.setIdPedido(idPedido);
                pago.setIdMetodo(idMetodoPago);
                pago.setMonto(total);
                pago.setReferencia(referencia == null ? "" : referencia.trim());
                pagoDao.create(connection, pago);

                pedidoDao.updateEstado(connection, idPedido, "PAGADO");
                connection.commit();

                return new CheckoutResult(idPedido, total);
            } catch (Exception ex) {
                connection.rollback();
                throw new SQLException("Error al procesar checkout: " + ex.getMessage(), ex);
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private BigDecimal calcularTotal(Map<String, CarritoItem> carrito) {
        BigDecimal total = BigDecimal.ZERO;
        for (CarritoItem item : carrito.values()) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public static class CheckoutResult {
        private final int idPedido;
        private final BigDecimal total;

        public CheckoutResult(int idPedido, BigDecimal total) {
            this.idPedido = idPedido;
            this.total = total;
        }

        public int getIdPedido() {
            return idPedido;
        }

        public BigDecimal getTotal() {
            return total;
        }
    }
}

