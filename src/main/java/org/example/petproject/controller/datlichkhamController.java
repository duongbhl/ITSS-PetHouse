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
import org.example.petproject.model.Appointment;
import org.example.petproject.service.UserService;
import org.example.petproject.util.SessionManager;

import java.io.IOException;
import java.net.URL;

public class datlichkhamController {

    UserService userService = new UserService();

    private PetDAO petDAO = new PetDAO();
    private UserDAO userDao=new UserDAO();


    private String ownerID= SessionManager.getCurrentUser().getUserId();
    @FXML
    private Label ownerName;

    @FXML
    private ComboBox<String> petSelected;

    @FXML
    private ComboBox<String> examSelected;

    @FXML
    private DatePicker scheduleSelected;


    @FXML
    void bookAppointmentButton(ActionEvent event) {
        userService.datlichkham(petSelected.getValue(),scheduleSelected.getValue(),examSelected.getValue(), Appointment.Status.pending);
    }

    @FXML
    void arrowpressedButton(ActionEvent evt) {
        String fxmlPath="/org/example/petproject/owner_dashboard.fxml";
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
    void initialize() {
        petSelected.setItems(FXCollections.observableArrayList(petDAO.findAllByOwnerId(this.ownerID)));
        examSelected.setItems(FXCollections.observableArrayList("Kham tong quat", "Tiem phong", "Sieu am"));
        ownerName.setText(userDao.getUserByOwnerID(this.ownerID).getFullName());


    }

}
