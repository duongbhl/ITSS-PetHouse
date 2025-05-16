package org.example.petproject.controller.Dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.example.petproject.controller.UserProfileController;
import org.example.petproject.model.User;

public class OwnerDashboardController implements DashboardControllerBase, javafx.fxml.Initializable {

    // header
    @FXML
    private Label lblUserName;
    @FXML
    private ImageView imgAvatar;

    // các nút chức năng
    @FXML
    private javafx.scene.control.Button btnMyPets;
    @FXML
    private javafx.scene.control.Button btnBook;
    @FXML
    private javafx.scene.control.Button btnHistory;
    @FXML
    private javafx.scene.control.Button btnGrooming;
    @FXML
    private javafx.scene.control.Button btnBoarding;
    @FXML
    private javafx.scene.control.Button btnNotify;

    @FXML
    private ImageView iconMyPets;

    @FXML
    private ImageView iconBook;

    @FXML
    private ImageView iconHistory;

    @FXML
    private ImageView iconGrooming;

    @FXML
    private ImageView iconBoarding;

    @FXML
    private ImageView iconNotify;
    @FXML
    private ImageView imgLogo;

    private Image loadIcon(String path) {
        URL url = getClass().getResource(path);
        if (url == null) {
            System.err.println("Missing resource: " + path);
            return null;
        }
        return new Image(url.toExternalForm());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Avatar từ user sẽ đổ trong initUser()
        var logoUrl = getClass().getResource("/assets/logo.png");
        if (logoUrl != null) {
            imgLogo.setImage(new Image(logoUrl.toExternalForm()));
        }

        imgAvatar.setCursor(javafx.scene.Cursor.HAND);
        imgAvatar.setOnMouseClicked(e -> showUserMenu());

        // Load các icon dashboard
        iconMyPets.setImage(loadIcon("/assets/icons/pets.png"));

        iconBook.setImage(loadIcon("/assets/icons/clinic.png"));

        iconHistory.setImage(loadIcon("/assets/icons/history.png"));

        iconGrooming.setImage(loadIcon("/assets/icons/grooming.png"));

        iconBoarding.setImage(loadIcon("/assets/icons/boarding.png"));

        iconNotify.setImage(loadIcon("/assets/icons/notification.png"));

    }

    private User currentUser;

    /**
     * Được gọi sau khi FXML load xong, từ LoginController.
     */
    @Override
    public void initUser(User user) {
        this.currentUser = user;
        // hiển thị tên + avatar
        lblUserName.setText(user.getFullName());
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
            imgAvatar.setImage(new Image(user.getAvatarUrl()));
        } else if (user.getAvatarUrl() == null) {
            var avatar = getClass().getResource("/assets/icons/user.png");
            if (avatar != null) {
                imgAvatar.setImage(new Image(avatar.toExternalForm()));
            }
        }
    }

    @FXML
    private void onMyPets(ActionEvent evt) {
        navigateTo("/org/example/petproject/OwnerPetsView.fxml", evt);
    }

    @FXML
    private void onBook(ActionEvent evt) {
        navigateTo("/org/example/petproject/MakeAppointmentView.fxml", evt);
    }

    @FXML
    private void onHistory(ActionEvent evt) {
        navigateTo("/org/example/petproject/MedicalHistoryView.fxml", evt);
    }

    @FXML
    private void onGrooming(ActionEvent evt) {
        navigateTo("/org/example/petproject/OwnerGroomingView.fxml", evt);
    }

    @FXML
    private void onBoarding(ActionEvent evt) {
        navigateTo("/org/example/petproject/BoardingView.fxml", evt);
    }

    @FXML
    private void onNotify(ActionEvent evt) {
        navigateTo("/org/example/petproject/NotificationView.fxml", evt);
    }

    /**
     * Hàm chung để chuyển scene con.
     */
    private void navigateTo(String fxmlPath, ActionEvent evt) {
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
                dcb.initUser(currentUser);
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
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("Lỗi");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showUserMenu() {
        MenuItem infoItem = new MenuItem("Thông tin tài khoản");
        infoItem.setOnAction(e -> openUserProfile());

        MenuItem logoutItem = new MenuItem("Đăng xuất");
        logoutItem.setOnAction(e -> performLogout());

        ContextMenu menu = new ContextMenu(infoItem, logoutItem);
        menu.show(imgAvatar, Side.BOTTOM, 0, 0);
    }

    private void openUserProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/UserProfileView.fxml"));
            Parent root = loader.load();
            UserProfileController ctrl = loader.getController();
            ctrl.setUser(currentUser);

            // Lấy styles cũ
            Scene oldScene = imgAvatar.getScene();
            Scene newScene = new Scene(root);
            newScene.getStylesheets().setAll(oldScene.getStylesheets());

            Stage stage = new Stage();
            stage.setTitle("Hồ sơ cá nhân");
            stage.setScene(newScene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể mở hồ sơ cá nhân.");
        }
    }

    private void performLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/LoginView.fxml"));
            Parent root = loader.load();

            // Lấy stylesheets của scene hiện tại
            Scene oldScene = imgAvatar.getScene();

            // Khởi tạo scene mới cho login và gán stylesheets
            Scene loginScene = new Scene(root);
            loginScene.getStylesheets().setAll(oldScene.getStylesheets());

            // Đặt lên stage
            Stage stage = (Stage) imgAvatar.getScene().getWindow();
            stage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể đăng xuất.");
        }
    }

}