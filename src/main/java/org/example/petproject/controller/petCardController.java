package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.petproject.dao.ServiceBookingDAO;
import org.example.petproject.dao.UserDAO;
import org.example.petproject.model.PetBoardingInfo;

public class petCardController {


    UserDAO userDAO = new UserDAO();
    ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();


    private String servicebookingid;

    private Double price;

    public petCardController() {}

    public petCardController(String servicebookingid, Double price) {
        this.servicebookingid = servicebookingid;
        this.price = price;
    }

    @FXML
    private Label staffLabel;

    @FXML
    private Label staffnameLabel;

    @FXML
    private Label staffphoneLabel;

    @FXML
    private Label roomnameLabel;

    @FXML
    private Label roomtypeLabel;

    @FXML
    private Label checkinLabel;

    @FXML
    private Label checkoutLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label petNameLabel;

    @FXML
    private TextField staffNameField;

    @FXML
    private TextField staffPhoneField;

    @FXML
    private TextField roomNumberField;

    @FXML
    private TextField roomTypeField;

    @FXML
    private TextField checkInDateField;

    @FXML
    private TextField checkOutDateField;

    @FXML
    private TextField priceField;

    private PetBoardingInfo currentPetInfo;
    private dslamdepController listlamdepController;// Reference to the main list controller
    private  dsluutruController listluutruController;

    public void setDataDSLamDep(PetBoardingInfo petInfo, dslamdepController listlamdepController) {
        this.currentPetInfo = petInfo;
        this.listlamdepController = listlamdepController; // Store reference to call update methods if needed
        petNameLabel.setText(currentPetInfo.getPetName());
        staffNameField.setText(petInfo.getStaffName() != null ? petInfo.getStaffName() : "");
        staffPhoneField.setText(petInfo.getStaffPhone() != null ? petInfo.getStaffPhone() : "");
        roomNumberField.setText(petInfo.getRoomName() != null ? petInfo.getRoomName() : "");
        roomTypeField.setText(petInfo.getRoomType() != null ? petInfo.getRoomType() : "");
        checkInDateField.setText(petInfo.getCheckInDate() != null ? petInfo.getCheckInDate() : "");
        checkOutDateField.setText(petInfo.getCheckOutDate() != null ? petInfo.getCheckOutDate() : "");
        priceField.setText(petInfo.getPrice() != null ? petInfo.getPrice() : "");
    }

    public void setDataDSLuuTru(PetBoardingInfo petInfo, dsluutruController listluutruController) {
        this.currentPetInfo = petInfo;
        this.listluutruController = listluutruController; // Store reference to call update methods if needed
        petNameLabel.setText(currentPetInfo.getPetName());
        staffNameField.setText(petInfo.getStaffName() != null ? petInfo.getStaffName() : "");
        staffPhoneField.setText(petInfo.getStaffPhone() != null ? petInfo.getStaffPhone() : "");
        roomNumberField.setText(petInfo.getRoomName() != null ? petInfo.getRoomName() : "");
        roomTypeField.setText(petInfo.getRoomType() != null ? petInfo.getRoomType() : "");
        checkInDateField.setText(petInfo.getCheckInDate() != null ? petInfo.getCheckInDate() : "");
        checkOutDateField.setText(petInfo.getCheckOutDate() != null ? petInfo.getCheckOutDate() : "");
        priceField.setText(petInfo.getPrice() != null ? petInfo.getPrice() : "");
    }

}
