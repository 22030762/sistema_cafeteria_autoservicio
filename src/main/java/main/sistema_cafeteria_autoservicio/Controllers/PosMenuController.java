package main.sistema_cafeteria_autoservicio.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.sistema_cafeteria_autoservicio.Launcher;
import main.sistema_cafeteria_autoservicio.Models.Producto;
import main.sistema_cafeteria_autoservicio.Utils.Dao.ProductoDao;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PosMenuController {

    @FXML
    private Label usuarioLabel;
    @FXML
    private Label estadoLabel;
    @FXML
    private Label categoriaActualLabel;
    @FXML
    private Label itemsResumenLabel;
    @FXML
    private Label totalResumenLabel;
    @FXML
    private FlowPane productosFlow;
    @FXML
    private VBox carritoItemsBox;

    private final List<Producto> productos = new ArrayList<>();
    private final Map<Integer, Integer> carritoCantidades = new LinkedHashMap<>();
    private final Map<Integer, Producto> carritoProductos = new LinkedHashMap<>();

    private String categoriaSeleccionada = "TODOS";

    @FXML
    public void initialize() {
        cargarProductosActivos();
    }

    public void setUsuarioSesion(String username) {
        usuarioLabel.setText("Cajero: " + username);
    }

    @FXML
    public void handleMostrarTodos() {
        categoriaSeleccionada = "TODOS";
        renderProductos();
    }

    @FXML
    public void handleMostrarComidas() {
        categoriaSeleccionada = "COMIDAS";
        renderProductos();
    }

    @FXML
    public void handleMostrarBebidas() {
        categoriaSeleccionada = "BEBIDAS";
        renderProductos();
    }

    @FXML
    public void handleMostrarPostres() {
        categoriaSeleccionada = "POSTRES";
        renderProductos();
    }

    @FXML
    public void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Launcher.class.getResource("/main/sistema_cafeteria_autoservicio/auth-view.fxml")
            );
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) usuarioLabel.getScene().getWindow();
            stage.setTitle("Auth");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            estadoLabel.setText("No se pudo cerrar sesion");
        }
    }

    private void cargarProductosActivos() {
        try {
            ProductoDao productoDao = new ProductoDao();
            productos.clear();
            productos.addAll(productoDao.findActivos());
            renderProductos();

            if (productos.isEmpty()) {
                estadoLabel.setText("No hay productos activos disponibles");
            } else {
                estadoLabel.setText("Productos cargados: " + productos.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
            estadoLabel.setText("Error cargando productos");
        }
    }

    private void renderProductos() {
        productosFlow.getChildren().clear();
        categoriaActualLabel.setText("Categoria: " + capitalizar(categoriaSeleccionada));

        for (Producto producto : productos) {
            if (perteneceCategoria(producto, categoriaSeleccionada)) {
                productosFlow.getChildren().add(crearTarjetaProducto(producto));
            }
        }
    }

    private VBox crearTarjetaProducto(Producto producto) {
        VBox card = new VBox(8);
        card.setPrefWidth(220);
        card.setMinHeight(170);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: #e5e7eb; -fx-border-radius: 12;");

        Label nombreLabel = new Label(producto.getNombre());
        nombreLabel.setWrapText(true);
        nombreLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        String descripcion = producto.getDescripcion() != null ? producto.getDescripcion() : "Sin descripcion";
        Label descripcionLabel = new Label(descripcion);
        descripcionLabel.setWrapText(true);
        descripcionLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        Label precioLabel = new Label(formatearMoneda(producto.getPrecioBase()));
        precioLabel.setStyle("-fx-text-fill: #f43f5e; -fx-font-size: 16px; -fx-font-weight: bold;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button agregarButton = new Button("Agregar");
        agregarButton.setStyle("-fx-background-color: #111827; -fx-text-fill: white; -fx-background-radius: 20;");
        agregarButton.setOnAction(event -> agregarProducto(producto));

        HBox acciones = new HBox(agregarButton);
        acciones.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(nombreLabel, descripcionLabel, spacer, precioLabel, acciones);
        return card;
    }

    private void agregarProducto(Producto producto) {
        int cantidadActual = carritoCantidades.getOrDefault(producto.getIdProducto(), 0);
        carritoCantidades.put(producto.getIdProducto(), cantidadActual + 1);
        carritoProductos.put(producto.getIdProducto(), producto);

        actualizarResumenPedido();
        estadoLabel.setText("Agregado: " + producto.getNombre());
    }

    private void actualizarResumenPedido() {
        int totalItems = 0;
        BigDecimal total = BigDecimal.ZERO;

        carritoItemsBox.getChildren().clear();

        for (Map.Entry<Integer, Integer> entry : carritoCantidades.entrySet()) {
            Producto producto = carritoProductos.get(entry.getKey());
            int cantidad = entry.getValue();

            if (producto == null) {
                continue;
            }

            totalItems += cantidad;
            BigDecimal subtotal = producto.getPrecioBase().multiply(BigDecimal.valueOf(cantidad));
            total = total.add(subtotal);

            Label linea = new Label(producto.getNombre() + " x" + cantidad + "  -  " + formatearMoneda(subtotal));
            linea.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
            carritoItemsBox.getChildren().add(linea);
        }

        if (carritoItemsBox.getChildren().isEmpty()) {
            Label vacio = new Label("Aun no hay productos en el pedido.");
            vacio.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px;");
            carritoItemsBox.getChildren().add(vacio);
        }

        itemsResumenLabel.setText("Items: " + totalItems);
        totalResumenLabel.setText("Total: " + formatearMoneda(total));
    }

    private boolean perteneceCategoria(Producto producto, String categoria) {
        if ("TODOS".equals(categoria)) {
            return true;
        }

        String texto = ((producto.getNombre() == null ? "" : producto.getNombre()) + " " +
                (producto.getDescripcion() == null ? "" : producto.getDescripcion())).toLowerCase(Locale.ROOT);

        switch (categoria) {
            case "BEBIDAS":
                return contieneAlgun(texto, "cafe", "te", "refresco", "jugo", "agua", "bebida");
            case "POSTRES":
                return contieneAlgun(texto, "pastel", "postre", "galleta", "helado", "chocolate");
            case "COMIDAS":
                return !perteneceCategoria(producto, "BEBIDAS") && !perteneceCategoria(producto, "POSTRES");
            default:
                return true;
        }
    }

    private boolean contieneAlgun(String texto, String... palabras) {
        for (String palabra : palabras) {
            if (texto.contains(palabra)) {
                return true;
            }
        }
        return false;
    }

    private String formatearMoneda(BigDecimal monto) {
        if (monto == null) {
            return "$0.00";
        }
        return String.format(Locale.US, "$%.2f", monto);
    }

    private String capitalizar(String texto) {
        String base = texto.toLowerCase(Locale.ROOT);
        return base.substring(0, 1).toUpperCase(Locale.ROOT) + base.substring(1);
    }
}
