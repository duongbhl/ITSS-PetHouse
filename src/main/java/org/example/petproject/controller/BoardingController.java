package org.example.petproject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.dao.UserDAO;
import org.example.petproject.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class BoardingController {
    private static final Logger LOGGER = Logger.getLogger(BoardingController.class.getName());

    private String ownerId;

    @FXML
    private ImageView imgLogo;

    @FXML
    private Label ownerName;

    @FXML
    public void initialize() {
        if (!SessionManager.isLoggedIn()) {
            LOGGER.warning("No active session found");
            navigateToLogin();
            return;
        }

        ownerId = SessionManager.getCurrentUser().getUserId();
        ownerName.setText(SessionManager.getCurrentUser().getFullName());

        try {
            imgLogo.setImage(new Image(getClass().getResource("/assets/logo.png").toExternalForm()));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load logo", e);
        }
    }

    @FXML
    private void handleLogoClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/OwnerDashboardView.fxml"));
            Parent root = loader.load();
            DashboardControllerBase ctrl = loader.getController();
            ctrl.initUser(SessionManager.getCurrentUser());
            Scene scene = imgLogo.getScene();
            root.getStylesheets().setAll(scene.getStylesheets());
            scene.setRoot(root);
            Stage stage = (Stage) scene.getWindow();
            stage.setMaximized(true);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating to dashboard", e);
            showError("Không thể chuyển về trang chủ");
        }
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) imgLogo.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error navigating to login", ex);
            showError("Không thể chuyển về màn đăng nhập.");
        }
    }

    @FXML
    void roomlistButton(ActionEvent evt) {
        String fxmlPath = "/org/example/petproject/RoomListView.fxml";
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if (fxmlUrl == null) {
            showError("Không tìm thấy màn hình " + fxmlPath);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent newRoot = loader.load();

            // initUser nếu cần
            Object ctrl = loader.getController();
            if (ctrl instanceof DashboardControllerBase dcb) {
                dcb.initUser(SessionManager.getCurrentUser());
            }

            // Lấy Scene hiện tại từ bất kỳ node nào (ví dụ button)
            Scene scene = ((Node) evt.getSource()).getScene();
            // Chuyển root thành root mới
            scene.setRoot(newRoot);

            // Nếu muốn, vẫn có thể maximize stage
            Stage stage = (Stage) scene.getWindow();
            stage.setMaximized(true);

        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Lỗi khi mở màn hình: " + fxmlPath);
        }

    }

    @FXML
    void petlistButton(ActionEvent evt) {
        String fxmlPath = "/org/example/petproject/BoardingListView.fxml";
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if (fxmlUrl == null) {
            showError("Không tìm thấy màn hình " + fxmlPath);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent newRoot = loader.load();

            // initUser nếu cần
            Object ctrl = loader.getController();
            if (ctrl instanceof DashboardControllerBase dcb) {
                dcb.initUser(SessionManager.getCurrentUser());
            }

            // Lấy Scene hiện tại từ bất kỳ node nào (ví dụ button)
            Scene scene = ((Node) evt.getSource()).getScene();
            // Chuyển root thành root mới
            scene.setRoot(newRoot);

            // Nếu muốn, vẫn có thể maximize stage
            Stage stage = (Stage) scene.getWindow();
            stage.setMaximized(true);

        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Lỗi khi mở màn hình: " + fxmlPath);
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}