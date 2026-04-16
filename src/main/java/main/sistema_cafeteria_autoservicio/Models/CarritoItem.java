package main.sistema_cafeteria_autoservicio.Models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CarritoItem {
    private final Producto producto;
    private final List<Extra> extras;
    private int cantidad;

    public CarritoItem(Producto producto, List<Extra> extras) {
        this.producto = producto;
        this.extras = extras.stream()
                .sorted(Comparator.comparingInt(Extra::getIdExtra))
                .collect(Collectors.toCollection(ArrayList::new));
        this.cantidad = 1;
    }

    public Producto getProducto() {
        return producto;
    }

    public List<Extra> getExtras() {
        return extras;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void incrementarCantidad() {
        cantidad += 1;
    }

    public void disminuirCantidad() {
        cantidad -= 1;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getTotalExtras() {
        return extras.stream()
                .map(extra -> extra.getPrecio() != null ? extra.getPrecio() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSubtotal() {
        BigDecimal precioBase = producto.getPrecioBase() != null ? producto.getPrecioBase() : BigDecimal.ZERO;
        return precioBase.add(getTotalExtras()).multiply(BigDecimal.valueOf(cantidad));
    }

    public String getExtrasTexto() {
        if (extras.isEmpty()) {
            return "sin extras";
        }
        return extras.stream().map(Extra::getNombre).collect(Collectors.joining(", "));
    }
}

