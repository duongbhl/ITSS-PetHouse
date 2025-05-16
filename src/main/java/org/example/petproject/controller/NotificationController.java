package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.model.NotificationDto;
import org.example.petproject.service.NotificationService;
import org.example.petproject.util.SessionManager;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
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

    @FXML
    private void arrowpressedButton(javafx.event.ActionEvent evt) {
        String fxmlPath="/org/example/petproject/owner_dashboard.fxml";
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
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Lỗi");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
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
