package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.petproject.model.NotificationDto;
import org.example.petproject.service.NotificationService;
import org.example.petproject.util.SessionManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationController {

    @FXML private Label lblUsername;
    @FXML private ImageView avatarImageView;
    @FXML private VBox notificationContainer;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        var user = SessionManager.getCurrentUser();
        if (user != null) {
            lblUsername.setText(user.getFullName());
        }

        loadAllNotifications();
    }

    private void loadAllNotifications() {
        notificationContainer.getChildren().clear();
        var user = SessionManager.getCurrentUser();
        if (user == null) {
            notificationContainer.getChildren().add(new Label("Chưa đăng nhập."));
            return;
        }

        List<NotificationDto> list =
                NotificationService.getNotifications(user.getUserId());

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
                "-fx-padding:8; " +
                        "-fx-border-color:#E0E0E0; -fx-border-radius:5; " +
                        "-fx-background-color:white; -fx-background-radius:5;"
        );

        // === Thay Label chuông thay cho ImageView ===
        Label bell = new Label("🔔");
        bell.setStyle("-fx-text-fill: gold; -fx-font-size: 18;");

        // Nội dung 2 dòng
        VBox content = new VBox(2);
        Label line1 = new Label(n.getDateTime().format(dtf)
                + " – " + n.getPetName());
        line1.setStyle("-fx-font-weight:bold;");
        Label line2 = new Label(n.getMessage());

        content.getChildren().addAll(line1, line2);
        HBox.setMargin(content, new Insets(0,0,0,4));

        box.getChildren().addAll(bell, content);
        return box;
    }
}
