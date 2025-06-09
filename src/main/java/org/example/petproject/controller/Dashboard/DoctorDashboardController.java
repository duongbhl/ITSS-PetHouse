package org.example.petproject.controller.Dashboard;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.example.petproject.controller.EditProfileController;
import org.example.petproject.controller.MedicalRecordController;
import org.example.petproject.dao.AppointmentDAO;
import org.example.petproject.dao.MedicalRecordDAO;
import org.example.petproject.model.Appointment;
import org.example.petproject.model.MedicalRecord;
import org.example.petproject.model.User;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class DoctorDashboardController implements DashboardControllerBase {

    @FXML
    private TableView<Appointment> appointmentsTableView;
    @FXML
    private TableColumn<Appointment, LocalDate> timeColumn;
    @FXML
    private TableColumn<Appointment, String> petNameColumn;
    @FXML
    private TableColumn<Appointment, String> ownerColumn;
    @FXML
    private TableColumn<Appointment, String> typeColumn;
    @FXML
    private TableColumn<Appointment, String> statusColumn;

    @FXML
    private DatePicker calendarDatePicker;
    @FXML
    private ImageView imgLogo;
    @FXML
    private Label lblUserName;
    @FXML
    private ImageView imgAvatar;
    @FXML
    private Button writeMedicalRecordButton;
    @FXML
    private Button btnProfile;
    @FXML
    private Button btnLogout;
    @FXML
    private ListView<MedicalRecord> recentMedicalRecordsListView;

    private User currentUser;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

    @FXML
    public void initialize() {
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        timeColumn.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatter.format(item));
            }
        });

        petNameColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getPet() != null ? cellData.getValue().getPet().getName() : ""));
        ownerColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
                () -> {
                    var pet = cellData.getValue().getPet();
                    return (pet != null && pet.getOwner() != null) ? pet.getOwner().getFullName() : "";
                }));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        calendarDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                loadAppointments(newVal);
        });

        writeMedicalRecordButton.setOnAction(e -> openMedicalRecordScreen());

        recentMedicalRecordsListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(MedicalRecord item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String petName = item.getPet() != null ? item.getPet().getName() : "";
                    String type = item.getType() != null ? item.getType() : "";
                    String date = item.getExamDate() != null ? item.getExamDate().toString() : "";
                    setText(petName + " - " + type + "\n" + date);
                }
            }
        });
    }

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        if (user == null)
            return;

        if (lblUserName != null)
            lblUserName.setText(user.getFullName());

        if (imgAvatar != null) {
            try {
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
                    imgAvatar.setImage(new Image(user.getAvatarUrl()));
                } else {
                    imgAvatar.setImage(new Image(getClass().getResourceAsStream("/assets/icons/user.png")));
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tải ảnh đại diện: " + e.getMessage());
                imgAvatar.setImage(new Image(getClass().getResourceAsStream("/assets/icons/user.png")));
            }
        }

        reloadDashboardData();
    }

    private void loadAppointments(LocalDate fromDate) {
        try {
            List<Appointment> appointments = appointmentDAO.findUpcomingByDoctorId(currentUser.getUserId(), fromDate);
            appointmentsTableView.setItems(FXCollections.observableArrayList(appointments));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRecentMedicalRecords() {
        if (currentUser == null)
            return;
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        List<MedicalRecord> records = medicalRecordDAO.findByDoctorIdAndDateRange(currentUser.getUserId(), weekAgo,
                today);
        recentMedicalRecordsListView.setItems(FXCollections.observableArrayList(records));
    }

    public void reloadDashboardData() {
        loadAppointments(calendarDatePicker.getValue() != null ? calendarDatePicker.getValue() : LocalDate.now());
        loadRecentMedicalRecords();
    }

    private void openMedicalRecordScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/MedicalRecordView.fxml"));
            Parent root = loader.load();
            MedicalRecordController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setSelectedDate(calendarDatePicker.getValue());
            controller.setDashboardController(this);

            Stage stage = new Stage();
            stage.setTitle("Write Medical Record");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> reloadDashboardData());
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Không thể mở màn hình viết hồ sơ: " + ex.getMessage());
        }
    }

    @FXML
    private void onProfile(javafx.scene.input.MouseEvent evt) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/EditUserProfileView.fxml"));
            Parent root = loader.load();
            EditProfileController controller = loader.getController();
            controller.setUser(currentUser);

            Stage stage = new Stage();
            stage.setTitle("Edit Profile");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            reloadDashboardData();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Không thể mở chỉnh sửa hồ sơ: " + ex.getMessage());
        }
    }

    @FXML
    private void onLogout(ActionEvent evt) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đăng xuất");
        alert.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");
        alert.setContentText("Nhấn OK để tiếp tục hoặc Cancel để hủy.");

        var result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/LoginView.fxml"));
                Parent root = loader.load();

                // Lấy scene cũ để giữ lại stylesheet
                Scene oldScene = ((Node) evt.getSource()).getScene();
                Scene scene = new Scene(root);
                scene.getStylesheets().setAll(oldScene.getStylesheets());

                Stage stage = (Stage) oldScene.getWindow();
                stage.setScene(scene);
                stage.centerOnScreen(); // để về giữa
            } catch (IOException ex) {
                ex.printStackTrace();
                showError("Lỗi khi đăng xuất: " + ex.getMessage());
            }
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
