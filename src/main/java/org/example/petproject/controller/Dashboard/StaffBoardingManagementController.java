package org.example.petproject.controller.Dashboard;

import javafx.event.ActionEvent;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.petproject.dao.PetBoardingInfoJPADAO;
import org.example.petproject.model.PetBoardingInfoJPA;
import org.example.petproject.model.User;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class StaffBoardingManagementController implements Initializable, DashboardControllerBase {

    @FXML private ImageView logoImageView;
    @FXML private Label userNameLabel;
    @FXML private ImageView userAvatarImageView;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private DatePicker fromDateDatePicker;
    @FXML private DatePicker toDateDatePicker;
    @FXML private VBox notificationContainer;

    private User currentUser;
    private PetBoardingInfoJPADAO petBoardingInfoJPADAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the DAO
        petBoardingInfoJPADAO = new PetBoardingInfoJPADAO();

        // Load logo image
        try {
            logoImageView.setImage(new Image(getClass().getResourceAsStream("/assets/logo.png")));
        } catch (Exception e) {
            System.err.println("Could not load logo image: " + e.getMessage());
        }

        // Populate status dropdown
        statusComboBox.getItems().addAll("", "pending", "confirmed", "in_progress", "completed", "cancelled");
        statusComboBox.setValue(""); // Empty value means "All statuses"

        // Set default date range to last week through next week
        fromDateDatePicker.setValue(LocalDate.now().minusWeeks(1));
        toDateDatePicker.setValue(LocalDate.now().plusWeeks(1));
    }

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        if (userNameLabel != null) {
            userNameLabel.setText(user.getFullName());
        }

        // Load user avatar if available
        if (userAvatarImageView != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
            try {
                userAvatarImageView.setImage(new Image(user.getAvatarUrl()));
            } catch (Exception e) {
                System.err.println("Could not load user avatar: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleFilterAction(ActionEvent event) {
        // Get filter criteria from UI
        String status = statusComboBox.getValue();
        LocalDate fromDate = fromDateDatePicker.getValue();
        LocalDate toDate = toDateDatePicker.getValue();

        // Validate date range
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            showAlert(Alert.AlertType.ERROR, "Date Error",
                    "The starting date must be before the ending date.");
            return;
        }

        try {
            // Get filtered pet boarding info from database
            List<PetBoardingInfoJPA> filteredInfoList;

            // Check if status is empty (means all statuses)
            if (status == null || status.isEmpty()) {
                // Filter by dates only
                filteredInfoList = petBoardingInfoJPADAO.findByFilters(null, fromDate, toDate);
            } else {
                // Filter by status and dates
                filteredInfoList = petBoardingInfoJPADAO.findByFilters(status, fromDate, toDate);
            }

            // Navigate to list view with filtered data
            navigateToListView(filteredInfoList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not retrieve boarding information: " + e.getMessage());
        }
    }

    private void navigateToListView(List<PetBoardingInfoJPA> filteredInfoList) {
        try {
            // Fix the path to match your project structure
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/petproject/controller/Dashboard/StaffBoardingListView.fxml"));

            // Try alternate paths if the above doesn't work
            if (loader.getLocation() == null) {
                loader = new FXMLLoader(getClass().getResource(
                        "/org/example/petproject/StaffBoardingListView.fxml"));
            }

            // Print debug info to verify path
            System.out.println("Loading from: " + loader.getLocation());

            // If location is null, path is wrong
            if (loader.getLocation() == null) {
                throw new IOException("Cannot find StaffBoardingListView.fxml - path is incorrect");
            }

            Parent root = loader.load();

            // Get controller and initialize with user and data
            StaffBoardingListController controller = loader.getController();
            controller.initUser(currentUser);
            controller.initData(filteredInfoList);

            // Set up the stage with the new scene
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
            stage.setTitle("Pet Boarding List");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not load the boarding list view: " + e.getMessage());
        }
    }

    @FXML
    public void handleLogoClick(MouseEvent event) {
        try {
            // Load the staff dashboard view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/StaffDashboardView.fxml"));
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                    "Could not load the staff dashboard view: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
