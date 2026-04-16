package main.sistema_cafeteria_autoservicio.Models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Pago {
    private int idPago;
    private int idPedido;
    private int idMetodo;
    private BigDecimal monto;
    private Timestamp fecha;
    private String referencia;

    public Pago() {
    }

    public Pago(int idPago, int idPedido, int idMetodo, BigDecimal monto, Timestamp fecha, String referencia) {
        this.idPago = idPago;
        this.idPedido = idPedido;
        this.idMetodo = idMetodo;
        this.monto = monto;
        this.fecha = fecha;
        this.referencia = referencia;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdMetodo() {
        return idMetodo;
    }

    public void setIdMetodo(int idMetodo) {
        this.idMetodo = idMetodo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}

