package org.example.petproject.controller.Dashboard;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.petproject.dao.ServiceBookingDAO;
import org.example.petproject.model.ServiceBooking;
import org.example.petproject.model.User;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StaffManageServiceController implements Initializable, DashboardControllerBase {
    @FXML
    private ImageView logoImageView;
    @FXML
    private Label userNameLabel;
    @FXML
    private ImageView userAvatarImageView;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private DatePicker fromDateDatePicker;
    @FXML
    private DatePicker toDateDatePicker;
    //    @FXML
    //    private VBox notificationContainer; // Not used in this flow yet

    private User currentUser;
    private final ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate status ComboBox
        List<String> statuses = Arrays.stream(ServiceBooking.Status.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        statusComboBox.setItems(FXCollections.observableArrayList(statuses));
        statusComboBox.getItems().add(0, "Tất cả"); // Option to select all statuses
        statusComboBox.getSelectionModel().selectFirst(); // Default to "Tất cả"
    }

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        if (userNameLabel != null) {
            userNameLabel.setText(user.getFullName());
        }
        
        // Load default avatar if user avatar is not available
        if (userAvatarImageView != null) {
            try {
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
                    userAvatarImageView.setImage(new javafx.scene.image.Image(user.getAvatarUrl()));
                } else {
                    // Load default avatar from resources
                    userAvatarImageView.setImage(new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/assets/icons/user.png")));
                }
            } catch (Exception e) {
                System.err.println("Error loading avatar: " + e.getMessage());
                // Load default avatar as fallback
                try {
                    userAvatarImageView.setImage(new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/assets/icons/user.png")));
                } catch (Exception ex) {
                    System.err.println("Error loading default avatar: " + ex.getMessage());
                }
            }
        }

        // Load logo
        if (logoImageView != null) {
            try {
                logoImageView.setImage(new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/assets/logo.png")));
            } catch (Exception e) {
                System.err.println("Error loading logo: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleFilterAction(ActionEvent actionEvent) {
        LocalDate fromDate = fromDateDatePicker.getValue();
        LocalDate toDate = toDateDatePicker.getValue();
        String selectedStatusString = statusComboBox.getSelectionModel().getSelectedItem();

        ServiceBooking.Status filterStatus = null;
        if (selectedStatusString != null && !selectedStatusString.equals("Tất cả")) {
            try {
                filterStatus = ServiceBooking.Status.valueOf(selectedStatusString);
            } catch (IllegalArgumentException e) {
                showAlert("Lỗi", "Trạng thái không hợp lệ.");
                return;
            }
        }

        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            showAlert("Lỗi", "Ngày bắt đầu không thể sau ngày kết thúc.");
            return;
        }

        List<ServiceBooking> filteredBookings = serviceBookingDAO.findBookingsByCriteria(fromDate, toDate, filterStatus);

        if (filteredBookings.isEmpty()) {
            showAlert("Thông báo", "Không tìm thấy đặt dịch vụ nào phù hợp với tiêu chí.");
            // Optionally clear the notificationContainer or display a message there
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/StaffManageServiceListView.fxml"));
            Parent root = loader.load();

            StaffManageServiceListController listController = loader.getController();
            listController.initUser(currentUser);
            listController.initData(filteredBookings);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                stage.setScene(scene);
            } else {
                scene.setRoot(root);
            }
            stage.setTitle("Danh sách đặt dịch vụ");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải màn hình danh sách đặt dịch vụ: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}