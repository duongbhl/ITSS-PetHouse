package org.example.petproject.controller.Dashboard;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.petproject.dao.ServiceBookingDAO;
import org.example.petproject.model.ServiceBooking;
import org.example.petproject.model.User;

import java.io.IOException;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class StaffManageServiceListController implements Initializable, DashboardControllerBase {

    @FXML
    private Label userNameLabel;
    @FXML
    private ImageView logoImageView;
    @FXML
    private ImageView userAvatarImageView;

    @FXML
    private TableView<ServiceBookingWrapper> serviceTableView;
    @FXML
    private TableColumn<ServiceBookingWrapper, Boolean> selectColumn;
    @FXML
    private TableColumn<ServiceBookingWrapper, String> checkInColumn;
    @FXML
    private TableColumn<ServiceBookingWrapper, String> petNameColumn;
    @FXML
    private TableColumn<ServiceBookingWrapper, String> serviceColumn;
    @FXML
    private TableColumn<ServiceBookingWrapper, String> staffColumn;
    @FXML
    private TableColumn<ServiceBookingWrapper, String> statusColumn;
    @FXML
    private TableColumn<ServiceBookingWrapper, Button> actionColumn;

    @FXML
    private CheckBox selectAllCheckBox;
    @FXML
    private Button confirmButton;
    @FXML
    private Button rejectButton;

    private User currentUser;
    private ObservableList<ServiceBookingWrapper> bookings = FXCollections.observableArrayList();
    private ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Make table editable
        serviceTableView.setEditable(true);

        // Configure the select all checkbox
        selectAllCheckBox.setOnAction(e -> {
            boolean selected = selectAllCheckBox.isSelected();
            bookings.forEach(b -> b.setSelected(selected));
            serviceTableView.refresh();
        });

        // Configure columns
        setupTableColumns();

        // Bind table data
        serviceTableView.setItems(bookings);
    }

    private void setupTableColumns() {
        // Select column with checkbox
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));

        // Check-in date column
        checkInColumn.setCellValueFactory(cellData -> cellData.getValue().checkInProperty());

        // Pet name column
        petNameColumn.setCellValueFactory(cellData -> cellData.getValue().petNameProperty());

        // Service column
        serviceColumn.setCellValueFactory(cellData -> cellData.getValue().serviceNameProperty());

        // Staff column
        staffColumn.setCellValueFactory(cellData -> cellData.getValue().staffNameProperty());

        // Status column
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Action column with buttons
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Xem");

            {
                btn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                btn.setOnAction(event -> {
                    ServiceBookingWrapper booking = getTableView().getItems().get(getIndex());
                    // View details action
                    showBookingDetails(booking.getBooking());
                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        if (userNameLabel != null) {
            userNameLabel.setText(user.getFullName());
        }

        // Load avatar if available
        if (userAvatarImageView != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
            userAvatarImageView.setImage(new javafx.scene.image.Image(user.getAvatarUrl()));
        }
    }

    public void initData(List<ServiceBooking> serviceBookings) {
        // Clear existing items
        bookings.clear();

        // Convert ServiceBooking objects to wrapper objects and add to the list
        if (serviceBookings != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
            serviceBookings.forEach(booking -> {
                ServiceBookingWrapper wrapper = new ServiceBookingWrapper(booking);
                bookings.add(wrapper);
            });
        }

        // Refresh the table view
        serviceTableView.refresh();
    }

    @FXML
    public void handleConfirmAction(ActionEvent actionEvent) {
        List<ServiceBookingWrapper> selectedBookings = bookings.stream()
                .filter(ServiceBookingWrapper::isSelected)
                .collect(Collectors.toList());

        if (selectedBookings.isEmpty()) {
            showAlert("Lưu ý", "Vui lòng chọn ít nhất một dịch vụ để xác nhận.");
            return;
        }

        try {
            for (ServiceBookingWrapper wrapper : selectedBookings) {
                ServiceBooking booking = wrapper.getBooking();
                booking.setStatus(ServiceBooking.Status.in_progress);
                booking.setHandledBy(currentUser);
                serviceBookingDAO.update(booking);
            }

            showAlert("Thành công", "Đã xác nhận " + selectedBookings.size() + " dịch vụ.");

            // Update status in the table
            selectedBookings.forEach(wrapper -> {
                wrapper.setStatus("in_progress");
                wrapper.setStaffName(currentUser.getFullName());
                wrapper.setSelected(false);
            });

            selectAllCheckBox.setSelected(false);
            serviceTableView.refresh();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể xác nhận dịch vụ: " + e.getMessage());
        }
    }

    @FXML
    public void handleRejectAction(ActionEvent actionEvent) {
        // No database change required as per requirements, just show a message
        showAlert("Thông báo", "Không có thay đổi nào được thực hiện.");
    }

    private void showBookingDetails(ServiceBooking booking) {
        // This would open a dialog or navigate to a details view
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(booking.getBookingId()).append("\n");
        details.append("Thú cưng: ").append(booking.getPet().getName()).append("\n");
        details.append("Dịch vụ: ").append(booking.getService().getName()).append("\n");
        details.append("Ngày check-in: ").append(booking.getCheckInTime()).append("\n");
        if (booking.getCheckOutTime() != null) {
            details.append("Ngày check-out: ").append(booking.getCheckOutTime()).append("\n");
        }
        details.append("Trạng thái: ").append(booking.getStatus()).append("\n");
        if (booking.getNote() != null && !booking.getNote().isEmpty()) {
            details.append("Ghi chú: ").append(booking.getNote()).append("\n");
        }

        showAlert("Chi tiết đặt dịch vụ", details.toString());
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleLogoClick(MouseEvent event) {
        try {
            // Load the staff dashboard view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/StaffDashboardView.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the current user
            StaffDashboardController controller = loader.getController();
            controller.initUser(currentUser);

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Get the current scene
            Scene scene = stage.getScene();

            // Instead of creating a new scene, just set the root of the existing scene
            // This helps maintain the state of the interface
            if (scene != null) {
                scene.setRoot(root);
            } else {
                // If there's no scene (unlikely), create a new one
                scene = new Scene(root);
                stage.setScene(scene);
            }

            stage.setTitle("Staff Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the staff dashboard view: " + e.getMessage());
        }
    }

    // Wrapper class to handle table view bindings and selection states
    public static class ServiceBookingWrapper {
        private final ServiceBooking booking;
        private final BooleanProperty selected = new SimpleBooleanProperty(false);

        public ServiceBookingWrapper(ServiceBooking booking) {
            this.booking = booking;
        }

        public ServiceBooking getBooking() {
            return booking;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        public String getCheckIn() {
            if (booking.getCheckInTime() == null)
                return "";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
            return booking.getCheckInTime().format(formatter);
        }

        public javafx.beans.property.StringProperty checkInProperty() {
            return new javafx.beans.property.SimpleStringProperty(getCheckIn());
        }

        public String getPetName() {
            return booking.getPet() != null ? booking.getPet().getName() : "";
        }

        public javafx.beans.property.StringProperty petNameProperty() {
            return new javafx.beans.property.SimpleStringProperty(getPetName());
        }

        public String getServiceName() {
            return booking.getService() != null ? booking.getService().getName() : "";
        }

        public javafx.beans.property.StringProperty serviceNameProperty() {
            return new javafx.beans.property.SimpleStringProperty(getServiceName());
        }

        public String getStaffName() {
            return booking.getHandledBy() != null ? booking.getHandledBy().getFullName() : "";
        }

        public void setStaffName(String name) {
            // This doesn't change the actual staff name, just updates the UI
        }

        public javafx.beans.property.StringProperty staffNameProperty() {
            return new javafx.beans.property.SimpleStringProperty(getStaffName());
        }

        public String getStatus() {
            return booking.getStatus().toString();
        }

        public void setStatus(String status) {
            // This doesn't change the actual status, just updates the UI
        }

        public javafx.beans.property.StringProperty statusProperty() {
            return new javafx.beans.property.SimpleStringProperty(getStatus());
        }
    }
}
