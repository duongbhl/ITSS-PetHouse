package org.example.petproject.controller.Dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.petproject.dao.AppointmentDAO;
import org.example.petproject.model.Appointment;
import org.example.petproject.model.User;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StaffAppointmentListController implements Initializable, DashboardControllerBase{

    @FXML
    private TableView<Appointment> appointmentTableView;

    @FXML
    private TableColumn<Appointment, Boolean> selectColumn;

    @FXML
    private TableColumn<Appointment, LocalDate> timeColumn;

    @FXML
    private TableColumn<Appointment, String> petNameColumn;

    @FXML
    private TableColumn<Appointment, String> speciesColumn;

    @FXML
    private TableColumn<Appointment, String> ownerNameColumn;

    @FXML
    private TableColumn<Appointment, String> phoneNumberColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentTypeColumn;

    @FXML
    private Label userNameLabel;

    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the TableView to be editable
        appointmentTableView.setEditable(true); // <--- Add this line

        // Set up table columns
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setEditable(true); // This makes the cells in this column editable

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

        speciesColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPet() != null) {
                return javafx.beans.binding.Bindings.createStringBinding(
                        () -> cellData.getValue().getPet().getSpecies());
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "");
        });

        ownerNameColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPet() != null && cellData.getValue().getPet().getOwner() != null) {
                return javafx.beans.binding.Bindings.createStringBinding(
                        () -> cellData.getValue().getPet().getOwner().getFullName());
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "");
        });

        phoneNumberColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPet() != null && cellData.getValue().getPet().getOwner() != null) {
                return javafx.beans.binding.Bindings.createStringBinding(
                        () -> cellData.getValue().getPet().getOwner().getPhone());
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "");
        });

        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        userNameLabel.setText(user.getFullName());
    }

    public void initData(List<Appointment> appointments) {
        appointmentTableView.getItems().clear();
        if (appointments != null) {
            appointmentTableView.getItems().addAll(appointments);
        }
    }

    @FXML
    public void handleConfirmButtonAction(ActionEvent actionEvent) {
        // Get selected appointments
        List<Appointment> selectedAppointments = appointmentTableView.getItems().stream()
                .filter(Appointment::isSelected)
                .collect(Collectors.toList());

        if (selectedAppointments.isEmpty()) {
            showAlert("No Selection", "Please select at least one appointment to confirm.");
            return;
        }

        // Update the status of selected appointments to "confirmed"
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        try {
            for (Appointment appointment : selectedAppointments) {
                appointment.setStatus(Appointment.Status.confirmed);
                appointment.setConfirmedBy(currentUser); // Optionally set who confirmed
                appointment.setConfirmedAt(java.time.LocalDateTime.now()); // Optionally set confirmation time
                appointmentDAO.update(appointment);
            }
            // Refresh the table view to show updated status and clear selections
            // Re-fetch or update items in place if necessary
            List<Appointment> currentItems = appointmentTableView.getItems();
            appointmentTableView.getItems().clear(); // Clear and re-add to reset selection visual state
            appointmentTableView.getItems().addAll(currentItems);
            appointmentTableView.refresh(); // Ensure UI updates
            showAlert("Success", "Selected appointments have been confirmed.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to confirm appointments: " + e.getMessage());
        } finally {
            appointmentDAO.close(); // Close EntityManager if your BaseDAO requires it
        }
    }

    @FXML
    public void handleRejectButtonAction(ActionEvent actionEvent) {
        // No action needed for reject button as per requirements
        showAlert("Info", "No changes were made.");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}