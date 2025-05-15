package org.example.petproject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.petproject.dao.UserDAO;

import java.io.IOException;

public class luutruController {

    private String ownerId;

    public luutruController() {}

    public luutruController(String ownerId) {
        this.ownerId = ownerId;
    }

    @FXML
    private Label ownerName;

    @FXML
    void arrowPressedButton(ActionEvent event) {
        //main
    }

    @FXML
    void roomlistButton(ActionEvent event) {
        dsphongController controller = new dsphongController("U002");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/dsphongScreen.fxml"));
        loader.setController(controller);
        Parent root = null;
        try{
            root=loader.load();
        }catch(IOException e){
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        Stage newStage = (Stage) ownerName.getScene().getWindow();
        newStage.setScene(scene);
        newStage.show();

    }

    @FXML
    void petlistButton(ActionEvent event) {
        dsluutruController controller = new dsluutruController("U002");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/dsluutruScreen.fxml"));
        loader.setController(controller);
        Parent root = null;
        try{
            root = loader.load();
        }catch(IOException e){
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        Stage newStage = (Stage) ownerName.getScene().getWindow();
        newStage.setScene(scene);
        newStage.show();
    }

    @FXML
    void initialize() {
        ownerName.setText(new UserDAO().getUserByOwnerID(this.ownerId).getFullName());
    }
}
