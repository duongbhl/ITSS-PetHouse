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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import org.example.petproject.controller.StaffBoardingManagementController;
import org.example.petproject.controller.StaffConfirmAppointmentController;
import org.example.petproject.controller.StaffManageServiceController;
import org.example.petproject.controller.UserProfileController;
import org.example.petproject.model.User;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.Cursor;
import javafx.stage.Modality;

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
    public void handleConfirmSchedule(ActionEvent actionEvent) {
        if (currentUser == null) {
            showError("User data not available. Cannot proceed.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/StaffConfirmAppointmentView.fxml"));
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
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/StaffManageServiceView.fxml"));
            Parent root = loader.load();

            // Assuming StaffManageServiceController implements DashboardControllerBase or
            // has initUser
            // If StaffManageServiceController is not DashboardControllerBase but has
            // initUser, cast appropriately
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
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/StaffBoardingManagementView.fxml"));
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

    @SuppressWarnings("unused")
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up context menu for user avatar
        if (userAvatarImageView != null) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem profileItem = new MenuItem("Thông tin tài khoản");
            MenuItem logoutItem = new MenuItem("Đăng xuất");

            profileItem.setOnAction(this::handleShowProfile);
            logoutItem.setOnAction(this::handleLogout);

            contextMenu.getItems().addAll(profileItem, logoutItem);

            userAvatarImageView.setCursor(Cursor.HAND);
            userAvatarImageView.setOnMouseClicked(event -> {
                contextMenu.show(userAvatarImageView, event.getScreenX(), event.getScreenY());
            });
        }
    }

    /**
     * Shows the user profile view when "Account Information" is selected
     */
    private void handleShowProfile(ActionEvent event) {
        if (currentUser == null) {
            showError("User data not available. Cannot proceed.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/UserProfileView.fxml"));
            Parent root = loader.load();

            UserProfileController controller = loader.getController();
            controller.setUser(currentUser);

            Stage stage = new Stage();
            stage.initOwner(userAvatarImageView.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("User Profile");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load profile view: " + e.getMessage());
        }
    }

    /**
     * Logs out the user and returns to login screen when "Log Out" is selected
     */
    private void handleLogout(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận đăng xuất");
        confirm.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");
        confirm.setContentText("Phiên làm việc hiện tại sẽ kết thúc.");

        ButtonType yes = new ButtonType("Đăng xuất");
        ButtonType no = new ButtonType("Huỷ", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(yes, no);

        confirm.showAndWait().ifPresent(type -> {
            if (type == yes) {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/org/example/petproject/LoginView.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) userAvatarImageView.getScene().getWindow();
                    Scene loginScene = new Scene(root);
                    loginScene.getStylesheets().setAll(stage.getScene().getStylesheets());

                    stage.setScene(loginScene);

                    // ✅ Không fullscreen
                    stage.setMaximized(false);

                    // ✅ Resize và canh giữa
                    stage.setWidth(800);
                    stage.setHeight(600);
                    stage.centerOnScreen();

                    stage.setTitle("Pet House");
                } catch (IOException e) {
                    e.printStackTrace();
                    showError("Không thể tải màn hình đăng nhập: " + e.getMessage());
                }
            }
        });
    }

}
