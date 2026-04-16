package main.sistema_cafeteria_autoservicio.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.sistema_cafeteria_autoservicio.Models.CarritoItem;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class CarritoModalController {

    @FXML
    private VBox lineasCarritoBox;
    @FXML
    private Label itemsLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label estadoLabel;

    private Map<String, CarritoItem> carrito = new LinkedHashMap<>();
    private Runnable onCarritoActualizado = () -> {
    };

    public void setCarrito(Map<String, CarritoItem> carrito, Runnable onCarritoActualizado) {
        this.carrito = carrito;
        this.onCarritoActualizado = onCarritoActualizado;
        renderCarrito();
    }

    @FXML
    public void handleCerrar() {
        Stage stage = (Stage) totalLabel.getScene().getWindow();
        stage.close();
    }

    private void renderCarrito() {
        lineasCarritoBox.getChildren().clear();

        int totalItems = 0;
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, CarritoItem> entry : carrito.entrySet()) {
            String key = entry.getKey();
            CarritoItem item = entry.getValue();

            totalItems += item.getCantidad();
            total = total.add(item.getSubtotal());

            VBox detalleBox = new VBox(2);
            Label nombre = new Label(item.getProducto().getNombre() + " x" + item.getCantidad());
            nombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

            Label extras = new Label("Extras: " + item.getExtrasTexto());
            extras.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

            Label subtotal = new Label(formatearMoneda(item.getSubtotal()));
            subtotal.setStyle("-fx-font-size: 13px; -fx-text-fill: #be123c; -fx-font-weight: bold;");

            detalleBox.getChildren().addAll(nombre, extras, subtotal);

            Button menos = new Button("-");
            menos.setOnAction(event -> cambiarCantidad(key, -1));
            menos.setStyle("-fx-background-color: #cbd5e1; -fx-text-fill: #0f172a;");

            Button mas = new Button("+");
            mas.setOnAction(event -> cambiarCantidad(key, 1));
            mas.setStyle("-fx-background-color: #1d4ed8; -fx-text-fill: white;");

            Button eliminar = new Button("Eliminar");
            eliminar.setOnAction(event -> eliminarLinea(key));
            eliminar.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white;");

            HBox acciones = new HBox(6, menos, mas, eliminar);
            acciones.setAlignment(Pos.CENTER_RIGHT);

            Region separator = new Region();
            HBox.setHgrow(separator, Priority.ALWAYS);

            HBox fila = new HBox(12, detalleBox, separator, acciones);
            fila.setPadding(new Insets(8));
            fila.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

            lineasCarritoBox.getChildren().add(fila);
        }

        if (lineasCarritoBox.getChildren().isEmpty()) {
            Label vacio = new Label("No hay productos en el carrito.");
            vacio.setStyle("-fx-text-fill: #64748b;");
            lineasCarritoBox.getChildren().add(vacio);
            estadoLabel.setText("Agrega productos desde el menu principal");
        } else {
            estadoLabel.setText("Puedes ajustar cantidades o eliminar lineas");
        }

        itemsLabel.setText("Items: " + totalItems);
        totalLabel.setText("Total a pagar: " + formatearMoneda(total));
        onCarritoActualizado.run();
    }

    private void cambiarCantidad(String key, int delta) {
        CarritoItem item = carrito.get(key);
        if (item == null) {
            return;
        }

        int nuevaCantidad = item.getCantidad() + delta;
        if (nuevaCantidad <= 0) {
            carrito.remove(key);
        } else {
            item.setCantidad(nuevaCantidad);
        }

        renderCarrito();
    }

    private void eliminarLinea(String key) {
        carrito.remove(key);
        renderCarrito();
    }

    private String formatearMoneda(BigDecimal monto) {
        if (monto == null) {
            return "$0.00";
        }
        return String.format(Locale.US, "$%.2f", monto);
    }
}

