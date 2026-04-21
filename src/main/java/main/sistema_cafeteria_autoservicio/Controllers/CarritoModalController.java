package main.sistema_cafeteria_autoservicio.Controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.sistema_cafeteria_autoservicio.Models.CarritoItem;
import main.sistema_cafeteria_autoservicio.Models.MetodoPago;
import main.sistema_cafeteria_autoservicio.Services.CheckoutService;
import main.sistema_cafeteria_autoservicio.Utils.Dao.MetodoPagoDao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
    @FXML
    private ComboBox<MetodoPago> metodoPagoCombo;
    @FXML
    private TextField referenciaField;
    @FXML
    private Button checkoutButton;

    private Map<String, CarritoItem> carrito = new LinkedHashMap<>();
    private final List<MetodoPago> metodosPago = new ArrayList<>();
    private final CheckoutService checkoutService = new CheckoutService();
    private Runnable onCarritoActualizado = () -> {
    };
    private int idUsuarioSesion;
    private String nombreUsuarioSesion = "";

    @FXML
    public void initialize() {
        cargarMetodosPago();
    }

    public void setContextoCheckout(int idUsuarioSesion, String nombreUsuarioSesion) {
        this.idUsuarioSesion = idUsuarioSesion;
        this.nombreUsuarioSesion = nombreUsuarioSesion == null ? "" : nombreUsuarioSesion;
        actualizarEstadoSesion();
    }

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

    @FXML
    public void handleCheckout() {
        if (carrito.isEmpty()) {
            estadoLabel.setText("No hay productos para cobrar");
            return;
        }
        MetodoPago metodo = metodoPagoCombo.getValue();
        if (metodo == null) {
            estadoLabel.setText("Selecciona un metodo de pago");
            return;
        }
        if (idUsuarioSesion <= 0) {
            estadoLabel.setText("Sesion invalida. Vuelve a iniciar sesion");
            return;
        }

        try {
            var resultado = checkoutService.procesarCheckout(
                    idUsuarioSesion,
                    carrito,
                    metodo.getIdMetodo(),
                    referenciaField != null ? referenciaField.getText() : ""
            );

            carrito.clear();
            renderCarrito();
            if (referenciaField != null) {
                referenciaField.clear();
            }
            if (metodoPagoCombo != null) {
                metodoPagoCombo.getSelectionModel().clearSelection();
            }
            estadoLabel.setText("Pedido #" + resultado.getIdPedido() + " cobrado correctamente");
            mostrarConfirmacion(resultado.getIdPedido(), resultado.getTotal());
        } catch (Exception ex) {
            ex.printStackTrace();
            estadoLabel.setText("No se pudo completar el checkout");
        }
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
        checkoutButton.setDisable(totalItems == 0 || metodoPagoCombo.getSelectionModel().isEmpty());
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

    private void cargarMetodosPago() {
        try {
            MetodoPagoDao metodoPagoDao = new MetodoPagoDao();
            metodosPago.clear();
            metodosPago.addAll(metodoPagoDao.findAll());

            metodoPagoCombo.getItems().setAll(metodosPago);
            metodoPagoCombo.setCellFactory(list -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(MetodoPago item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });
            metodoPagoCombo.setButtonCell(new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(MetodoPago item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "Selecciona metodo" : item.getNombre());
                }
            });
            metodoPagoCombo.valueProperty().addListener((obs, oldVal, newVal) ->
                    checkoutButton.setDisable(carrito.isEmpty() || newVal == null)
            );
            if (metodosPago.isEmpty()) {
                estadoLabel.setText("No hay metodos de pago configurados");
                checkoutButton.setDisable(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            estadoLabel.setText("No se pudieron cargar los metodos de pago");
            checkoutButton.setDisable(true);
        }
    }

    private void mostrarConfirmacion(int idPedido, BigDecimal total) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Checkout completado");
        alert.setHeaderText("Venta registrada");
        alert.setContentText("Pedido #" + idPedido + "\nCajero: " + nombreUsuarioSesion + "\nTotal: " + formatearMoneda(total));
        alert.showAndWait();
    }

    private void actualizarEstadoSesion() {
        if (idUsuarioSesion > 0 && !nombreUsuarioSesion.isBlank()) {
            estadoLabel.setText("Cajero activo: " + nombreUsuarioSesion);
        }
    }
}

