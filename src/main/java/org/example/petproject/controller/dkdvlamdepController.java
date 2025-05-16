package org.example.petproject.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.dao.PetDAO;
import org.example.petproject.model.ServiceBooking;
import org.example.petproject.model.User;
import org.example.petproject.service.UserService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class dkdvlamdepController implements DashboardControllerBase, Initializable {
    @FXML
    private ImageView imgLogo;
    @FXML
    private Label ownerName;
    @FXML
    private ImageView ownerAvatar;

    @FXML
    private ComboBox<String> petSelected;
    @FXML
    private CheckBox tamSelected;
    @FXML
    private CheckBox catSelected;
    @FXML
    private CheckBox vstaiSelected;
    @FXML
    private CheckBox vsmongSelected;
    @FXML
    private ImageView imgTam;
    @FXML
    private ImageView imgCat;
    @FXML
    private ImageView imgVstai;
    @FXML
    private ImageView imgVsmong;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private DatePicker scheduleSelected;
    @FXML
    private TextArea noteBox;
    @FXML
    private Button bookAppointmentButton;

    private User currentUser;
    private final PetDAO petDAO = new PetDAO();
    private final UserService userService = new UserService(); // Initialize userService

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Price calculation listeners
        ChangeListener<Boolean> priceListener = (obs, old, val) -> updateTotalPrice();
        tamSelected.selectedProperty().addListener(priceListener);
        catSelected.selectedProperty().addListener(priceListener);
        vstaiSelected.selectedProperty().addListener(priceListener);
        vsmongSelected.selectedProperty().addListener(priceListener);

        // Disable book until form valid
        bookAppointmentButton.setDisable(true);
        ChangeListener<Object> formChecker = (obs, old, val) -> {
            boolean valid = petSelected.getValue() != null && scheduleSelected.getValue() != null;
            bookAppointmentButton.setDisable(!valid);
        };
        petSelected.valueProperty().addListener(formChecker);
        scheduleSelected.valueProperty().addListener(formChecker);
    }

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        ownerName.setText(user.getFullName());

        // Avatar
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
            ownerAvatar.setImage(new Image(user.getAvatarUrl()));
        } else {
            URL defaultAvatar = getClass().getResource("/assets/icons/user.png");
            if (defaultAvatar != null)
                ownerAvatar.setImage(new Image(defaultAvatar.toExternalForm()));
        }
        // Logo
        URL logo = getClass().getResource("/assets/logo.png");
        if (logo != null)
            imgLogo.setImage(new Image(logo.toExternalForm()));

        // Pets
        List<String> pets = petDAO.findAllByOwnerId(user.getUserId());
        petSelected.setItems(FXCollections.observableArrayList(pets));

        // Initial price
        updateTotalPrice();

        // Service icons
        setServiceIcon(imgTam, "/assets/icons/grooming.png");
        setServiceIcon(imgCat, "/assets/icons/cut.png");
        setServiceIcon(imgVstai, "/assets/icons/vstai.png");
        setServiceIcon(imgVsmong, "/assets/icons/nail-clipper.png");
    }

    private void setServiceIcon(ImageView iv, String path) {
        URL url = getClass().getResource(path);
        if (url != null)
            iv.setImage(new Image(url.toExternalForm()));
    }

    @FXML
    private void handleLogoClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/owner_dashboard.fxml"));
            Parent root = loader.load();
            DashboardControllerBase ctrl = loader.getController();
            ctrl.initUser(currentUser);
            Scene scene = ((ImageView) event.getSource()).getScene();
            root.getStylesheets().setAll(scene.getStylesheets());
            scene.setRoot(root);
            ((Stage) scene.getWindow()).setMaximized(true);
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Không thể quay về Dashboard: " + ex.getMessage())
                    .showAndWait();
        }
    }

    @FXML
    private void bookAppointmentButton(ActionEvent event) {
        if (petSelected.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn thú cưng.").showAndWait();
            return;
        }
        if (scheduleSelected.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn ngày.").showAndWait();
            return;
        }
        try {
            // Chuẩn bị dữ liệu
            LocalDate checkIn = scheduleSelected.getValue();
            String note = noteBox.getText();
            String petName = petSelected.getValue();
            // ID service làm đẹp của bạn (check lại trong DB, ví dụ "GROOM001")
            String serviceId = "GROOM001";

            // Gọi UserService để lưu booking
            userService.dkdvvesinh(
                    checkIn,
                    note,
                    ServiceBooking.Status.in_progress, // hoặc pending tuỳ nghiệp vụ
                    petName,
                    serviceId);

            new Alert(Alert.AlertType.INFORMATION, "Đăng ký dịch vụ thành công!").showAndWait();
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Đăng ký thất bại:\n" + ex.getMessage()).showAndWait();
        }
    }

    private void updateTotalPrice() {
        double sum = 0;
        sum += tamSelected.isSelected() ? 50000 : 0;
        sum += catSelected.isSelected() ? 50000 : 0;
        sum += vstaiSelected.isSelected() ? 50000 : 0;
        sum += vsmongSelected.isSelected() ? 50000 : 0;
        String fmt = String.format(Locale.GERMAN, "%,.0f VNĐ", sum).replace('.', ',');
        totalPriceLabel.setText(fmt);
    }

    private void clearForm() {
        petSelected.getSelectionModel().clearSelection();
        tamSelected.setSelected(false);
        catSelected.setSelected(false);
        vstaiSelected.setSelected(false);
        vsmongSelected.setSelected(false);
        scheduleSelected.setValue(null);
        noteBox.clear();
        updateTotalPrice();
    }
}
