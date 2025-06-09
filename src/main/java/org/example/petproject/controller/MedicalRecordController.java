package org.example.petproject.controller;

import java.time.LocalDate;
import java.util.List;

import org.example.petproject.controller.Dashboard.DoctorDashboardController;
import org.example.petproject.dao.AppointmentDAO;
import org.example.petproject.dao.MedicalRecordDAO;
import org.example.petproject.model.Appointment;
import org.example.petproject.model.MedicalRecord;
import org.example.petproject.model.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MedicalRecordController {
    @FXML private ImageView imgLogo;
    @FXML private ListView<Appointment> appointmentsListView;
    @FXML private TextField petNameField;
    @FXML private TextField ownerNameField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextArea symptomsArea;
    @FXML private TextArea diagnosisArea;
    @FXML private TextArea prescriptionArea;
    @FXML private TextArea treatmentArea;
    @FXML private DatePicker followUpDatePicker;
    @FXML private TextArea noteArea;
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

        // Initialize type combo box
        typeComboBox.setItems(FXCollections.observableArrayList(
            "General Checkup",
            "Vaccination",
            "Dental Check",
            "Skin Check",
            "Blood Test",
            "Follow-up"
        ));

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
                // Set default type based on appointment type
                typeComboBox.setValue(newVal.getType());
            }
        });

        // Xử lý nút lưu
        saveRecordButton.setOnAction(e -> saveMedicalRecord());
    }

    // Gọi từ dashboard để truyền bác sĩ hiện tại
    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadAppointments();
    }

    private void loadAppointments() {
        if (currentUser == null) return;
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }
        List<Appointment> appointments = appointmentDAO.findAvailableForMedicalRecord(currentUser.getUserId(), selectedDate);
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList(appointments);
        appointmentsListView.setItems(appointmentList);
    }

    private void saveMedicalRecord() {
        Appointment selectedAppointment = appointmentsListView.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            showAlert("Please select an appointment.");
            return;
        }

        // Validate required fields
        if (typeComboBox.getValue() == null || 
            symptomsArea.getText().isBlank() || 
            diagnosisArea.getText().isBlank() || 
            prescriptionArea.getText().isBlank() || 
            treatmentArea.getText().isBlank()) {
            showAlert("Please fill in all required fields.");
            return;
        }

        try {
            MedicalRecord record = new MedicalRecord();
            record.setAppointment(selectedAppointment);
            record.setPet(selectedAppointment.getPet());
            record.setDoctor(currentUser);
            record.setExamDate(LocalDate.now());
            record.setType(typeComboBox.getValue());
            record.setSymptoms(symptomsArea.getText());
            record.setDiagnosis(diagnosisArea.getText());
            record.setPrescription(prescriptionArea.getText());
            record.setTreatment(treatmentArea.getText());
            record.setFollowUpDate(followUpDatePicker.getValue());
            record.setNote(noteArea.getText());
            
            medicalRecordDAO.save(record);
            
            // Update appointment status
            selectedAppointment.setStatus(Appointment.Status.completed);
            appointmentDAO.update(selectedAppointment);
            
            showAlert("Medical record saved successfully!");
            
            // Clear form
            clearForm();
            
            // Reload appointments
            loadAppointments();
            
            // Update dashboard if available
            if (dashboardController != null) {
                dashboardController.reloadDashboardData();
            }
        } catch (Exception ex) {
            showAlert("Error saving medical record: " + ex.getMessage());
        }
    }

    private void clearForm() {
        petNameField.clear();
        ownerNameField.clear();
        typeComboBox.setValue(null);
        symptomsArea.clear();
        diagnosisArea.clear();
        prescriptionArea.clear();
        treatmentArea.clear();
        followUpDatePicker.setValue(null);
        noteArea.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }

    public void setDashboardController(DoctorDashboardController controller) {
        this.dashboardController = controller;
    }

    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
        loadAppointments();
    }
} 