package org.example.petproject.controller.Dashboard;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.example.petproject.dao.AppointmentDAO;
import org.example.petproject.dao.MedicalRecordDAO;
import org.example.petproject.model.Appointment;
import org.example.petproject.model.MedicalRecord;
import org.example.petproject.model.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class DoctorDashboardController implements DashboardControllerBase {

    // You can remove these @FXML annotations if you no longer have these elements in your FXML
    // @FXML
    // private ImageView doctorImageView;
    //
    // @FXML
    // private Label doctorNameLabel;
    //
    // @FXML
    // private Label doctorSpecializationLabel;
    //
    // @FXML
    // private Label yearsPracticeLabel;
    //
    // @FXML
    // private Label licenseNumberLabel;

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
        // Set up table columns
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        timeColumn.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        petNameColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPet() != null) {
                return javafx.beans.binding.Bindings.createStringBinding(
                        () -> cellData.getValue().getPet().getName());
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "");
        });

        ownerColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPet() != null && cellData.getValue().getPet().getOwner() != null) {
                return javafx.beans.binding.Bindings.createStringBinding(
                        () -> cellData.getValue().getPet().getOwner().getFullName());
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "");
        });

        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add calendar date picker listener
        calendarDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && currentUser != null) {
                loadAppointments(newVal);
            }
        });

        writeMedicalRecordButton.setOnAction(e -> openMedicalRecordScreen());

        // Recent medical records list cell
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

        // Set default date to today
        calendarDatePicker.setValue(LocalDate.now());
    }

    /**
     * Gán dữ liệu user vào dashboard
     */
    @Override
    public void initUser(User user) {
        this.currentUser = user;
        if (user == null) {
            System.out.println("User is null in DoctorDashboardController");
            return;
        }
        if (lblUserName != null) {
            lblUserName.setText(user.getFullName());
        }
        if (imgAvatar != null) {
            try {
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
                    imgAvatar.setImage(new Image(user.getAvatarUrl()));
                } else {
                    imgAvatar.setImage(new Image(getClass().getResourceAsStream("/assets/icons/user.png")));
                }
            } catch (Exception e) {
                System.err.println("Error loading avatar: " + e.getMessage());
                try {
                    imgAvatar.setImage(new Image(getClass().getResourceAsStream("/assets/icons/user.png")));
                } catch (Exception ex) {
                    System.err.println("Error loading default avatar: " + ex.getMessage());
                }
            }
        }
        // Auto select today for calendarDatePicker
        if (calendarDatePicker != null) {
            calendarDatePicker.setValue(LocalDate.now());
        }
        loadAppointments(LocalDate.now());
        loadRecentMedicalRecords();
    }

    private void loadAppointments(LocalDate fromDate) {
        try {
            List<Appointment> appointments = appointmentDAO.findUpcomingByDoctorId(currentUser.getUserId(), fromDate);
            ObservableList<Appointment> appointmentList = FXCollections.observableArrayList(appointments);
            appointmentsTableView.setItems(appointmentList);
        } catch (Exception e) {
            showError("Error loading appointments: " + e.getMessage());
        }
    }

    private void openMedicalRecordScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/MedicalRecordView.fxml"));
            Parent root = loader.load();
            org.example.petproject.controller.MedicalRecordController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setSelectedDate(calendarDatePicker.getValue());
            controller.setDashboardController(this);
            Stage stage = new Stage();
            stage.setTitle("Write Medical Record");
            stage.setScene(new Scene(root));
            stage.setOnHidden(event -> reloadDashboardData());
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadRecentMedicalRecords() {
        try {
            List<MedicalRecord> records = medicalRecordDAO.findRecentByDoctorId(currentUser.getUserId());
            ObservableList<MedicalRecord> recordList = FXCollections.observableArrayList(records);
            recentMedicalRecordsListView.setItems(recordList);
        } catch (Exception e) {
            showError("Error loading recent medical records: " + e.getMessage());
        }
    }

    public void reloadDashboardData() {
        loadAppointments(calendarDatePicker.getValue());
        loadRecentMedicalRecords();
    }

    @FXML
    private void onProfile(javafx.scene.input.MouseEvent evt) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/EditUserProfileView.fxml"));
            Parent root = loader.load();
            org.example.petproject.controller.EditProfileController controller = loader.getController();
            controller.setUser(currentUser);
            Stage stage = new Stage();
            stage.setTitle("Edit Profile");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            // Reload dashboard data after closing
            reloadDashboardData();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Không thể mở màn chỉnh sửa hồ sơ: " + ex.getMessage());
        }
    }

    @FXML
    private void onLogout(ActionEvent evt) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Error during logout");
        }
    }

    private void navigateTo(String fxmlPath, ActionEvent evt) {
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if (fxmlUrl == null) {
            showError("Screen not found: " + fxmlPath);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent newRoot = loader.load();

            // initUser if needed
            Object ctrl = loader.getController();
            if (ctrl instanceof DashboardControllerBase dcb) {
                dcb.initUser(currentUser);
            }

            Scene scene = ((Node) evt.getSource()).getScene();
            scene.setRoot(newRoot);

            Stage stage = (Stage) scene.getWindow();
            stage.setMaximized(true);

        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Error opening screen: " + fxmlPath);
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}