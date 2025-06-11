package org.example.petproject.controller;

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
import javafx.beans.property.SimpleObjectProperty;

import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.controller.Dashboard.StaffDashboardController;
import org.example.petproject.dao.AppointmentDAO;
import org.example.petproject.dao.UserDAO;
import org.example.petproject.model.Appointment;
import org.example.petproject.model.User;

import java.io.IOException;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class StaffAppointmentListController implements Initializable, DashboardControllerBase {

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
    private TableColumn<Appointment, Appointment.Status> statusColumn;

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

        // Set up status column
        statusColumn.setCellValueFactory(cellData -> {
            Appointment appointment = cellData.getValue();
            return new SimpleObjectProperty<>(appointment != null ? appointment.getStatus() : null);
        });
        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Appointment.Status status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.toString());
                    
                }
            }
        });
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
            showAlert("No Selection", "Không có lịch hẹn nào được chọn, hãy chọn lịch hẹn để xác nhận.");
            return;
        }

        // Update the status of selected appointments to "confirmed"
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        UserDAO userDAO = new UserDAO();
        User designatedDoctor = null;
        try {
            List<User> doctors = userDAO.findUsersByRole(User.Role.doctor);
            if (!doctors.isEmpty()) {
                designatedDoctor = doctors.get(0);
            } else {
                showAlert("Error", "Không tìm thấy bác sĩ trong hệ thống. Không thể gán lịch hẹn.");
                return;
            }
            for (Appointment appointment : selectedAppointments) {
                appointment.setStatus(Appointment.Status.confirmed);
                appointment.setConfirmedBy(currentUser);
                appointment.setConfirmedAt(java.time.LocalDateTime.now());
                appointment.setDoctor(designatedDoctor);
                appointmentDAO.update(appointment);
            }
            
            // Refresh the table view to show updated status
            appointmentTableView.refresh();
            showAlert("Success", "Lựa chọn lịch hẹn đã được xác nhận.");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi ", "Không thể xác nhận lịch hẹn: " + e.getMessage());
        } finally {
            appointmentDAO.close();
        }
    }

    @FXML
    private void handleRejectButtonAction(ActionEvent event) {
        // Get selected appointments
        List<Appointment> selectedAppointments = appointmentTableView.getItems().stream()
                .filter(Appointment::isSelected)
                .collect(Collectors.toList());
        
        if (selectedAppointments.isEmpty()) {
            showAlert("Cảnh báo", "Vui lòng chọn ít nhất một lịch hẹn để từ chối!");
            return;
        }

        AppointmentDAO appointmentDAO = new AppointmentDAO();
        try {
            // Update each selected appointment
            for (Appointment appointment : selectedAppointments) {
                // Only update pending appointments
                if (appointment.getStatus() == Appointment.Status.pending) {
                    appointment.setStatus(Appointment.Status.cancelled);
                    appointment.setConfirmedBy(currentUser);
                    appointment.setConfirmedAt(LocalDateTime.now());
                    appointmentDAO.update(appointment);
                }
            }
            
            // Refresh the table view to show updated status
            appointmentTableView.refresh();
            
            showAlert("Thành công", "Đã từ chối các lịch hẹn đã chọn!");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể từ chối lịch hẹn: " + e.getMessage());
        } finally {
            appointmentDAO.close();
        }
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
}
