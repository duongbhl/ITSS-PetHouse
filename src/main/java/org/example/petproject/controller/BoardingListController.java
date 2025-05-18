package org.example.petproject.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.dao.PetBoardingDAO;
import org.example.petproject.dao.RoomDAO;
import org.example.petproject.dao.ServiceBookingDAO;
import org.example.petproject.dao.UserDAO;
import org.example.petproject.model.PetBoardingInfo;
import org.example.petproject.model.ServiceBooking;
import org.example.petproject.model.User;
import org.example.petproject.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class BoardingListController {
    ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();
    PetBoardingDAO petBoardingDAO = new PetBoardingDAO();
    RoomDAO roomDAO = new RoomDAO();

    private String ownerID= SessionManager.getCurrentUser().getUserId();

    @FXML
    private ImageView imgLogo;

    @FXML
    private FlowPane cardsContainer;

    @FXML
    private Label ownerName;

    @FXML
    private ImageView ownerAvatar;

    @FXML
    private void handleLogoClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/BoardingView.fxml"));
            Parent root = loader.load();
            Scene scene = imgLogo.getScene();
            root.getStylesheets().setAll(scene.getStylesheets());
            scene.setRoot(root);
            Stage stage = (Stage) scene.getWindow();
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAddCard(ActionEvent evt) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/RegisterBoardingView.fxml"));
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Đăng ký dịch vụ làm đẹp & vệ sinh");
            dialog.initOwner(((Node) evt.getSource()).getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            // Sau khi đóng form, load lại data
            loadPetData();
        } catch (IOException ex) {
            showError("Không thể mở form đăng ký dịch vụ.");
            ex.printStackTrace();
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Lỗi");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private ObservableList<PetBoardingInfo> boardedPetsData = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        ownerName.setText(new UserDAO().getUserByOwnerID(this.ownerID).getFullName());
        try{
            //ownerAvatar.setImage(new Image(SessionManager.getCurrentUser().getAvatarUrl()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
                    serviceBooking.getNote().trim(),
                    roomDAO.findByName(petBoardingDAO.findPetBoardingByServiceId(serviceBooking.getBookingId()).getRoom().getName()).getPricePerDay().toString()
            ));
        }
    }

    public void displayPetCards() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER); // Canh giữa cả theo chiều ngang và dọc
        vbox.setPrefHeight(376);
        vbox.setPrefWidth(270);
        vbox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 15; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: #E0E0E0; " +
                        "-fx-border-width: 1.5; " +
                        "-fx-border-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0.2, 0, 0);"
        );

        Button button = new Button("+");
        button.setPrefSize(70, 70); // Kích thước đều để tạo hình tròn
        button.setStyle(
                "-fx-font-size: 36px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-color: #4CAF50; " +
                        "-fx-background-radius: 35px;" + // Làm cho button tròn
                        "-fx-padding: 0;"
        );

        button.setShape(new Circle(35));// Thiết lập hình dạng tròn
        button.setOnAction(e->{handleAddCard(e);});
        vbox.getChildren().add(button);

        for (PetBoardingInfo petInfo : boardedPetsData) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/PetCardView.fxml"));
                BorderPane petCardNode = loader.load();
                PetCardController controller=loader.getController();
                controller.setData(petInfo);
                cardsContainer.getChildren().add(petCardNode);
            } catch (IOException e) {
                System.err.println("Error loading PetCard.fxml for " + petInfo.getPetName());
                e.printStackTrace();
            }
        }
        cardsContainer.getChildren().add(vbox);
    }
}