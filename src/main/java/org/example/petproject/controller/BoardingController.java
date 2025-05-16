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

public class BoardingController {

    private String ownerId;

    public BoardingController() {
    }

    public BoardingController(String ownerId) {
        this.ownerId = ownerId;
    }

    @FXML
    private Label ownerName;

    @FXML
    void arrowPressedButton(ActionEvent event) {
        // main
    }

    @FXML
    void roomlistButton(ActionEvent event) {
        RoomListController controller = new RoomListController("U002");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/RoomListView.fxml"));
        loader.setController(controller);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        Stage newStage = (Stage) ownerName.getScene().getWindow();
        newStage.setScene(scene);
        newStage.show();

    }

    @FXML
    void petlistButton(ActionEvent event) {
        BoardingListController controller = new BoardingListController("U002");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/BoardingListView.fxml"));
        loader.setController(controller);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
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