package org.example.petproject.controller.Dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.petproject.model.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StaffDashboardController implements Initializable, DashboardControllerBase {

    @FXML
    private Label userNameLabel; // Assuming you have this in StaffDashboardView.fxml
    @FXML
    private ImageView userAvatarImageView; // Assuming you have this
    @FXML
    private ImageView logoImageView; // Assuming you have this
    @FXML
    private Button confirmScheduleButton; // Ensure this fx:id exists in StaffDashboardView.fxml
    @FXML // <-- Add this annotation
    private Button manageBeautyServicesButton; // Ensure this fx:id is in StaffDashboardView.fxml
    @FXML
    private Button manageBoardingButton; // Assuming this button also needs an FXML link if it has an onAction
    private User currentUser;

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        if (userNameLabel != null) {
            userNameLabel.setText(user.getFullName());
        }
        // Optionally load avatar and logo if they exist in this view
        if (userAvatarImageView != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
            userAvatarImageView.setImage(new javafx.scene.image.Image(user.getAvatarUrl()));
        }
        // Example for logo, adjust path as needed
        // if (logoImageView != null) {
        //     logoImageView.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/assets/logo.png")));
        // }
    }

    @FXML
    public void handleConfirmSchedule(ActionEvent actionEvent) {
        if (currentUser == null) {
            showError("User data not available. Cannot proceed.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/StaffConfirmAppointmentView.fxml"));
            Parent root = loader.load();

            StaffConfirmAppointmentController controller = loader.getController();
            controller.initUser(currentUser); // Pass the current user

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                stage.setScene(scene);
            } else {
                scene.setRoot(root);
            }
            stage.setTitle("Confirm Appointments"); // Optional: Set a title for the new view
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load the confirm appointment view: " + e.getMessage());
        }
    }

    @FXML
    public void handleManageBeautyServices(ActionEvent actionEvent) {
        if (currentUser == null) {
            showError("User data not available. Cannot proceed.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/StaffManageServiceView.fxml"));
            Parent root = loader.load();

            // Assuming StaffManageServiceController implements DashboardControllerBase or has initUser
            // If StaffManageServiceController is not DashboardControllerBase but has initUser, cast appropriately
            // e.g., StaffManageServiceController controller = loader.getController();
            StaffManageServiceController controller = loader.getController();
            controller.initUser(currentUser); // Pass the current user

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                stage.setScene(scene);
            } else {
                scene.setRoot(root);
            }
            stage.setTitle("Manage Beauty Services"); // Optional: Set a title for the new view
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load the manage beauty services view: " + e.getMessage());
        }
    }

    @FXML
    public void handleManageBoarding(ActionEvent actionEvent) {
        if (currentUser == null) {
            showError("User data not available. Cannot proceed.");
            return;
        }
        try {
            // Load the FXML file for the boarding management view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/StaffBoardingManagementView.fxml"));
            Parent root = loader.load();

            // Get the controller for the boarding management view
            StaffBoardingManagementController controller = loader.getController();
            // Pass the current user to the new controller
            controller.initUser(currentUser);

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                stage.setScene(scene);
            } else {
                scene.setRoot(root); // Or create new Scene(root) if you prefer
            }
            stage.setTitle("Pet Boarding Management"); // Optional: Set a title for the new view
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load the boarding management view: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
