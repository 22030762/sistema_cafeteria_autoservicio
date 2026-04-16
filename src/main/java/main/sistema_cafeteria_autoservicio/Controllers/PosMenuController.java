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
import javafx.stage.Stage;
import main.sistema_cafeteria_autoservicio.Launcher;
import main.sistema_cafeteria_autoservicio.Models.Extra;
import main.sistema_cafeteria_autoservicio.Models.Producto;
import main.sistema_cafeteria_autoservicio.Utils.Dao.ExtraDao;
import main.sistema_cafeteria_autoservicio.Utils.Dao.ProductoDao;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
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
    private Label itemsResumenLabel;
    @FXML
    private Label totalResumenLabel;
    @FXML
    private Label extrasSeleccionadosLabel;
    @FXML
    private FlowPane productosFlow;
    @FXML
    private VBox carritoItemsBox;
    @FXML
    private VBox extrasBox;

    private final List<Producto> productos = new ArrayList<>();
    private final List<Extra> extrasDisponibles = new ArrayList<>();
    private final Map<String, DetalleLinea> carritoDetalles = new LinkedHashMap<>();

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

        DetalleLinea detalle = carritoDetalles.get(claveDetalle);
        if (detalle == null) {
            detalle = new DetalleLinea(producto, extrasSeleccionados);
            carritoDetalles.put(claveDetalle, detalle);
        } else {
            detalle.cantidad += 1;
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
        carritoItemsBox.getChildren().clear();

        for (Map.Entry<String, DetalleLinea> entry : carritoDetalles.entrySet()) {
            String claveDetalle = entry.getKey();
            DetalleLinea detalle = entry.getValue();

            totalItems += detalle.cantidad;
            BigDecimal subtotal = detalle.calcularSubtotal();
            total = total.add(subtotal);

            VBox infoLinea = new VBox(2);
            Label nombreLinea = new Label(detalle.producto.getNombre() + " x" + detalle.cantidad);
            nombreLinea.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");

            Label extrasLinea = new Label("Extras: " + detalle.obtenerNombresExtras());
            extrasLinea.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 12px;");

            Label subtotalLinea = new Label(formatearMoneda(subtotal));
            subtotalLinea.setStyle("-fx-text-fill: #fda4af; -fx-font-size: 13px;");

            infoLinea.getChildren().addAll(nombreLinea, extrasLinea, subtotalLinea);

            Button menosButton = new Button("-");
            menosButton.setStyle("-fx-background-color: #334155; -fx-text-fill: white;");
            menosButton.setOnAction(event -> modificarCantidad(claveDetalle, -1));

            Button masButton = new Button("+");
            masButton.setStyle("-fx-background-color: #334155; -fx-text-fill: white;");
            masButton.setOnAction(event -> modificarCantidad(claveDetalle, 1));

            Button eliminarButton = new Button("X");
            eliminarButton.setStyle("-fx-background-color: #7f1d1d; -fx-text-fill: white;");
            eliminarButton.setOnAction(event -> eliminarDetalle(claveDetalle));

            HBox acciones = new HBox(6, menosButton, masButton, eliminarButton);
            acciones.setAlignment(Pos.CENTER_RIGHT);

            Region separador = new Region();
            HBox.setHgrow(separador, Priority.ALWAYS);

            HBox linea = new HBox(8, infoLinea, separador, acciones);
            linea.setPadding(new Insets(6, 0, 6, 0));
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

    private void modificarCantidad(String claveDetalle, int delta) {
        DetalleLinea detalle = carritoDetalles.get(claveDetalle);
        if (detalle == null) {
            return;
        }

        int nuevaCantidad = detalle.cantidad + delta;
        if (nuevaCantidad <= 0) {
            carritoDetalles.remove(claveDetalle);
            estadoLabel.setText("Linea eliminada del pedido");
        } else {
            detalle.cantidad = nuevaCantidad;
            estadoLabel.setText("Cantidad actualizada");
        }

        actualizarResumenPedido();
    }

    private void eliminarDetalle(String claveDetalle) {
        carritoDetalles.remove(claveDetalle);
        estadoLabel.setText("Linea eliminada del pedido");
        actualizarResumenPedido();
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

    private static class DetalleLinea {
        private final Producto producto;
        private final List<Extra> extras;
        private int cantidad;

        private DetalleLinea(Producto producto, List<Extra> extras) {
            this.producto = producto;
            this.extras = extras.stream()
                    .sorted(Comparator.comparingInt(Extra::getIdExtra))
                    .collect(Collectors.toCollection(ArrayList::new));
            this.cantidad = 1;
        }

        private BigDecimal calcularSubtotal() {
            BigDecimal precioBase = producto.getPrecioBase() != null ? producto.getPrecioBase() : BigDecimal.ZERO;
            BigDecimal totalExtras = extras.stream()
                    .map(extra -> extra.getPrecio() != null ? extra.getPrecio() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return precioBase.add(totalExtras).multiply(BigDecimal.valueOf(cantidad));
        }

        private String obtenerNombresExtras() {
            if (extras.isEmpty()) {
                return "sin extras";
            }
            return extras.stream().map(Extra::getNombre).collect(Collectors.joining(", "));
        }
    }
}
