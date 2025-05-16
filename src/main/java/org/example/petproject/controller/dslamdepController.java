package org.example.petproject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.controller.Dashboard.OwnerDashboardController;
import org.example.petproject.dao.ServiceBookingDAO;
import org.example.petproject.dao.ServiceDAO;
import org.example.petproject.model.PetBoardingInfo;
import org.example.petproject.model.Service;
import org.example.petproject.model.ServiceBooking;
import org.example.petproject.model.User;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class dslamdepController implements DashboardControllerBase, Initializable {

    @FXML
    private ImageView imgLogo;
    @FXML
    private Label ownerName;
    @FXML
    private ImageView ownerAvatar;
    @FXML
    private FlowPane cardsContainer;
    @FXML
    private Button addButton; // nút + với graphic

    private User currentUser;
    private final ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load icon plus.png cho nút addButton
        Image plusIcon = new Image(
                getClass().getResource("/assets/icons/plus.png").toExternalForm());
        ImageView iv = new ImageView(plusIcon);
        iv.setFitWidth(32);
        iv.setFitHeight(32);
        addButton.setGraphic(iv);

        // (Optional) bạn có thể set tooltip
        addButton.setTooltip(new Tooltip("Thêm dịch vụ làm đẹp & vệ sinh"));
    }

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        ownerName.setText(user.getFullName());

        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
            ownerAvatar.setImage(new Image(user.getAvatarUrl()));
        } else if (user.getAvatarUrl() == null) {
            var avatar = getClass().getResource("/assets/icons/user.png");
            if (avatar != null) {
                ownerAvatar.setImage(new Image(avatar.toExternalForm()));
            }
        }
        URL logoUrl = getClass().getResource("/assets/logo.png");
        if (logoUrl != null) {
            imgLogo.setImage(new Image(logoUrl.toExternalForm()));
        }

        loadPetData();
    }

    @FXML
    private void handleLogoClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/owner_dashboard.fxml"));
            Parent root = loader.load();
            OwnerDashboardController dash = loader.getController();
            dash.initUser(currentUser);

            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);

        } catch (IOException e) {
            showError("Không thể quay về Dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddCard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/dkdvlamdepScreeen.fxml"));
            Parent root = loader.load();
            dkdvlamdepController form = loader.getController();
            form.initUser(currentUser);

            Stage dialog = new Stage();
            dialog.setTitle("Đăng ký dịch vụ làm đẹp & vệ sinh");
            dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
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

    private void loadPetData() {
        cardsContainer.getChildren().clear();

        // 1) Lấy service "Làm đẹp & vệ sinh"
        Service grooming = serviceDAO.findServiceByName("Làm đẹp & vệ sinh");
        System.out.println(">> grooming service: " + grooming);
        if (grooming == null) {
            System.out.println("   >> Dịch vụ 'Làm đẹp & vệ sinh' không tồn tại!");
            return;
        }
        String groomingId = grooming.getServiceId();

        // 2) Lấy danh sách booking đang in_progress cho dịch vụ này
        List<ServiceBooking> bookings = serviceBookingDAO.getBookingsByOwnerId(
                currentUser.getUserId(),
                ServiceBooking.Status.pending,
                groomingId);
        System.out.println(">> bookings found: " + bookings.size());

        // 3) Tạo card cho mỗi booking
        for (ServiceBooking sb : bookings) {
            User handler = sb.getHandledBy();
            String handlerName = handler != null ? handler.getFullName() : "Chưa phân công";
            String handlerPhone = handler != null ? handler.getPhone() : "";
            PetBoardingInfo info = new PetBoardingInfo(
                    sb.getBookingId(),
                    sb.getPet().getName(),
                    handlerName,
                    handlerPhone,
                    "", "",
                    sb.getCheckInTime().toString(),
                    sb.getCheckOutTime() != null ? sb.getCheckOutTime().toString() : "",
                    sb.getService().getPrice().toString());

            try {
                FXMLLoader l = new FXMLLoader(
                        getClass().getResource("/org/example/petproject/petCard.fxml"));
                Parent card = l.load();
                petCardController ctrl = l.getController();
                ctrl.setData(info);
                cardsContainer.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Lỗi");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
