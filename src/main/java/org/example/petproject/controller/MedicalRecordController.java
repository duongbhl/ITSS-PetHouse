package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.petproject.model.Appointment;
import org.example.petproject.model.User;
import org.example.petproject.model.Pet;
import org.example.petproject.model.MedicalRecord;
import org.example.petproject.dao.AppointmentDAO;
import org.example.petproject.dao.MedicalRecordDAO;
import org.example.petproject.controller.Dashboard.DoctorDashboardController;

import java.time.LocalDate;
import java.util.List;

public class MedicalRecordController {
    @FXML private ImageView imgLogo;
    @FXML private ListView<Appointment> appointmentsListView;
    @FXML private TextField petNameField;
    @FXML private TextField ownerNameField;
    @FXML private TextArea medicalHistoryArea;
    @FXML private TextArea treatmentPlanArea;
    @FXML private DatePicker scheduleDatePicker;
    @FXML private Button saveRecordButton;
    @FXML private Hyperlink dashboardLink;
    @FXML private Hyperlink appointmentsLink;

    private User currentUser; // Bác sĩ hiện tại
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    private DoctorDashboardController dashboardController;
    private LocalDate selectedDate;

    @FXML
    public void initialize() {
        // Load logo
        if (imgLogo.getImage() == null) {
            imgLogo.setImage(new Image(getClass().getResourceAsStream("/assets/logo.png")));
        }
        // Khi chưa có currentUser, chưa load lịch hẹn
        appointmentsListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String owner = item.getPet() != null && item.getPet().getOwner() != null
                        ? item.getPet().getOwner().getFullName() : "";
                    setText(item.getPet().getName() + " - " + owner + "\n" +
                            (item.getAppointmentTime() != null ? item.getAppointmentTime().toString() : ""));
                }
            }
        });
        // Khi chọn lịch hẹn, tự động điền thông tin pet và owner
        appointmentsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                petNameField.setText(newVal.getPet().getName());
                ownerNameField.setText(newVal.getPet().getOwner().getFullName());
            }
        });
        // Xử lý nút lưu
        saveRecordButton.setOnAction(e -> saveMedicalRecord());
    }

    // Gọi từ dashboard để truyền bác sĩ hiện tại
    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadTodayAppointments();
    }

    private void loadTodayAppointments() {
        if (currentUser == null) return;
        LocalDate today = LocalDate.now();
        List<Appointment> appointments = appointmentDAO.findByDoctorId(currentUser.getUserId(), today);
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList(appointments);
        appointmentsListView.setItems(appointmentList);
    }

    private void saveMedicalRecord() {
        Appointment selectedAppointment = appointmentsListView.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            showAlert("Please select an appointment.");
            return;
        }
        String history = medicalHistoryArea.getText();
        String treatment = treatmentPlanArea.getText();
        LocalDate scheduleDate = scheduleDatePicker.getValue();
        if (history == null || history.isBlank() || treatment == null || treatment.isBlank()) {
            showAlert("Please enter both Medical History and Treatment Plan.");
            return;
        }
        try {
            MedicalRecord record = new MedicalRecord();
            record.setAppointment(selectedAppointment);
            record.setPet(selectedAppointment.getPet());
            record.setDoctor(currentUser);
            record.setExamDate(LocalDate.now());
            record.setDiagnosis(history);
            record.setPrescription(treatment);
            record.setFollowUpDate(scheduleDate);
            medicalRecordDAO.save(record);
            selectedAppointment.setStatus(Appointment.Status.completed);
            appointmentDAO.update(selectedAppointment);
            showAlert("Medical record saved!");
            appointmentsListView.getItems().remove(selectedAppointment);
            medicalHistoryArea.clear();
            treatmentPlanArea.clear();
            scheduleDatePicker.setValue(null);
            if (dashboardController != null) {
                dashboardController.reloadDashboardData();
            }
        } catch (Exception ex) {
            showAlert("Error saving medical record: " + ex.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }

    // Khi load danh sách Scheduled Appointments, chỉ lấy các appointment chưa có record hoặc chưa completed
    private void loadAppointmentsForDate(LocalDate date) {
        if (currentUser == null || date == null) return;
        List<Appointment> appointments = appointmentDAO.findAvailableForMedicalRecord(currentUser.getUserId(), date);
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList(appointments);
        appointmentsListView.setItems(appointmentList);
    }

    public void setDashboardController(DoctorDashboardController controller) {
        this.dashboardController = controller;
    }

    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
        loadAppointmentsForDate(date);
    }
} 