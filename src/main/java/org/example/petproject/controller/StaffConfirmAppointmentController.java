package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.controller.Dashboard.StaffDashboardController;
import org.example.petproject.dao.AppointmentDAO;
import org.example.petproject.model.Appointment;
import org.example.petproject.model.User;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class StaffConfirmAppointmentController implements Initializable, DashboardControllerBase {

    @FXML
    private ImageView logoImageView;
    @FXML
    private Label userNameLabel;
    @FXML
    private ImageView userAvatarImageView;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private DatePicker fromDateDatePicker;
    @FXML
    private DatePicker toDateDatePicker;
    @FXML
    private VBox notificationContainer;

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ComboBox with status options
        statusComboBox.getItems().addAll("Pending", "Confirmed", "Completed", "Cancelled");
    }

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        if (userNameLabel != null) {
            userNameLabel.setText(user.getFullName());
        }

        // Load default avatar if user avatar is not available
        if (userAvatarImageView != null) {
            try {
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
                    userAvatarImageView.setImage(new javafx.scene.image.Image(user.getAvatarUrl()));
                } else {
                    // Load default avatar from resources
                    userAvatarImageView.setImage(new javafx.scene.image.Image(
                            getClass().getResourceAsStream("/assets/icons/user.png")));
                }
            } catch (Exception e) {
                System.err.println("Error loading avatar: " + e.getMessage());
                // Load default avatar as fallback
                try {
                    userAvatarImageView.setImage(new javafx.scene.image.Image(
                            getClass().getResourceAsStream("/assets/icons/user.png")));
                } catch (Exception ex) {
                    System.err.println("Error loading default avatar: " + ex.getMessage());
                }
            }
        }

        // Load logo
        if (logoImageView != null) {
            try {
                logoImageView.setImage(new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/assets/logo.png")));
            } catch (Exception e) {
                System.err.println("Error loading logo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleFilterButtonAction() {
        String status = statusComboBox.getValue();
        LocalDate fromDate = fromDateDatePicker.getValue();
        LocalDate toDate = toDateDatePicker.getValue();

        // Get filtered appointments directly from database
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        List<Appointment> filteredAppointments;

        // Use the new database filtering method
        if (status != null || fromDate != null || toDate != null) {
            filteredAppointments = appointmentDAO.findByFilters(status, fromDate, toDate);
        } else {
            filteredAppointments = appointmentDAO.findAll();
        }

        // Navigate to appointment list view and pass the filtered data
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/StaffAppointmentListView.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the filtered data
            StaffAppointmentListController controller = loader.getController();
            controller.initUser(currentUser);
            controller.initData(filteredAppointments);

            // Set the new scene
            Stage stage = (Stage) userNameLabel.getScene().getWindow();

            // Get the current scene
            Scene scene = stage.getScene();

            // Instead of creating a new scene, just set the root of the existing scene
            // This helps maintain the state of the interface
            if (scene != null) {
                scene.setRoot(root);
            } else {
                // If there's no scene (unlikely), create a new one
                scene = new Scene(root);
                stage.setScene(scene);
            }
            stage.setTitle("Appointment List");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show error dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to open appointment list view: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void handleLogoClick(MouseEvent event) {
        try {
            // Load the staff dashboard view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/StaffDashboardView.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the current user
            StaffDashboardController controller = loader.getController();
            controller.initUser(currentUser);

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Get the current scene
            Scene scene = stage.getScene();

            // Instead of creating a new scene, just set the root of the existing scene
            // This helps maintain the state of the interface
            if (scene != null) {
                scene.setRoot(root);
            } else {
                // If there's no scene (unlikely), create a new one
                scene = new Scene(root);
                stage.setScene(scene);
            }

            stage.setTitle("Staff Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show error dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to open staff dashboard view: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
