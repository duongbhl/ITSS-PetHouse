package org.example.petproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.petproject.controller.datlichkhamController;

import java.io.IOException;

public class datlichkham extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        datlichkhamController controller = new datlichkhamController("U002");
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/org/example/petproject/datlichkhamScreen.fxml"));
        fxmlLoader.setController(controller);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("DLK");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}