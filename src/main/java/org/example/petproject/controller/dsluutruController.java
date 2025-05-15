package org.example.petproject.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.petproject.dao.PetBoardingDAO;
import org.example.petproject.dao.RoomDAO;
import org.example.petproject.dao.ServiceBookingDAO;
import org.example.petproject.dao.UserDAO;
import org.example.petproject.model.PetBoardingInfo;
import org.example.petproject.model.ServiceBooking;

import java.io.IOException;
import java.util.List;

public class dsluutruController {
    ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();
    PetBoardingDAO petBoardingDAO = new PetBoardingDAO();
    RoomDAO roomDAO = new RoomDAO();

    private String ownerID;

    public dsluutruController() {}

    public dsluutruController(String ownerID) {
        this.ownerID = ownerID;
    }

    @FXML
    private FlowPane cardsContainer;

    @FXML
    private Label ownerName;

    @FXML
    void arrowPressedButton(ActionEvent event) {
        luutruController controller = new luutruController("U002");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/luutruScreen.fxml"));
        loader.setController(controller);
        Parent root = null;
        try{
            root=loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);
        Stage stage = (Stage) this.ownerName.getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    void handleAddCard(ActionEvent event) {
        dkdvluutruController controller = new dkdvluutruController("U002");
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/org/example/petproject/dkdvluutruScreen.fxml"));
        fxmlLoader.setController(controller);
        Parent root = null;
        try{
            root=fxmlLoader.load();
        }catch(IOException e){
            e.printStackTrace();
        }
        Stage newStage =(Stage) this.ownerName.getScene().getWindow();
        Scene scene = new Scene(root);
        newStage.setScene(scene);
        newStage.show();
    }

    private ObservableList<PetBoardingInfo> boardedPetsData = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        ownerName.setText(new UserDAO().getUserByOwnerID(this.ownerID).getFullName());
        loadPetData();
        displayPetCards();
    }

    private void loadPetData(){
        List<ServiceBooking> list=serviceBookingDAO.getBookingsByOwnerId(this.ownerID, ServiceBooking.Status.in_progress,"S002");
        for (ServiceBooking serviceBooking : list) {
            boardedPetsData.add(new PetBoardingInfo(
                    serviceBooking.getBookingId(),
                    serviceBooking.getPet().getName(),
                    serviceBooking.getHandledBy().getFullName(),
                    serviceBooking.getHandledBy().getPhone(),
                    petBoardingDAO.findPetBoardingByServiceId(serviceBooking.getBookingId()).getRoom().getName(),
                    petBoardingDAO.findPetBoardingByServiceId(serviceBooking.getBookingId()).getRoom().getType().toString(),
                    serviceBooking.getCheckInTime().toString(),
                    serviceBooking.getCheckOutTime().toString(),
                    roomDAO.findByName(petBoardingDAO.findPetBoardingByServiceId(serviceBooking.getBookingId()).getRoom().getName()).getPricePerDay().toString()
            ));
        }
    }

    public void displayPetCards() {

        for (PetBoardingInfo petInfo : boardedPetsData) {
            try {
                petCardController controller=new petCardController();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/petCard.fxml"));
                loader.setController(controller);
                VBox petCardNode = loader.load();
                controller.setDataDSLuuTru(petInfo, this);
                cardsContainer.getChildren().add(petCardNode);
            } catch (IOException e) {
                System.err.println("Error loading PetCard.fxml for " + petInfo.getPetName());
                e.printStackTrace();
            }
        }
    }
}
