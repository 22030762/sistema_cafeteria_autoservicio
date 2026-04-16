package main.sistema_cafeteria_autoservicio.Models;

import java.math.BigDecimal;

public class Extra {
    private int idExtra;
    private String nombre;
    private BigDecimal precio;
    private boolean activo;

    public Extra() {
    }

    public Extra(int idExtra, String nombre, BigDecimal precio, boolean activo) {
        this.idExtra = idExtra;
        this.nombre = nombre;
        this.precio = precio;
        this.activo = activo;
    }

    public int getIdExtra() {
        return idExtra;
    }

    public void setIdExtra(int idExtra) {
        this.idExtra = idExtra;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}

