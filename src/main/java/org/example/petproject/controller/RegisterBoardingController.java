package org.example.petproject.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.petproject.dao.PetDAO;
import org.example.petproject.dao.RoomDAO;
import org.example.petproject.dao.UserDAO;
import org.example.petproject.model.ServiceBooking;
import org.example.petproject.service.UserService;

import java.io.IOException;

public class RegisterBoardingController {
    UserDAO userdao = new UserDAO();
    PetDAO petdao = new PetDAO();
    RoomDAO roomdao = new RoomDAO();

    UserService userservice = new UserService();

    private String ownerID;

    public RegisterBoardingController() {
    }

    public RegisterBoardingController(String ownerID) {
        this.ownerID = ownerID;
    }

    @FXML
    private Label ownerName;

    @FXML
    private ComboBox<String> petSelected;

    @FXML
    private DatePicker inscheduleSelected;

    @FXML
    private DatePicker outscheduleSelected;

    @FXML
    private ComboBox<String> roomSelected;

    @FXML
    private TextField noteText;

    @FXML
    void arrowPressedButton(ActionEvent event) {
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
        Stage stage = (Stage) this.ownerName.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void bookAppointmentButton(ActionEvent event) {
        userservice.dkdvluutru(inscheduleSelected.getValue(),
                outscheduleSelected.getValue(),
                noteText.getText(),
                ServiceBooking.Status.pending,
                petSelected.getValue(),
                "S002",
                roomSelected.getValue());
    }

    @FXML
    void initialize() {
        ownerName.setText(userdao.getUserByOwnerID(this.ownerID).getFullName());
        petSelected.setItems(FXCollections.observableArrayList(petdao.findAllByOwnerId(this.ownerID)));
        roomSelected.setItems(FXCollections.observableArrayList(roomdao.findAllNames()));

    }

}