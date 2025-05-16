package org.example.petproject.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.dao.PetDAO;
import org.example.petproject.model.Appointment;
import org.example.petproject.model.User;
import org.example.petproject.service.UserService;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MakeAppointmentController implements DashboardControllerBase, Initializable {

    @FXML
    private ImageView imgLogo;
    @FXML
    private Label ownerName;
    @FXML
    private ImageView ownerAvatar;

    @FXML
    private ComboBox<String> petSelected;
    @FXML
    private ComboBox<String> examSelected;
    @FXML
    private DatePicker scheduleSelected;

    private final UserService userService = new UserService();
    private final PetDAO petDAO = new PetDAO();
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ComboBox exam options
        examSelected.setItems(FXCollections.observableArrayList(
                "Khám tổng quát", "Tiêm phòng", "Siêu âm"));
    }

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        // Header: tên + avatar
        ownerName.setText(user.getFullName());
        if (user.getAvatarUrl() != null) {
            ownerAvatar.setImage(new Image(user.getAvatarUrl()));
        }
        imgLogo.setImage(new Image(
                getClass().getResource("/assets/logo.png").toExternalForm()));
        // Load pets
        List<String> pets = petDAO.findAllByOwnerId(user.getUserId());
        petSelected.setItems(FXCollections.observableArrayList(pets));
    }

    @FXML
    private void bookAppointmentButton(ActionEvent event) {
        try {
            // Gọi service đặt lịch
            userService.datlichkham(
                    petSelected.getValue(),
                    scheduleSelected.getValue(),
                    examSelected.getValue(),
                    Appointment.Status.pending);
            // Thông báo thành công
            new Alert(Alert.AlertType.INFORMATION, "Đặt lịch thành công!").showAndWait();

            // Reset form về trạng thái trắng
            petSelected.getSelectionModel().clearSelection();
            examSelected.getSelectionModel().clearSelection();
            scheduleSelected.setValue(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            // Thông báo lỗi
            new Alert(Alert.AlertType.ERROR,
                    "Đặt lịch thất bại:\n" + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleLogoClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/OwnerDashboardView.fxml"));
            Parent root = loader.load();
            DashboardControllerBase ctrl = loader.getController();
            ctrl.initUser(currentUser);
            Scene scene = imgLogo.getScene();
            root.getStylesheets().setAll(scene.getStylesheets());
            scene.setRoot(root);
            Stage stage = (Stage) scene.getWindow();
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}