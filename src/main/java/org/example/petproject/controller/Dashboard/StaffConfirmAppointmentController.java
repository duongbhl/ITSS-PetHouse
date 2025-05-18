package org.example.petproject.controller.Dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.petproject.dao.AppointmentDAO;
import org.example.petproject.model.Appointment;
import org.example.petproject.model.User;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
        userNameLabel.setText(user.getFullName());
        // Load avatar if available
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
            userAvatarImageView.setImage(new javafx.scene.image.Image(user.getAvatarUrl()));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/StaffAppointmentListView.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the filtered data
            StaffAppointmentListViewController controller = loader.getController();
            controller.initUser(currentUser);
            controller.initData(filteredAppointments);

            // Set the new scene
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
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
}