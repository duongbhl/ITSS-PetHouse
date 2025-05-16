package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.model.Pet;
import org.example.petproject.model.User;
import org.example.petproject.service.PetService;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class OwnerPetsController implements DashboardControllerBase, Initializable {

    // 1.a. Enum cho các kiểu sort
    private enum SortOption {
        NAME_ASC, NAME_DESC, AGE_ASC, AGE_DESC
    }

    // 1.b. Biến giữ state của sort hiện tại
    private SortOption currentSort = null;

    @FXML
    private Label lblUserName;
    @FXML
    private ImageView imgAvatar;
    @FXML
    private ImageView imgLogo;
    @FXML
    private TextField tfSearch;
    @FXML
    private Button btnSort;
    @FXML
    private FlowPane flowPets;

    private final PetService petService = new PetService();
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load logo
        InputStream logoStream = getClass().getResourceAsStream("/assets/logo.png");
        if (logoStream != null) {
            imgLogo.setImage(new Image(logoStream));
        }
        imgLogo.setOnMouseClicked(e -> navigateToHome());
        imgLogo.setCursor(javafx.scene.Cursor.HAND); // để trỏ chuột có hiệu ứng tay
        // Mỗi khi gõ hoặc xoá ký tự thì reload lại danh sách
        tfSearch.textProperty().addListener((obs, oldVal, newVal) -> loadPets());

        // khi nhấn Sort thì mở dialog chọn kiểu sắp xếp
        btnSort.setOnAction(e -> showSortDialog());

        // khi gõ tìm kiếm, reload lại
        tfSearch.textProperty().addListener((obs, o, n) -> loadPets());

    }

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        lblUserName.setText(user.getFullName());
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
            imgAvatar.setImage(new Image(user.getAvatarUrl()));
        } else {
            InputStream av = getClass().getResourceAsStream("/assets/icons/user.png");
            if (av != null)
                imgAvatar.setImage(new Image(av));
        }
        loadPets();
    }

    private void loadPets() {
        String kw = tfSearch.getText() == null
                ? ""
                : tfSearch.getText().toLowerCase().trim();

        // 1. Lấy & filter
        List<Pet> allPets = petService.findByOwnerId(currentUser.getUserId());
        List<Pet> pets = allPets.stream()
                .filter(p -> p.getName().toLowerCase().contains(kw) ||
                        p.getSpecies().toLowerCase().contains(kw))
                .collect(Collectors.toList());

        // 2. Áp dụng sort nếu đã chọn
        if (currentSort != null) {
            switch (currentSort) {
                case NAME_ASC:
                    pets.sort(Comparator.comparing(Pet::getName, String.CASE_INSENSITIVE_ORDER));
                    break;
                case NAME_DESC:
                    pets.sort(Comparator.comparing(Pet::getName, String.CASE_INSENSITIVE_ORDER).reversed());
                    break;
                case AGE_ASC: // Tuổi tăng dần: pet sinh sau => birthDate descending
                    pets.sort(Comparator.comparing(Pet::getBirthDate).reversed());
                    break;
                case AGE_DESC: // Tuổi giảm dần: pet sinh trước => birthDate ascending
                    pets.sort(Comparator.comparing(Pet::getBirthDate));
                    break;
            }
        }

        // 3. Clear và build UI
        flowPets.getChildren().clear();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Pet p : pets) {
            // 1. Thẻ ngoài giờ thành HBox để chia trái | phải
            HBox card = new HBox(12);
            card.getStyleClass().add("pet-card");
            card.setAlignment(Pos.CENTER_LEFT);

            // === LEFT: hình + nút ===
            VBox leftBox = new VBox(8);
            leftBox.setAlignment(Pos.TOP_CENTER);

            // 1.1 Ảnh thú cưng
            ImageView iv = new ImageView();
            iv.setFitWidth(80);
            iv.setFitHeight(80);
            if (p.getPhotoUrl() != null) {
                iv.setImage(new Image(p.getPhotoUrl()));
            } else {
                InputStream icon = getClass().getResourceAsStream("/assets/icons/pets.png");
                if (icon != null)
                    iv.setImage(new Image(icon));
            }

            // 1.2 Nút xem chi tiết dưới ảnh
            Button detailBtn = new Button("Xem chi tiết");
            detailBtn.getStyleClass().add("detail-button");
            detailBtn.setOnAction(e -> showDetail(p));

            leftBox.getChildren().addAll(iv, detailBtn);

            // === RIGHT: thông tin thú cưng ===
            VBox rightBox = new VBox(6);
            rightBox.setAlignment(Pos.CENTER_LEFT);

            // Tên
            Label nameLbl = new Label(p.getName());
            nameLbl.getStyleClass().add("pet-name");

            // Loài, ngày sinh
            Label speciesLbl = new Label(p.getSpecies());
            speciesLbl.getStyleClass().add("info-line");
            Label dobLbl = new Label(
                    p.getBirthDate() != null
                            ? p.getBirthDate().format(fmt)
                            : "");
            dobLbl.getStyleClass().add("info-line");
            // Giới tính với icon + nhãn getLabel()
            HBox genderBox = new HBox(4);
            genderBox.getStyleClass().add("info-box");
            Pet.Gender g = p.getGender() != null ? p.getGender() : Pet.Gender.male;
            String gIconPath = (g == Pet.Gender.female)
                    ? "/assets/icons/female.png"
                    : "/assets/icons/male.png";
            try (InputStream gStream = getClass().getResourceAsStream(gIconPath)) {
                if (gStream != null) {
                    ImageView gIcon = new ImageView(new Image(gStream));
                    gIcon.setFitWidth(14);
                    gIcon.setFitHeight(14);
                    Label gLbl = new Label(g.getLabel()); // <-- dùng getLabel()
                    gLbl.getStyleClass().add("info-line");
                    genderBox.getChildren().addAll(gIcon, gLbl);
                }
            } catch (IOException ignored) {
            }

            // Cân nặng
            HBox weightBox = new HBox(4);
            weightBox.getStyleClass().add("info-box");
            try (InputStream wStream = getClass().getResourceAsStream("/assets/icons/weight.png")) {
                if (wStream != null) {
                    ImageView wIcon = new ImageView(new Image(wStream));
                    wIcon.setFitWidth(14);
                    wIcon.setFitHeight(14);
                    Label wLbl = new Label(p.getWeight() + " kg");
                    wLbl.getStyleClass().add("info-line");
                    weightBox.getChildren().addAll(wIcon, wLbl);
                }
            } catch (IOException ignored) {
            }

            rightBox.getChildren().addAll(nameLbl, speciesLbl, dobLbl, genderBox, weightBox);

            // 3. Ghép trái + phải vào card
            card.getChildren().addAll(leftBox, rightBox);

            flowPets.getChildren().add(card);
        }

        // Add new pet card
        VBox addCard = new VBox(10);
        addCard.getStyleClass().add("pet-card");
        ImageView ivAdd = new ImageView();
        ivAdd.setFitHeight(120);
        ivAdd.setPreserveRatio(true);

        InputStream addIcon = getClass().getResourceAsStream("/assets/icons/pet_add.png");
        if (addIcon != null)
            ivAdd.setImage(new Image(addIcon));

        Button addBtn = new Button("Thêm thú cưng");
        addBtn.getStyleClass().add("add-button");
        addBtn.setOnAction(e -> openAddPetDialog());

        addCard.getChildren().addAll(ivAdd, addBtn);
        flowPets.getChildren().add(addCard);
    }

    private void openAddPetDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/AddPetView.fxml"));
            Parent root = loader.load();
            AddPetController ctrl = loader.getController();
            ctrl.setOwner(currentUser);

            // Preserve CSS
            Scene parentScene = flowPets.getScene();
            List<String> sheets = new ArrayList<>(parentScene.getStylesheets());
            Scene dialogScene = new Scene(root);
            dialogScene.getStylesheets().setAll(sheets);

            Stage dialog = new Stage();
            dialog.initOwner(parentScene.getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Thêm thú cưng");
            dialog.setScene(dialogScene);
            dialog.showAndWait();

            loadPets();
        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Không mở được form thêm thú cưng");
        }
    }

    private void showDetail(Pet pet) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/PetDetailView.fxml"));
            Parent root = loader.load();
            PetDetailController ctrl = loader.getController();

            // truyền Pet + người dùng, rồi initData()
            ctrl.setPetAndOwner(pet, currentUser);
            ctrl.initData();

            // preserve CSS
            Scene parentScene = flowPets.getScene();
            List<String> sheets = new ArrayList<>(parentScene.getStylesheets());
            Scene scene = new Scene(root);
            scene.getStylesheets().setAll(sheets);

            Stage dialog = new Stage();
            dialog.initOwner(parentScene.getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Thông tin chi tiết thú cưng");
            dialog.setScene(scene);
            dialog.showAndWait();

            // nếu có thay đổi, bạn có thể reload danh sách:
            loadPets();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể mở chi tiết thú cưng");
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showSortDialog() {
        List<String> choices = List.of(
                "Tên A → Z",
                "Tên Z → A",
                "Tuổi tăng dần",
                "Tuổi giảm dần");
        ChoiceDialog<String> dlg = new ChoiceDialog<>(choices.get(0), choices);
        dlg.setTitle("Sắp xếp thú cưng");
        dlg.setHeaderText(null);
        dlg.setContentText("Chọn cách sắp xếp:");
        Optional<String> res = dlg.showAndWait();
        res.ifPresent(sel -> {
            switch (sel) {
                case "Tên A → Z" -> currentSort = SortOption.NAME_ASC;
                case "Tên Z → A" -> currentSort = SortOption.NAME_DESC;
                case "Tuổi tăng dần" -> currentSort = SortOption.AGE_ASC;
                case "Tuổi giảm dần" -> currentSort = SortOption.AGE_DESC;
            }
            loadPets(); // reload sau khi chọn
        });
    }

    private void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/OwnerDashboardView.fxml"));
            Parent root = loader.load();

            DashboardControllerBase ctrl = loader.getController();
            ctrl.initUser(currentUser); // nếu trang chủ cần thông tin người dùng

            Scene currentScene = imgLogo.getScene();
            currentScene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể quay về trang chủ.");
        }
    }

}