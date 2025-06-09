package org.example.petproject.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.dao.PetDAO;
import org.example.petproject.dao.RoomDAO;
import org.example.petproject.dao.UserDAO;
import org.example.petproject.model.ServiceBooking;
import org.example.petproject.service.UserService;
import org.example.petproject.util.SessionManager;

import java.io.IOException;
import java.net.URL;

@SuppressWarnings("unused")
public class RegisterBoardingController {
    UserDAO userdao = new UserDAO();
    PetDAO petdao = new PetDAO();
    RoomDAO roomdao = new RoomDAO();

    UserService userservice = new UserService();

    private String ownerID = SessionManager.getCurrentUser().getUserId();

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
    private TextArea noteText;

    @FXML
    private ImageView imgLogo;

    @FXML
    private void handleLogoClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/BoardingListView.fxml"));
            Parent root = loader.load();
            Scene scene = imgLogo.getScene();
            root.getStylesheets().setAll(scene.getStylesheets());
            scene.setRoot(root);
            Stage stage = (Stage) scene.getWindow();
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Lỗi");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    void bookAppointmentButton(ActionEvent event) {
        StringBuilder details = new StringBuilder();
        details.append("Note: " + noteText.getText().trim() + "\n");
        details.append("Thoi gian vao: " + inscheduleSelected.getValue().toString().trim() + "\n");
        details.append("Thoi gian ra: " + outscheduleSelected.getValue().toString().trim() + "\n");
        details.append(
                "Gia ca: " + roomdao.findByName(roomSelected.getValue().toString().trim()).getPricePerDay() + "\n");
        try {
            userservice.dkdvluutru(
                    inscheduleSelected.getValue(),
                    outscheduleSelected.getValue(),
                    details.toString(),
                    ServiceBooking.Status.pending,
                    petSelected.getValue(),
                    "S002",
                    roomSelected.getValue());
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Thêm thành công");
            a.setHeaderText(null);
            a.showAndWait();
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Thêm thất bại");
            a.setHeaderText(null);
            a.showAndWait();
        }
    }

    @FXML
    void initialize() {
        ownerName.setText(userdao.getUserByOwnerID(this.ownerID).getFullName());
        petSelected.setItems(FXCollections.observableArrayList(petdao.findAllByOwnerId(this.ownerID)));
        roomSelected.setItems(FXCollections.observableArrayList(roomdao.findAllNames()));

    }

}