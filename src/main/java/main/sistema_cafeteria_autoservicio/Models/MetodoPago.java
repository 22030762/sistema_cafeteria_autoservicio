package main.sistema_cafeteria_autoservicio.Models;

public class MetodoPago {
    private int idMetodo;
    private String nombre;

    public MetodoPago() {
    }

    public MetodoPago(int idMetodo, String nombre) {
        this.idMetodo = idMetodo;
        this.nombre = nombre;
    }

    public int getIdMetodo() {
        return idMetodo;
    }

    public void setIdMetodo(int idMetodo) {
        this.idMetodo = idMetodo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

