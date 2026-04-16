package main.sistema_cafeteria_autoservicio.Models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Pedido {
    private int idPedido;
    private int idUsuario;
    private Timestamp fecha;
    private BigDecimal total;
    private String estado;

    public Pedido() {
    }

    public Pedido(int idPedido, int idUsuario, Timestamp fecha, BigDecimal total, String estado) {
        this.idPedido = idPedido;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

