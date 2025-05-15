package org.example.petproject;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.petproject.controller.luutruController;

import java.io.IOException;

public class luutru extends Application {
    @Override
    public void start(Stage primaryStage)throws Exception {
        luutruController controller = new luutruController("U002");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/luutruScreen.fxml"));
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Pet House");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
