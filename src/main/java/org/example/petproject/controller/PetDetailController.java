package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.petproject.model.Pet;
import org.example.petproject.model.User;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PetDetailController implements Initializable {

    @FXML
    private Label lblUserName, lblName, lblSpecies, lblDob, lblAge, lblGender, lblWeight, lblDiet,
            lblHealthNotes;
    @FXML
    private ImageView ivPhoto;
    @FXML
    private Button btnEdit;

    private Pet pet;
    private User owner;

    /**
     * Gọi từ OwnerPetsController.showDetail trước khi hiển thị.
     */
    public void setPetAndOwner(Pet pet, User owner) {
        this.pet = pet;
        this.owner = owner;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Thiết lập nút sửa
        btnEdit.setOnAction(e -> openEditPet());
    }

    /**
     * Sau khi FXMLLoader.load() và setPetAndOwner(), gọi initData() để fill UI.
     */
    public void initData() {

        // 2) Ảnh pet
        if (pet.getPhotoUrl() != null) {
            ivPhoto.setImage(new Image(pet.getPhotoUrl()));
        } else {
            InputStream defaultImg = getClass().getResourceAsStream("/assets/icons/pets.png");
            if (defaultImg != null)
                ivPhoto.setImage(new Image(defaultImg));
        }

        // 3) Các thông tin text
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblName.setText(pet.getName());
        lblSpecies.setText(pet.getSpecies());
        lblDob.setText(pet.getBirthDate() != null ? pet.getBirthDate().format(fmt) : "");
        // Tính tuổi
        if (pet.getBirthDate() != null) {
            int age = Period.between(pet.getBirthDate(), LocalDate.now()).getYears();
            lblAge.setText(age + " tuổi");
        }
        lblGender.setText(pet.getGender() != null ? pet.getGender().getLabel() : "");
        lblWeight.setText(pet.getWeight() + " kg");
        lblDiet.setText(pet.getDiet() != null ? pet.getDiet() : "");
        lblHealthNotes.setText(pet.getHealthNotes() != null ? pet.getHealthNotes() : "");
    }

    private void openEditPet() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/AddPetView.fxml"));
            Parent root = loader.load();

            // chuyền pet cũ vào form AddPet để edit
            AddPetController addCtrl = loader.getController();
            addCtrl.setOwner(owner);
            addCtrl.setPetToEdit(pet);

            // preserve styles
            Scene parentScene = btnEdit.getScene();
            List<String> sheets = new ArrayList<>(parentScene.getStylesheets());
            Scene scene = new Scene(root);
            scene.getStylesheets().setAll(sheets);

            Stage dialog = new Stage();
            dialog.initOwner(parentScene.getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Sửa thông tin thú cưng");
            dialog.setScene(scene);
            dialog.showAndWait();

            // sau khi sửa xong, reload data mới từ DB
            initData();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không mở được form sửa").showAndWait();
        }
    }
}