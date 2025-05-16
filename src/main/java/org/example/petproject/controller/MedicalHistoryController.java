package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.model.MedicalRecord;
import org.example.petproject.model.Pet;
import org.example.petproject.model.User;
import org.example.petproject.service.MedicalRecordService;
import org.example.petproject.service.PetService;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class MedicalHistoryController implements DashboardControllerBase, Initializable {

    @FXML
    private ImageView imgLogo;
    @FXML
    private Label lblUsername;
    @FXML
    private ImageView avatarImageView;

    @FXML
    private TextField txtPetName;
    @FXML
    private Button btnSearchPet;
    @FXML
    private ComboBox<Pet> comboPets;
    @FXML
    private VBox cardContainer;

    private User currentUser;
    private final PetService petService = new PetService();
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Thiết lập ComboBox hiển thị tên Pet
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
    }

    @Override
    public void initUser(User user) {
        System.out.println(">> initUser của MedicalHistoryController, user = " + user);

        this.currentUser = user;
        // Header: tên user và avatar
        lblUsername.setText(user.getFullName());
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
            avatarImageView.setImage(new Image(user.getAvatarUrl()));
        } else if (user.getAvatarUrl() == null) {
            var avatar = getClass().getResource("/assets/icons/user.png");
            if (avatar != null) {
                avatarImageView.setImage(new Image(avatar.toExternalForm()));
            }
        }
        // Logo từ assets
        Image logo = new Image(getClass().getResource("/assets/logo.png").toExternalForm());
        imgLogo.setImage(logo);

        // Load danh sách thú cưng
        comboPets.getItems().setAll(petService.findByOwnerId(user.getUserId()));
    }

    @FXML
    private void handleLogoClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/OwnerDashboardView.fxml"));
            Parent root = loader.load();
            DashboardControllerBase ctrl = loader.getController();
            ctrl.initUser(currentUser);

            Scene scene = imgLogo.getScene();
            // giữ nguyên stylesheets
            root.getStylesheets().addAll(scene.getStylesheets());

            scene.setRoot(root);
            Stage stage = (Stage) scene.getWindow();
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchPet() {
        String kw = txtPetName.getText().trim();
        List<Pet> list = kw.isEmpty()
                ? petService.findByOwnerId(currentUser.getUserId())
                : petService.searchByName(kw, currentUser.getUserId());
        comboPets.getItems().setAll(list);
        if (list.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Không tìm thấy thú cưng.").showAndWait();
        }
    }

    @FXML
    private void handlePetSelected() {
        Pet pet = comboPets.getValue();
        if (pet == null)
            return;
        List<MedicalRecord> records = MedicalRecordService.getByPet(pet.getPetId());
        cardContainer.getChildren().clear();
        for (MedicalRecord r : records) {
            cardContainer.getChildren().add(buildCard(r));
        }
    }

    // Phương thức buildCard giữ nguyên logic cũ
    private Label createIconLabel(String text, String iconFileName) {
        ImageView iv = new ImageView(
                new Image(getClass().getResourceAsStream("/assets/icons/" + iconFileName)));
        iv.setFitWidth(20);
        iv.setFitHeight(20);
        Label lbl = new Label(text, iv);
        lbl.setContentDisplay(ContentDisplay.LEFT);
        lbl.setGraphicTextGap(8);
        return lbl;
    }

    private VBox buildCard(MedicalRecord r) {
        VBox card = new VBox();
        card.setStyle(
                "-fx-border-color: #BDBDBD; -fx-border-radius: 5; " +
                        "-fx-background-color: white; -fx-background-radius: 5;");

        Label lblDate = new Label(r.getExamDate().format(dateFmt));
        lblDate.setStyle(
                "-fx-background-color: #EEEEEE; -fx-font-weight: bold; " +
                        "-fx-padding: 5; -fx-border-color: #BDBDBD; " +
                        "-fx-border-radius: 5 5 0 0; -fx-background-radius: 5 5 0 0;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.getColumnConstraints().addAll(
                new ColumnConstraints() {
                    {
                        setPercentWidth(20);
                    }
                },
                new ColumnConstraints() {
                    {
                        setPercentWidth(30);
                    }
                },
                new ColumnConstraints() {
                    {
                        setPercentWidth(20);
                    }
                },
                new ColumnConstraints() {
                    {
                        setPercentWidth(30);
                    }
                });

        // Row 0
        grid.add(createIconLabel("Triệu chứng:", "trieuchung.png"), 0, 0);
        TextField tfSym = new TextField(r.getSymptoms());
        tfSym.setEditable(false);
        grid.add(tfSym, 1, 0);
        grid.add(createIconLabel("Ngày tái khám:", "ngaytaikham.png"), 2, 0);
        TextField tfFollow = new TextField(
                r.getFollowUpDate() != null ? r.getFollowUpDate().format(dateFmt) : "");
        tfFollow.setEditable(false);
        grid.add(tfFollow, 3, 0);

        // Row 1
        grid.add(createIconLabel("Chẩn đoán:", "chuandoan.png"), 0, 1);
        TextField tfDiag = new TextField(r.getDiagnosis());
        tfDiag.setEditable(false);
        grid.add(tfDiag, 1, 1);
        grid.add(createIconLabel("Hướng điều trị:", "huongdieutri.png"), 2, 1);
        TextField tfTreat = new TextField(r.getTreatment());
        tfTreat.setEditable(false);
        grid.add(tfTreat, 3, 1);

        // Row 2
        grid.add(createIconLabel("Phác thuốc:", "phacthuoc.png"), 0, 2);
        TextField tfPres = new TextField(r.getPrescription());
        tfPres.setEditable(false);
        grid.add(tfPres, 1, 2);
        grid.add(createIconLabel("Ghi chú:", "notes.png"), 2, 2);
        TextField tfNote = new TextField(r.getNote());
        tfNote.setEditable(false);
        grid.add(tfNote, 3, 2);

        card.getChildren().addAll(lblDate, grid);
        return card;
    }
}