module main.sistema_cafeteria_autoservicio {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens main.sistema_cafeteria_autoservicio to javafx.fxml;
    opens main.sistema_cafeteria_autoservicio.Controllers to javafx.fxml;
    exports main.sistema_cafeteria_autoservicio;
}