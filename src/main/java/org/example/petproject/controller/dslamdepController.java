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
import org.example.petproject.dao.ServiceBookingDAO;
import org.example.petproject.dao.UserDAO;
import org.example.petproject.model.PetBoardingInfo;
import org.example.petproject.model.ServiceBooking;

import java.io.IOException;
import java.util.List;

public class dslamdepController {

    ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();

    private String ownerID;

    public dslamdepController() {}

    public dslamdepController(String ownerID) {
        this.ownerID = ownerID;
    }

    @FXML
    private FlowPane cardsContainer;

    @FXML
    private Label ownerName;

    @FXML
    void arrowPressedButton(ActionEvent event) {
        //main
    }

    @FXML
    void handleAddCard(ActionEvent event) {
        dkdvlamdepController controller = new dkdvlamdepController("U002");
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/org/example/petproject/dkdvlamdepScreeen.fxml"));
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
        List<ServiceBooking>list=serviceBookingDAO.getBookingsByOwnerId(this.ownerID, ServiceBooking.Status.in_progress,"S001");
        for (ServiceBooking serviceBooking : list) {
            boardedPetsData.add(new PetBoardingInfo(
                    serviceBooking.getBookingId(),
                    serviceBooking.getPet().getName(),
                    serviceBooking.getHandledBy().getFullName(),
                    serviceBooking.getHandledBy().getPhone(),
                    "",
                    "",
                    serviceBooking.getCheckInTime().toString(),
                    serviceBooking.getCheckOutTime().toString(),
                   "200000"
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
                controller.setDataDSLamDep(petInfo, this);
                cardsContainer.getChildren().add(petCardNode);
            } catch (IOException e) {
                System.err.println("Error loading PetCard.fxml for " + petInfo.getPetName());
                e.printStackTrace();
            }
        }
    }
}
