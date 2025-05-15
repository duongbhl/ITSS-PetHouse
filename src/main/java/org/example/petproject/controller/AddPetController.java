package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.petproject.model.Pet;
import org.example.petproject.model.User;
import org.example.petproject.service.PetService;
import org.example.petproject.utils.FileStorageUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddPetController implements Initializable {

    @FXML
    private ImageView ivPetPhoto;
    @FXML
    private TextField tfName, tfSpecies, tfWeight;
    @FXML
    private DatePicker dpBirth;
    @FXML
    private ChoiceBox<Pet.Gender> cbGender;
    @FXML
    private TextArea taDiet, taHealthNotes;
    @FXML
    private Button btnSave, btnCancel;

    private final PetService petService = new PetService();
    private User owner;
    private File photoFile;
    private Pet currentPet; // ★ giữ pet đang edit (null nếu add mới)

    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Gọi khi muốn edit: truyền vào pet, prefill lên form.
     */
    public void setPetToEdit(Pet pet) {
        this.currentPet = pet;
        // Prefill:
        tfName.setText(pet.getName());
        tfSpecies.setText(pet.getSpecies());
        dpBirth.setValue(pet.getBirthDate());
        cbGender.getSelectionModel().select(pet.getGender());
        tfWeight.setText(pet.getWeight() != null ? pet.getWeight().toString() : "");
        taDiet.setText(pet.getDiet());
        taHealthNotes.setText(pet.getHealthNotes());
        if (pet.getPhotoUrl() != null && !pet.getPhotoUrl().isBlank()) {
            ivPetPhoto.setImage(new Image(pet.getPhotoUrl()));
        }
        // Thay text nút lưu
        btnSave.setText("Cập nhật thú cưng");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbGender.getItems().setAll(Pet.Gender.values());
        // hiển thị label tùy enum
        cbGender.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Pet.Gender g) {
                return g == null ? "" : g.getLabel();
            }

            @Override
            public Pet.Gender fromString(String s) {
                for (Pet.Gender g : Pet.Gender.values())
                    if (g.getLabel().equals(s))
                        return g;
                return null;
            }
        });
        cbGender.getSelectionModel().selectFirst();

        btnCancel.setOnAction(e -> close());
        btnSave.setOnAction(e -> onSave());
    }

    @FXML
    private void onUploadPhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn ảnh thú cưng");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File f = chooser.showOpenDialog(ivPetPhoto.getScene().getWindow());
        if (f != null) {
            photoFile = f;
            ivPetPhoto.setImage(new Image(f.toURI().toString()));
        }
    }

    private void onSave() {
        // 1) Lấy & validate
        String name = tfName.getText().trim();
        String species = tfSpecies.getText().trim();
        LocalDate birth = dpBirth.getValue();
        Pet.Gender gender = cbGender.getValue();
        String wStr = tfWeight.getText().trim();
        if (owner == null || name.isEmpty() || species.isEmpty() || birth == null || gender == null || wStr.isEmpty()) {
            showAlert("Vui lòng điền đầy đủ thông tin.");
            return;
        }
        float weight;
        try {
            weight = Float.parseFloat(wStr);
        } catch (NumberFormatException ex) {
            showAlert("Cân nặng phải là số.");
            return;
        }

        // 2) Build hoặc cập nhật entity
        Pet pet = (currentPet != null ? currentPet : new Pet());
        pet.setOwner(owner);
        pet.setName(name);
        pet.setSpecies(species);
        pet.setBirthDate(birth);
        pet.setGender(gender);
        pet.setWeight(weight);
        pet.setDiet(taDiet.getText().trim());
        pet.setHealthNotes(taHealthNotes.getText().trim());

        // 3) Xử lý ảnh
        if (photoFile != null) {
            try {
                String url = FileStorageUtil.storeImage(photoFile);
                pet.setPhotoUrl(url);
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Lỗi lưu ảnh.");
                return;
            }
        }

        // 4) Lưu DB: nếu edit (currentPet!=null) dùng merge trong service
        petService.save(pet);
        close();
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    @FXML
    private void close() {
        ((Stage) ivPetPhoto.getScene().getWindow()).close();
    }
}
