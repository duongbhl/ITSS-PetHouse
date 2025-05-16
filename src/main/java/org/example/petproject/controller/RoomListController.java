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

public class RoomListController {

    private String ownerID;

    public RoomListController() {
    }

    public RoomListController(String ownerID) {
        this.ownerID = ownerID;
    }

    @FXML
    private Label ownerName;

    @FXML
    void arrowPressedButton(ActionEvent event) {
        BoardingController controller = new BoardingController("U002");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/RoomListView.fxml"));
        loader.setController(controller);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        Stage stage = (Stage) this.ownerName.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void initialize() {
        ownerName.setText(new UserDAO().getUserByOwnerID(this.ownerID).getFullName());
    }

}