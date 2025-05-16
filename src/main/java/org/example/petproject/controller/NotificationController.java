package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.model.NotificationDto;
import org.example.petproject.model.User;
import org.example.petproject.service.NotificationService;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class NotificationController implements DashboardControllerBase, Initializable {

    @FXML
    private ImageView imgLogo;
    @FXML
    private Label lblUsername;
    @FXML
    private ImageView avatarImageView;
    @FXML
    private VBox notificationContainer;

    private User currentUser;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // nothing here, wait for initUser
    }

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        // Header
        lblUsername.setText(user.getFullName());
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
            avatarImageView.setImage(new Image(user.getAvatarUrl()));
        } else if (user.getAvatarUrl() == null) {
            var avatar = getClass().getResource("/assets/icons/user.png");
            if (avatar != null) {
                avatarImageView.setImage(new Image(avatar.toExternalForm()));
            }
        }
        // Logo
        Image logo = new Image(getClass().getResource("/assets/logo.png").toExternalForm());
        imgLogo.setImage(logo);
        // Load notifications
        loadAllNotifications();
    }

    @FXML
    private void handleLogoClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/OwnerDashboardView.fxml"));
            Parent root = loader.load();
            DashboardControllerBase ctrl = loader.getController();
            ctrl.initUser(currentUser);
            Scene scene = imgLogo.getScene();
            root.getStylesheets().setAll(scene.getStylesheets());
            scene.setRoot(root);
            Stage stage = (Stage) scene.getWindow();
            stage.setMaximized(true);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Không thể quay về Dashboard.").showAndWait();
            e.printStackTrace();
        }
    }

    private void loadAllNotifications() {
        notificationContainer.getChildren().clear();
        List<NotificationDto> list = NotificationService.getNotifications(currentUser.getUserId());
        if (list.isEmpty()) {
            Label empty = new Label("Chưa có thông báo nào.");
            empty.setStyle("-fx-font-style: italic; -fx-text-fill: #777;");
            notificationContainer.getChildren().add(empty);
        } else {
            for (NotificationDto n : list) {
                notificationContainer.getChildren().add(buildBox(n));
            }
        }
    }

    private HBox buildBox(NotificationDto n) {
        HBox box = new HBox(10);
        box.setStyle(
                "-fx-padding:8; -fx-border-color:#E0E0E0; -fx-border-radius:5; " +
                        "-fx-background-color:white; -fx-background-radius:5;");
        Label bell = new Label("🔔");
        bell.setStyle("-fx-text-fill: gold; -fx-font-size: 18;");
        VBox content = new VBox(2);
        Label line1 = new Label(n.getDateTime().format(dtf) + " – " + n.getPetName());
        line1.setStyle("-fx-font-weight:bold;");
        Label line2 = new Label(n.getMessage());
        content.getChildren().addAll(line1, line2);
        VBox.setMargin(content, new Insets(0, 0, 0, 4));
        box.getChildren().addAll(bell, content);
        return box;
    }
}