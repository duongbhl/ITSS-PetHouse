package org.example.petproject.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.model.User;
import org.example.petproject.service.UserService;
import org.example.petproject.util.SessionManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LoginController implements Initializable {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ImageView logoView;
    @FXML
    private Button loginButton;
    @FXML
    private ProgressIndicator loader;

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load logo
        InputStream stream = getClass().getResourceAsStream("/assets/logo.png");
        if (stream != null) {
            logoView.setImage(new Image(stream));
        }
    }

    @FXML
    private void handleLogin(ActionEvent evt) {
        String email = emailField.getText().trim();
        String pass = passwordField.getText();

        // 1) Validate
        if (email.isEmpty() || pass.isEmpty()) {
            showError("Vui lòng điền cả Email và Mật khẩu.");
            return;
        }
        if (!ValidationUtil.isEmailValid(email)) {
            showError("Định dạng Email không hợp lệ.");
            return;
        }

        // 2) UI feedback
        loginButton.setDisable(true);
        loader.setVisible(true);

        // 3) Thực thi login trên background thread
        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() {
                return userService.login(email, pass);
            }
        };

        loginTask.setOnSucceeded(e -> {
            loader.setVisible(false);
            loginButton.setDisable(false);

            User user = loginTask.getValue();
            if (user == null) {
                showError("Email hoặc mật khẩu không đúng.");
            } else {
                SessionManager.setCurrentUser(user);
                openDashboardFor(user, evt);
            }
        });

        loginTask.setOnFailed(e -> {
            loader.setVisible(false);
            loginButton.setDisable(false);

            // Lấy exception gốc
            Throwable ex = loginTask.getException();
            ex.printStackTrace(); // In stacktrace ra console
            // Hiện dialog với chi tiết lỗi
            showError("Lỗi kết nối cơ sở dữ liệu:\n" + ex.getMessage());
        });

        new Thread(loginTask).start();
    }

    /**
     * Phân luồng và load dashboard dựa trên role, gọi initUser().
     */
    private void openDashboardFor(User user, ActionEvent evt) {
        String fxmlPath;
        switch (user.getRole()) {
            case owner:
                fxmlPath = "/org/example/petproject/OwnerDashboardView.fxml";
                break;
            case doctor:
                fxmlPath = "/org/example/petproject/DoctorDashboardView.fxml";
                break;
            case staff:
                fxmlPath = "/org/example/petproject/StaffDashboardView.fxml";
                break;
            case admin:
                fxmlPath = "/org/example/petproject/AdminDashboardView.fxml";
                break;
            default:
                showError("Role không hợp lệ.");
                return;
        }

        try {
            System.out.println("=== DEBUG DASHBOARD LOADING ===");
            System.out.println("User role: " + user.getRole());
            System.out.println("User name: " + user.getFullName());
            System.out.println("FXML path: " + fxmlPath);
            
            URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                throw new IOException("Cannot find FXML file at path: " + fxmlPath);
            }
            System.out.println("FXML resource URL: " + resourceUrl);
            
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            System.out.println("Created FXMLLoader");
            
            Parent newRoot = loader.load();
            System.out.println("Loaded FXML");

            DashboardControllerBase dc = loader.getController();
            if (dc == null) {
                throw new IOException("Controller is null for: " + fxmlPath);
            }
            System.out.println("Got controller: " + dc.getClass().getName());
            
            dc.initUser(user);
            System.out.println("Initialized user in controller");

            Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setMaximized(true);
            System.out.println("Successfully set new scene");
            
        } catch (IOException ex) {
            System.err.println("=== ERROR LOADING DASHBOARD ===");
            System.err.println("Error message: " + ex.getMessage());
            ex.printStackTrace();
            showError("Không thể mở Dashboard: " + ex.getMessage());
        }
    }


    @FXML
    private void showRegister(ActionEvent evt) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/example/petproject/RegisterView.fxml"));
            ((Node) evt.getSource()).getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể mở màn Đăng ký.");
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // Helper validate email
    public static class ValidationUtil {
        private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        public static boolean isEmailValid(String email) {
            return email != null && EMAIL_PATTERN.matcher(email).matches();
        }
    }
}