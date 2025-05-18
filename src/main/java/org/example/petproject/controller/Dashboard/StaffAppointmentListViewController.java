package org.example.petproject.controller.Dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.petproject.model.Appointment;
import org.example.petproject.model.User;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class StaffAppointmentListViewController implements Initializable, DashboardControllerBase{

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
        // Set up table columns
        selectColumn.setCellFactory(col -> new CheckBoxTableCell<>());
        selectColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));

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
        appointmentTableView.getItems().addAll(appointments);
    }

    @FXML
    public void handleConfirmButtonAction(ActionEvent actionEvent) {
        // Implementation for confirming selected appointments
    }

    @FXML
    public void handleRejectButtonAction(ActionEvent actionEvent) {
        // Implementation for rejecting selected appointments
    }
}
