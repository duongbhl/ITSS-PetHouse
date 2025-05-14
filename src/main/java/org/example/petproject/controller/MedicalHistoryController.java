package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.example.petproject.model.Pet;
import org.example.petproject.model.MedicalRecord;
import org.example.petproject.service.PetService;
import org.example.petproject.service.MedicalRecordService;
import org.example.petproject.util.SessionManager;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MedicalHistoryController {

    @FXML private TextField txtPetName;
    @FXML private Button btnSearchPet;
    @FXML private ComboBox<Pet> comboPets;
    @FXML private VBox cardContainer;
    @FXML private  Label lblUsername;

    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {


        var user = SessionManager.getCurrentUser();
        if (user != null) {
            lblUsername.setText(user.getFullName());
        }


            // Setup ComboBox để chỉ show tên pet
        comboPets.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pet pet) {
                return pet == null ? "" : pet.getName();
            }
            @Override
            public Pet fromString(String string) {
                return null;
            }
        });
        comboPets.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pet item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        // (Tùy chọn) load sẵn tất cả thú cưng của owner khi form khởi tạo:
        String ownerId = SessionManager.getCurrentUser().getUserId();
        comboPets.getItems().setAll(PetService.getByOwner(ownerId));
    }

    /** Người dùng bấm “Tìm Thú Cưng” để filter theo tên (hoặc load all nếu để trống) */
    @FXML
    private void handleSearchPet() {
        String kw = txtPetName.getText().trim();
        String ownerId = SessionManager.getCurrentUser().getUserId();
        List<Pet> list;
        if (kw.isEmpty()) {
            list = PetService.getByOwner(ownerId);
        } else {
            list = PetService.searchByName(kw, ownerId);
        }
        comboPets.getItems().setAll(list);
        if (list.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Không tìm thấy thú cưng.").show();
        }
    }

    /** Tự động chạy khi bạn chọn 1 item trong ComboBox */
    @FXML
    private void handlePetSelected() {
        Pet pet = comboPets.getValue();
        if (pet == null) return;
        List<MedicalRecord> records = MedicalRecordService.getByPet(pet.getPetId());
        // Hiển thị động từng “card”
        cardContainer.getChildren().clear();
        for (MedicalRecord r : records) {
            cardContainer.getChildren().add(buildCard(r));
        }
    }
    private Label createIconLabel(String text, String iconFileName) {
        ImageView iv = new ImageView(
                new Image(getClass().getResourceAsStream("/asetss/icons/" + iconFileName))
        );
        iv.setFitWidth(20);
        iv.setFitHeight(20);
        Label lbl = new Label(text, iv);
        lbl.setContentDisplay(ContentDisplay.LEFT);
        lbl.setGraphicTextGap(8);
        return lbl;
    }

    /** Xây dựng mỗi card của 1 MedicalRecord (giống mẫu cũ) */
    private VBox buildCard(MedicalRecord r) {
        VBox card = new VBox();
        card.setStyle(
                "-fx-border-color: #BDBDBD; -fx-border-radius: 5; " +
                        "-fx-background-color: white; -fx-background-radius: 5;"
        );

        Label lblDate = new Label(r.getExamDate().format(dateFmt));
        lblDate.setStyle(
                "-fx-background-color: #EEEEEE; -fx-font-weight: bold; " +
                        "-fx-padding: 5; -fx-border-color: #BDBDBD; " +
                        "-fx-border-radius: 5 5 0 0; -fx-background-radius: 5 5 0 0;"
        );

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        // 4 cột tỉ lệ 20/30/20/30
        grid.getColumnConstraints().addAll(
                new ColumnConstraints() {{ setPercentWidth(20); }},
                new ColumnConstraints() {{ setPercentWidth(30); }},
                new ColumnConstraints() {{ setPercentWidth(20); }},
                new ColumnConstraints() {{ setPercentWidth(30); }}
        );

        // Row 0
        grid.add(createIconLabel("Triệu chứng:", "trieuchung.png"), 0, 0);
        TextField tfSym = new TextField(r.getSymptoms()); tfSym.setEditable(false);
        grid.add(tfSym, 1, 0);
        grid.add(createIconLabel("Ngày tái khám:", "ngaytaikham.png"), 2, 0);
        TextField tfFollow = new TextField(
                r.getFollowUpDate() != null ? r.getFollowUpDate().format(dateFmt) : ""
        ); tfFollow.setEditable(false);
        grid.add(tfFollow, 3, 0);

        // Row 1
        grid.add(createIconLabel("Chẩn đoán:", "chuandoan.png"), 0, 1);
        TextField tfDiag = new TextField(r.getDiagnosis()); tfDiag.setEditable(false);
        grid.add(tfDiag, 1, 1);
        grid.add(createIconLabel("Hướng điều trị:", "huongdieutri.png"), 2, 1);
        TextField tfTreat = new TextField(r.getTreatment()); tfTreat.setEditable(false);
        grid.add(tfTreat, 3, 1);

        // Row 2
        grid.add(createIconLabel("Phác thuốc:", "phacthuoc.png"), 0, 2);
        TextField tfPres = new TextField(r.getPrescription()); tfPres.setEditable(false);
        grid.add(tfPres, 1, 2);
        grid.add(createIconLabel("Ghi chú:", "notes.png"), 2, 2);
        TextField tfNote = new TextField(r.getNote()); tfNote.setEditable(false);
        grid.add(tfNote, 3, 2);

        card.getChildren().addAll(lblDate, grid);
        return card;
    }
}
