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
import org.example.petproject.dao.RoomDAO;
import org.example.petproject.dao.UserDAO;
import org.example.petproject.model.ServiceBooking;
import org.example.petproject.service.UserService;
import org.example.petproject.util.SessionManager;

import java.io.IOException;
import java.net.URL;


public class dkdvluutruController {
    UserDAO userdao = new UserDAO();
    PetDAO petdao = new PetDAO();
    RoomDAO roomdao = new RoomDAO();

    UserService userservice = new UserService();

    private String ownerID= SessionManager.getCurrentUser().getUserId();


    @FXML
    private Label ownerName;

    @FXML
    private ComboBox<String> petSelected;

    @FXML
    private DatePicker inscheduleSelected;

    @FXML
    private DatePicker outscheduleSelected;

    @FXML
    private ComboBox<String> roomSelected;

    @FXML
    private TextField noteText;

    @FXML
    void arrowPressedButton(ActionEvent evt) {
        String fxmlPath="/org/example/petproject/dsluutruScreen.fxml";
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
        userservice.dkdvluutru(inscheduleSelected.getValue(),
                outscheduleSelected.getValue(),
                noteText.getText(),
                ServiceBooking.Status.pending,
                petSelected.getValue(),
                "S002",
                roomSelected.getValue());
    }

    @FXML
    void initialize(){
        ownerName.setText(userdao.getUserByOwnerID(this.ownerID).getFullName());
        petSelected.setItems(FXCollections.observableArrayList(petdao.findAllByOwnerId(this.ownerID)));
        roomSelected.setItems(FXCollections.observableArrayList(roomdao.findAllNames()));

    }


}
