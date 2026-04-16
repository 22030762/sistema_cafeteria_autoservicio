package main.sistema_cafeteria_autoservicio.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
        try {
            AuthDao authDao = new AuthDao();
            var usuario = authDao.login(usernameField.getText(), passwordField.getText());
            if (usuario) {
                System.out.println("Login exitoso");
                errorLabel.setText("Login exitoso");
            } else {
                errorLabel.setText("Credenciales incorrectas");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error al conectar con la base de datos");
        }
    }
}
