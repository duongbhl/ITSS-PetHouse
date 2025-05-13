package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.petproject.service.UserService;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import org.example.petproject.model.User;

public class LoginController implements javafx.fxml.Initializable {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView logoView;

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputStream stream = LoginController.class.getResourceAsStream("/assets/logo.png");

        if (stream == null) {
            System.err.println("❌ Không tìm thấy logo ở đường dẫn /assets/logo.png");
            return;
        }

        Image image = new Image(stream);
        logoView.setImage(image);
    }

    @FXML
    private void handleLogin(ActionEvent evt) {
        String email = emailField.getText().trim();
        String pass = passwordField.getText();

        // gọi service, đối chiếu với bảng Users
        User user = userService.login(email, pass);
        if (user != null) {
            try {
                Parent root = FXMLLoader.load(
                        getClass().getResource("/org/example/petproject/dashboard.fxml"));
                Stage st = (Stage) ((Button) evt.getSource()).getScene().getWindow();
                st.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
                showError("Không thể mở Dashboard");
            }
        } else {
            showError("Email hoặc mật khẩu không đúng.");
        }
    }

    @FXML
    private void showRegister(ActionEvent evt) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/example/petproject/register.fxml"));
            Scene scene = ((Node) evt.getSource()).getScene();
            scene.setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Không thể mở màn Đăng ký");
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
