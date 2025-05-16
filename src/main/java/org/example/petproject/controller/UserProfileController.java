package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import org.example.petproject.model.User;
import org.example.petproject.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {

    @FXML
    private ImageView ivAvatar;
    @FXML
    private Label tfFullName; // phải là Label, không phải TextField
    @FXML
    private Label lblRole;
    @FXML
    private Label tfEmail; // cũng là Label
    @FXML
    private Label tfPhone; // cũng là Label

    @FXML
    private Button btnEdit;
    @FXML
    private Button btnChangePwd;

    private User currentUser;
    private final UserService userService = new UserService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnEdit.setOnAction(e -> onEdit());
        btnChangePwd.setOnAction(e -> onChangePassword());
    }

    /** Phải được gọi ngay sau khi load FXML để đổ dữ liệu vào form */
    public void setUser(User user) {
        this.currentUser = user;
        loadUser();
    }

    /** Đổ dữ liệu User vào các Label */
    private void loadUser() {
        // Avatar
        if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isBlank()) {
            ivAvatar.setImage(new Image(currentUser.getAvatarUrl()));
        } else {
            InputStream placeholder = getClass().getResourceAsStream("/assets/icons/user.png");
            if (placeholder != null)
                ivAvatar.setImage(new Image(placeholder));
        }
        // Thông tin
        tfFullName.setText(currentUser.getFullName());
        lblRole.setText(currentUser.getRole().name());
        tfEmail.setText(currentUser.getEmail());
        tfPhone.setText(currentUser.getPhone());
    }

    /** Xử lý khi nhấn “Chỉnh sửa thông tin” */
    private void onEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/EditUserProfileView.fxml"));
            Parent root = loader.load();
            EditProfileController ctrl = loader.getController();
            ctrl.setUser(currentUser);

            // giữ nguyên CSS
            Scene oldScene = ivAvatar.getScene();
            Scene newScene = new Scene(root);
            newScene.getStylesheets().setAll(oldScene.getStylesheets());

            Stage dialog = new Stage();
            dialog.initOwner(ivAvatar.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Chỉnh sửa hồ sơ");
            dialog.setScene(newScene);
            dialog.showAndWait();

            // sau khi đóng, load lại
            loadUser();
        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không mở được form chỉnh sửa.").showAndWait();
        }
    }

    /** Xử lý khi nhấn “Đổi mật khẩu” */
    private void onChangePassword() {
        Dialog<Pair<String, String>> dlg = new Dialog<>();
        dlg.setTitle("Đổi mật khẩu");
        // buttons
        dlg.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        // form
        PasswordField oldPf = new PasswordField();
        PasswordField newPf = new PasswordField();
        GridPane g = new GridPane();
        g.setVgap(10);
        g.setHgap(10);
        g.addRow(0, new Label("Mật khẩu cũ:"), oldPf);
        g.addRow(1, new Label("Mật khẩu mới:"), newPf);
        dlg.getDialogPane().setContent(g);

        // result converter
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK)
                return new Pair<>(oldPf.getText(), newPf.getText());
            return null;
        });

        dlg.showAndWait().ifPresent(pair -> {
            String oldPwd = pair.getKey(), newPwd = pair.getValue();
            boolean ok = userService.changePassword(
                    currentUser.getUserId(), oldPwd, newPwd);
            if (ok) {
                new Alert(Alert.AlertType.INFORMATION,
                        "Đổi mật khẩu thành công!").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Mật khẩu cũ không đúng.").showAndWait();
            }
        });
    }

}