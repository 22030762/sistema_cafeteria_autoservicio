package main.sistema_cafeteria_autoservicio.Models;

import java.math.BigDecimal;

public class PedidoDetalleExtra {
    private int idDetalleExtra;
    private int idDetalle;
    private int idExtra;
    private BigDecimal precioExtra;

    public PedidoDetalleExtra() {
    }

    public PedidoDetalleExtra(int idDetalleExtra, int idDetalle, int idExtra, BigDecimal precioExtra) {
        this.idDetalleExtra = idDetalleExtra;
        this.idDetalle = idDetalle;
        this.idExtra = idExtra;
        this.precioExtra = precioExtra;
    }

    public int getIdDetalleExtra() {
        return idDetalleExtra;
    }

    public void setIdDetalleExtra(int idDetalleExtra) {
        this.idDetalleExtra = idDetalleExtra;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdExtra() {
        return idExtra;
    }

    public void setIdExtra(int idExtra) {
        this.idExtra = idExtra;
    }

    public BigDecimal getPrecioExtra() {
        return precioExtra;
    }

    public void setPrecioExtra(BigDecimal precioExtra) {
        this.precioExtra = precioExtra;
    }
}

