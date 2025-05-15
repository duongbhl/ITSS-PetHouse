package org.example.petproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.petproject.controller.dslamdepController;

public class dslamdep extends Application {
    @Override
    public void start(Stage primaryStage)throws Exception {
        dslamdepController controller = new dslamdepController("U002");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/dslamdepScreen.fxml"));
        loader.setController(controller);
        Parent root = loader.load();
        primaryStage.setTitle("Pet Project");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
