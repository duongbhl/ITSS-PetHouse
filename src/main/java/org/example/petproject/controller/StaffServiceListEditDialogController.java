package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.petproject.model.ServiceBooking;
import org.example.petproject.dao.ServiceBookingDAO;
import java.time.LocalDate;

public class StaffServiceListEditDialogController {
    @FXML
    private DatePicker checkInDatePicker;
    @FXML
    private DatePicker checkOutDatePicker;
    @FXML
    private TextField petNameField;
    @FXML
    private TextArea noteArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private ServiceBooking booking;
    private Runnable onSaveCallback;
    private final ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();

    public void setBooking(ServiceBooking booking) {
        this.booking = booking;
        if (booking != null) {
            checkInDatePicker.setValue(booking.getCheckInTime());
            checkOutDatePicker.setValue(booking.getCheckOutTime());
            if (booking.getPet() != null) {
                petNameField.setText(booking.getPet().getName());
            }
            noteArea.setText(booking.getNote());
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void handleSave() {
        if (booking == null) return;
        // Validate
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();
        String petName = petNameField.getText();
        String note = noteArea.getText();
        if (checkIn == null || checkOut == null || petName == null || petName.isBlank()) {
            // Hiển thị thông báo lỗi nếu cần
            return;
        }
        booking.setCheckInTime(checkIn);
        booking.setCheckOutTime(checkOut);
        if (booking.getPet() != null) {
            booking.getPet().setName(petName);
        }
        booking.setNote(note);
        // Cập nhật database
        serviceBookingDAO.update(booking);
        if (onSaveCallback != null) onSaveCallback.run();
        closeDialog();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
} 