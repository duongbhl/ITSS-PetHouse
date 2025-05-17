package org.example.petproject.controller.Dashboard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.petproject.model.User;

import java.net.URL;
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
        userNameLabel.setText(user.getFullName());
        // Load avatar if available
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
            userAvatarImageView.setImage(new javafx.scene.image.Image(user.getAvatarUrl()));
        }
    }

    @FXML
    private void handleFilterButtonAction() {
        // Logic to filter appointments based on selected status and date range
        String status = statusComboBox.getValue();
        System.out.println("Filtering appointments with status: " + status);
    }
}