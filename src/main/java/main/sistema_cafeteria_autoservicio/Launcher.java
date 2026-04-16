package main.sistema_cafeteria_autoservicio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Launcher.class.getResource("/main/sistema_cafeteria_autoservicio/auth-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Auth");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
