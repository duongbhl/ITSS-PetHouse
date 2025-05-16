package org.example.petproject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.petproject.model.User;
import org.example.petproject.model.User.Role;
import org.example.petproject.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements javafx.fxml.Initializable {

    @FXML
    private TextField emailField;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Hyperlink loginLink;
    @FXML
    private ImageView logoView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputStream stream = RegisterController.class.getResourceAsStream("/assets/logo.png");

        if (stream == null) {
            System.err.println("❌ Không tìm thấy logo ở đường dẫn /assets/logo.png");
            return;
        }

        Image image = new Image(stream);
        logoView.setImage(image);
    }

    private final UserService userService = new UserService();

    @FXML
    private void handleRegister(ActionEvent event) {
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String pw = passwordField.getText();
        String pw2 = confirmPasswordField.getText();

        // 1. Validate cơ bản
        if (email.isEmpty() || fullName.isEmpty() || phone.isEmpty() || pw.isEmpty() || pw2.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Chưa điền đủ",
                    "Vui lòng nhập đầy đủ email, họ tên, điện thoại và mật khẩu.");
            return;
        }
        if (!pw.equals(pw2)) {
            showAlert(Alert.AlertType.WARNING, "Sai xác nhận mật khẩu",
                    "Mật khẩu nhập lại không khớp.");
            return;
        }

        // 2. Khởi tạo User, gán role mặc định = "owner"
        try {
            User u = new User();
            u.setEmail(email);
            u.setFullName(fullName);
            u.setPhone(phone);
            u.setPassword(pw);
            u.setRole(Role.owner); // mặc định là chủ nuôi

            userService.register(u);

            showAlert(Alert.AlertType.INFORMATION, "Thành công",
                    "Đăng ký thành công! Vui lòng đăng nhập.");
            // Chuyển về Login
            showLogin(event);

        } catch (RuntimeException ex) {
            showAlert(Alert.AlertType.ERROR, "Lỗi đăng ký", ex.getMessage());
        }
    }

    @FXML
    private void showLogin(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(
                    getClass().getResource("/org/example/petproject/LoginView.fxml"));
            Scene scene = ((Node) event.getSource()).getScene(); // lấy Scene hiện tại
            scene.setRoot(loginRoot); // chỉ thay root
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở màn Đăng nhập.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}