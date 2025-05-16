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
import java.util.Locale;
import java.util.ResourceBundle;

public class OwnerGroomingController implements DashboardControllerBase, Initializable {

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
                    getClass().getResource("/org/example/petproject/OwnerDashboardView.fxml"));
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
                    getClass().getResource("/org/example/petproject/RegisterGroomingView.fxml"));
            Parent root = loader.load();
            RegisterGroomingController form = loader.getController();
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

        Service grooming = serviceDAO.findByType(Service.Type.lam_dep_va_ve_sinh);
        if (grooming == null) {
            showError("Dịch vụ làm đẹp & vệ sinh không tồn tại!");
            return;
        }
        String groomingId = grooming.getServiceId();

        List<ServiceBooking> bookings = serviceBookingDAO.getBookingsByOwnerId(
                currentUser.getUserId(),
                ServiceBooking.Status.in_progress,
                groomingId);

        for (ServiceBooking sb : bookings) {
            User handler = sb.getHandledBy();
            String handlerName = handler != null ? handler.getFullName() : "Chưa phân công";
            String handlerPhone = handler != null ? handler.getPhone() : "";

            // Lấy note làm detail (đã lưu chuỗi checkbox + tổng giá)
            String detail = sb.getNote() != null ? sb.getNote() : "";

            // Tên dịch vụ chung ("Làm đẹp & vệ sinh")
            String serviceType = grooming.getName();

            // Giá tổng (nếu bạn lưu trong note thì parse, hoặc dùng service.price)
            String price = String.format(Locale.GERMAN, "%,.0f", (double) /* tổng giá */ 0)
                    .replace('.', ',') + " VNĐ";
            // nếu bạn đã thêm total_price, dùng sb.getTotalPrice()

            PetBoardingInfo info = new PetBoardingInfo(
                    sb.getBookingId(),
                    sb.getPet().getName(),
                    serviceType,
                    handlerName,
                    handlerPhone,
                    /* roomName */"",
                    sb.getCheckInTime().toString(),
                    sb.getCheckOutTime() != null ? sb.getCheckOutTime().toString() : "",
                    detail,
                    price);

            FXMLLoader l = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/PetCardView.fxml"));
            try {
                Parent card = l.load();
                PetCardController ctrl = l.getController();
                ctrl.setData(info);
                cardsContainer.getChildren().add(card);
            } catch (IOException e) {
                showError("Không thể tải thẻ thú cưng.");
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