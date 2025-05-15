package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.example.petproject.model.User;
import org.example.petproject.service.UserService;
import org.example.petproject.utils.FileStorageUtil;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class EditProfileController implements Initializable {

    @FXML
    private ImageView ivAvatar;
    @FXML
    private TextField tfFullName;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfPhone;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;

    private User currentUser;
    private File avatarFile;
    private final UserService userService = new UserService();

    /** Phải được gọi ngay sau khi load FXML */
    public void setUser(User user) {
        this.currentUser = user;
        loadUser();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancel.setOnAction(e -> onCancel());
        btnSave.setOnAction(e -> onSave());
    }

    private void loadUser() {
        if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isBlank()) {
            ivAvatar.setImage(new Image(currentUser.getAvatarUrl()));
        } else {
            InputStream placeholder = getClass()
                    .getResourceAsStream("/assets/icons/user.png");
            if (placeholder != null)
                ivAvatar.setImage(new Image(placeholder));
        }
        tfFullName.setText(currentUser.getFullName());
        tfEmail.setText(currentUser.getEmail());
        tfPhone.setText(currentUser.getPhone());
    }

    @FXML
    private void onCancel() {
        avatarFile = null;
        loadUser();
    }

    @FXML
    private void onSave() {
        // 1. Validate
        if (tfFullName.getText().trim().isEmpty()
                || tfEmail.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                    "Họ tên và Email không được để trống.")
                    .showAndWait();
            return;
        }
        // 2. Upload avatar nếu có
        if (avatarFile != null) {
            try {
                String url = FileStorageUtil.storeImage(avatarFile);
                currentUser.setAvatarUrl(url);
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR,
                        "Lưu ảnh thất bại.")
                        .showAndWait();
                return;
            }
        }
        // 3. Cập nhật thông tin
        currentUser.setFullName(tfFullName.getText().trim());
        currentUser.setEmail(tfEmail.getText().trim());
        currentUser.setPhone(tfPhone.getText().trim());
        // 4. Gọi service lưu vào DB
        userService.update(currentUser);
        // 5. Đóng dialog
        ((Window) btnSave.getScene().getWindow()).hide();
    }

    @FXML
    private void onUploadAvatar() {
        Window w = ivAvatar.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn ảnh đại diện");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files", "*.png", "*.jpg", "*.jpeg"));
        File f = chooser.showOpenDialog(w);
        if (f != null) {
            avatarFile = f;
            ivAvatar.setImage(new Image(f.toURI().toString()));
        }
    }
}
