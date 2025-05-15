package org.example.petproject.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.dao.PetDAO;
import org.example.petproject.dao.UserDAO;
import org.example.petproject.model.ServiceBooking;
import org.example.petproject.service.UserService;
import org.example.petproject.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class dkdvlamdepController {

    UserDAO userDAO = new UserDAO();
    PetDAO petDAO = new PetDAO();

    UserService userService = new UserService();

    private String ownerID= SessionManager.getCurrentUser().getUserId();


    @FXML
    private Label ownerName;

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
    private Label totalPrice;

    @FXML
    private DatePicker scheduleSelected;

    @FXML
    private TextArea noteBox;

    @FXML
    void arrowpressedButton(ActionEvent evt) {
        String fxmlPath="/org/example/petproject/dslamdepScreen.fxml";
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if (fxmlUrl == null) {
            showError("Không tìm thấy màn hình " + fxmlPath);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent newRoot = loader.load();

            // initUser nếu cần
            Object ctrl = loader.getController();
            if (ctrl instanceof DashboardControllerBase dcb) {
                dcb.initUser(SessionManager.getCurrentUser());
            }

            // Lấy Scene hiện tại từ bất kỳ node nào (ví dụ button)
            Scene scene = ((Node) evt.getSource()).getScene();
            // Chuyển root thành root mới
            scene.setRoot(newRoot);

            // Nếu muốn, vẫn có thể maximize stage
            Stage stage = (Stage) scene.getWindow();
            stage.setMaximized(true);

        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Lỗi khi mở màn hình: " + fxmlPath);
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Lỗi");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    void bookAppointmentButton(ActionEvent event) {
        userService.dkdvvesinh(scheduleSelected.getValue(),noteBox.getText(), ServiceBooking.Status.pending,petSelected.getValue(),"S001");
    }

    @FXML
    void initialize() {
        ownerName.setText(userDAO.getUserByOwnerID(this.ownerID).getFullName());
        petSelected.setItems(FXCollections.observableArrayList(petDAO.findAllByOwnerId(this.ownerID)));
        tamSelected.setOnAction(event -> updatetotalprice());
        catSelected.setOnAction(event -> updatetotalprice());
        vstaiSelected.setOnAction(event -> updatetotalprice());
        vsmongSelected.setOnAction(event -> updatetotalprice());
    }

    private void updatetotalprice() {
        double totalprice = 0;
        if(tamSelected.isSelected()) totalprice+=50000;
        if(catSelected.isSelected()) totalprice+=50000;
        if(vstaiSelected.isSelected()) totalprice+=50000;
        if(vsmongSelected.isSelected()) totalprice+=50000;

        String formattedPrice = String.format(Locale.GERMAN, "%,.0f VNĐ", totalprice).replace('.', ','); // GERMAN uses . as thousands separator
        if (formattedPrice.endsWith(", VNĐ")) { // Handle cases like 220,000, VNĐ
            formattedPrice = formattedPrice.replace(", VNĐ", " VNĐ");
        }
        // A simpler approach for the exact format "XXX.XXX VNĐ"
        formattedPrice = String.format("%,d VNĐ", (int)totalprice).replace(",", ".");

        totalPrice.setText(formattedPrice);
    }

}
