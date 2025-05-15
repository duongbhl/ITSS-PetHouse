package org.example.petproject.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.petproject.dao.PetDAO;
import org.example.petproject.dao.UserDAO;
import org.example.petproject.model.Appointment;
import org.example.petproject.model.User;
import org.example.petproject.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class datlichkhamController {

    UserService userService = new UserService();

    private PetDAO petDAO = new PetDAO();
    private UserDAO userDao=new UserDAO();


    private String ownerID;

    public datlichkhamController() {
        // Nếu bạn cần inject service nào đó thì để riêng qua setter hoặc sau đó mới xử lý
    }

    public datlichkhamController(String ownerID) {
        this.ownerID = ownerID;
    }


    @FXML
    private Label ownerName;

    @FXML
    private ComboBox<String> petSelected;

    @FXML
    private ComboBox<String> examSelected;

    @FXML
    private DatePicker scheduleSelected;


    @FXML
    void bookAppointmentButton(ActionEvent event) {
        userService.datlichkham(petSelected.getValue(),scheduleSelected.getValue(),examSelected.getValue(), Appointment.Status.pending);
    }

    @FXML
    void arrowpressedButton(ActionEvent event) {

    }

    @FXML
    void initialize() {
        petSelected.setItems(FXCollections.observableArrayList(petDAO.findAllByOwnerId(this.ownerID)));
        examSelected.setItems(FXCollections.observableArrayList("Kham tong quat", "Tiem phong", "Sieu am"));
        ownerName.setText(userDao.getUserByOwnerID(ownerID).getFullName());


    }

}
