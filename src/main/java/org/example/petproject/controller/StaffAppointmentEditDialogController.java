package org.example.petproject.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.petproject.model.Appointment;
import org.example.petproject.dao.AppointmentDAO;
import org.example.petproject.dao.PetDAO;
import org.example.petproject.dao.UserDAO;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StaffAppointmentEditDialogController implements Initializable {

    @FXML
    private DatePicker appointmentTimePicker;
    @FXML
    private TextField petNameField;
    @FXML
    private TextField speciesField;
    @FXML
    private TextField ownerNameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private ComboBox<String> appointmentTypeComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Appointment appointment;
    private boolean saveClicked = false;
    private final PetDAO petDAO = new PetDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing StaffAppointmentEditDialogController");
        
        // Initialize appointment type ComboBox with default values
        appointmentTypeComboBox.setItems(FXCollections.observableArrayList(
            "Khám tổng quát",
            "Tiêm phòng",
            "Siêu âm"
        ));

        // Make fields editable
        petNameField.setEditable(true);
        speciesField.setEditable(true);
        ownerNameField.setEditable(true);
        phoneNumberField.setEditable(true);
        appointmentTimePicker.setEditable(true);
        appointmentTypeComboBox.setEditable(false);

        // Add event handlers
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> handleCancel());
    }

    public void setAppointment(Appointment appointment) {
        System.out.println("Setting appointment: " + (appointment != null ? appointment.getAppointmentId() : "null"));
        this.appointment = appointment;
        
        // Populate fields with appointment data
        if (appointment != null) {
            appointmentTimePicker.setValue(appointment.getAppointmentTime());
            if (appointment.getPet() != null) {
                petNameField.setText(appointment.getPet().getName());
                speciesField.setText(appointment.getPet().getSpecies());
                if (appointment.getPet().getOwner() != null) {
                    ownerNameField.setText(appointment.getPet().getOwner().getFullName());
                    phoneNumberField.setText(appointment.getPet().getOwner().getPhone());
                }
            }
            appointmentTypeComboBox.setValue(appointment.getType());
        }
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        System.out.println("Handling save action");
        if (isInputValid()) {
            try {
                // Update appointment object with new values
                appointment.setAppointmentTime(appointmentTimePicker.getValue());
                appointment.setType(appointmentTypeComboBox.getValue());

                // Update pet information if changed
                if (appointment.getPet() != null) {
                    appointment.getPet().setName(petNameField.getText().trim());
                    appointment.getPet().setSpecies(speciesField.getText().trim());
                    
                    // Update owner information if changed
                    if (appointment.getPet().getOwner() != null) {
                        appointment.getPet().getOwner().setFullName(ownerNameField.getText().trim());
                        appointment.getPet().getOwner().setPhone(phoneNumberField.getText().trim());
                        
                        // Update owner in database
                        userDAO.update(appointment.getPet().getOwner());
                    }
                    
                    // Update pet in database
                    petDAO.update(appointment.getPet());
                }

                // Save appointment to database
                AppointmentDAO appointmentDAO = new AppointmentDAO();
                try {
                    System.out.println("Saving appointment to database...");
                    appointmentDAO.update(appointment);
                    saveClicked = true;
                    showAlert("Thành công", "Cập nhật lịch hẹn thành công!");
                    closeDialog();
                } finally {
                    appointmentDAO.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể cập nhật lịch hẹn: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("Handling cancel action");
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (appointmentTimePicker.getValue() == null) {
            errorMessage += "Vui lòng chọn thời gian!\n";
        }
        if (petNameField.getText() == null || petNameField.getText().trim().isEmpty()) {
            errorMessage += "Vui lòng nhập tên thú cưng!\n";
        }
        if (speciesField.getText() == null || speciesField.getText().trim().isEmpty()) {
            errorMessage += "Vui lòng nhập loài!\n";
        }
        if (ownerNameField.getText() == null || ownerNameField.getText().trim().isEmpty()) {
            errorMessage += "Vui lòng nhập tên chủ nuôi!\n";
        }
        if (phoneNumberField.getText() == null || phoneNumberField.getText().trim().isEmpty()) {
            errorMessage += "Vui lòng nhập số điện thoại!\n";
        }
        if (appointmentTypeComboBox.getValue() == null) {
            errorMessage += "Vui lòng chọn loại hẹn!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            showAlert("Lỗi", errorMessage);
            return false;
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