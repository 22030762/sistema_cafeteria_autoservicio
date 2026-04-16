package main.sistema_cafeteria_autoservicio.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.sistema_cafeteria_autoservicio.Launcher;
import main.sistema_cafeteria_autoservicio.Models.CarritoItem;
import main.sistema_cafeteria_autoservicio.Models.Extra;
import main.sistema_cafeteria_autoservicio.Models.Producto;
import main.sistema_cafeteria_autoservicio.Utils.Dao.ExtraDao;
import main.sistema_cafeteria_autoservicio.Utils.Dao.ProductoDao;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class PosMenuController {

    @FXML
    private Label usuarioLabel;
    @FXML
    private Label estadoLabel;
    @FXML
    private Label categoriaActualLabel;
    @FXML
    private Label extrasSeleccionadosLabel;
    @FXML
    private FlowPane productosFlow;
    @FXML
    private VBox extrasBox;

    private final List<Producto> productos = new ArrayList<>();
    private final List<Extra> extrasDisponibles = new ArrayList<>();
    private final Map<String, CarritoItem> carritoDetalles = new LinkedHashMap<>();

    private String categoriaSeleccionada = "TODOS";

    @FXML
    public void initialize() {
        cargarExtrasActivos();
        cargarProductosActivos();
        actualizarResumenPedido();
    }

    public void setUsuarioSesion(String username) {
        usuarioLabel.setText("Cajero: " + username);
    }

    @FXML
    public void handleAbrirCarrito() {
        if (carritoDetalles.isEmpty()) {
            estadoLabel.setText("El carrito esta vacio");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    Launcher.class.getResource("/main/sistema_cafeteria_autoservicio/carrito-modal-view.fxml")
            );
            Scene scene = new Scene(loader.load());

            CarritoModalController controller = loader.getController();
            controller.setCarrito(carritoDetalles, this::actualizarResumenPedido);

            Stage owner = (Stage) usuarioLabel.getScene().getWindow();
            Stage modal = new Stage();
            modal.setTitle("Carrito de compra");
            modal.initOwner(owner);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.setScene(scene);
            modal.showAndWait();

            actualizarResumenPedido();
        } catch (IOException e) {
            e.printStackTrace();
            estadoLabel.setText("No se pudo abrir el carrito");
        }
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

    private void cargarExtrasActivos() {
        try {
            ExtraDao extraDao = new ExtraDao();
            extrasDisponibles.clear();
            extrasDisponibles.addAll(extraDao.findActivos());
            renderExtras();
        } catch (Exception e) {
            e.printStackTrace();
            extrasSeleccionadosLabel.setText("No se pudieron cargar extras");
        }
    }

    private void renderExtras() {
        extrasBox.getChildren().clear();

        if (extrasDisponibles.isEmpty()) {
            Label vacio = new Label("Sin extras activos");
            vacio.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
            extrasBox.getChildren().add(vacio);
            extrasSeleccionadosLabel.setText("Extras seleccionados: 0");
            return;
        }

        for (Extra extra : extrasDisponibles) {
            CheckBox check = new CheckBox(extra.getNombre() + " (+" + formatearMoneda(extra.getPrecio()) + ")");
            check.setStyle("-fx-font-size: 12px; -fx-text-fill: #111827;");
            check.setUserData(extra);
            check.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarEtiquetaExtras());
            extrasBox.getChildren().add(check);
        }

        actualizarEtiquetaExtras();
    }

    private void actualizarEtiquetaExtras() {
        long totalSeleccionados = extrasBox.getChildren().stream()
                .filter(CheckBox.class::isInstance)
                .map(CheckBox.class::cast)
                .filter(CheckBox::isSelected)
                .count();
        extrasSeleccionadosLabel.setText("Extras seleccionados: " + totalSeleccionados);
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

        Button agregarButton = new Button("Agregar con extras");
        agregarButton.setStyle("-fx-background-color: #111827; -fx-text-fill: white; -fx-background-radius: 20;");
        agregarButton.setOnAction(event -> agregarProducto(producto));

        HBox acciones = new HBox(agregarButton);
        acciones.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(nombreLabel, descripcionLabel, spacer, precioLabel, acciones);
        return card;
    }

    private void agregarProducto(Producto producto) {
        List<Extra> extrasSeleccionados = obtenerExtrasSeleccionados();
        String claveDetalle = construirClaveDetalle(producto.getIdProducto(), extrasSeleccionados);

        CarritoItem detalle = carritoDetalles.get(claveDetalle);
        if (detalle == null) {
            detalle = new CarritoItem(producto, extrasSeleccionados);
            carritoDetalles.put(claveDetalle, detalle);
        } else {
            detalle.incrementarCantidad();
        }

        actualizarResumenPedido();
        estadoLabel.setText("Agregado: " + producto.getNombre() + " (extras: " + extrasSeleccionados.size() + ")");
    }

    private List<Extra> obtenerExtrasSeleccionados() {
        return extrasBox.getChildren().stream()
                .filter(CheckBox.class::isInstance)
                .map(CheckBox.class::cast)
                .filter(CheckBox::isSelected)
                .map(checkBox -> (Extra) checkBox.getUserData())
                .collect(Collectors.toList());
    }

    private String construirClaveDetalle(int idProducto, List<Extra> extrasSeleccionados) {
        String extrasKey = extrasSeleccionados.stream()
                .map(Extra::getIdExtra)
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining("-"));
        return idProducto + "|" + extrasKey;
    }

    private void actualizarResumenPedido() {
        int totalItems = 0;
        BigDecimal total = BigDecimal.ZERO;

        for (CarritoItem detalle : carritoDetalles.values()) {
            totalItems += detalle.getCantidad();
            total = total.add(detalle.getSubtotal());
        }

        if (totalItems == 0) {
            estadoLabel.setText("Carrito vacio");
        } else {
            estadoLabel.setText("Carrito: " + totalItems + " items | Total: " + formatearMoneda(total));
        }
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
