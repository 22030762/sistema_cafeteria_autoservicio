package main.sistema_cafeteria_autoservicio.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.sistema_cafeteria_autoservicio.Launcher;
import main.sistema_cafeteria_autoservicio.Utils.Dao.AuthDao;


public class AuthController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText() != null ? usernameField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText().trim() : "";

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Completa usuario y contraseña");
            return;
        }

        try {
            AuthDao authDao = new AuthDao();
            boolean autenticado = authDao.login(username, password);

            if (autenticado) {
                abrirMenuPos(username);
            } else {
                errorLabel.setText("Credenciales incorrectas");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error al conectar con la base de datos");
        }
    }

    private void abrirMenuPos(String username) throws java.io.IOException {
        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource("/main/sistema_cafeteria_autoservicio/pos-menu-view.fxml")
        );

        Scene posScene = new Scene(loader.load());
        PosMenuController posMenuController = loader.getController();
        posMenuController.setUsuarioSesion(username);

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setTitle("POS - Menu");
        stage.setScene(posScene);
        stage.show();
    }
}
