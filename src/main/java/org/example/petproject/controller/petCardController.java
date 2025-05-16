package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.petproject.dao.ServiceBookingDAO;
import org.example.petproject.model.PetBoardingInfo;
import org.example.petproject.model.ServiceBooking;

public class petCardController {

    private final ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();

    @FXML
    private ImageView imgPet;
    @FXML
    private Label lblPetName;
    @FXML
    private Label lblServiceType;
    @FXML
    private Label lblEmpName;
    @FXML
    private Label lblEmpPhone;
    @FXML
    private TextArea txtServiceInfo;
    @FXML
    private Label lblPrice;

    public void setData(PetBoardingInfo info) {
        // 1) Hiển petName
        lblPetName.setText(info.getPetName());

        // 2) Lấy entity ServiceBooking đầy đủ từ DB
        ServiceBooking sb = serviceBookingDAO.findBookingById(info.getbookingId());
        if (sb != null) {
            // 2a) Ảnh pet
            String imgUrl = sb.getPet().getPhotoUrl(); // hoặc getAvatarUrl tuỳ entity
            if (imgUrl != null && !imgUrl.isBlank()) {
                imgPet.setImage(new Image(imgUrl, true));
            }
            // 2b) Loại dịch vụ
            lblServiceType.setText(sb.getService().getName());

            // 2c) Nhân viên phụ trách (có thể null)
            if (sb.getHandledBy() != null) {
                lblEmpName.setText(sb.getHandledBy().getFullName());
                lblEmpPhone.setText(sb.getHandledBy().getPhone());
            } else {
                lblEmpName.setText("Chưa phân công");
                lblEmpPhone.setText("");
            }

            // 2d) Mô tả chi tiết: bạn có thể build chuỗi từ sb.getService().getType(),
            // sb.getCheckInTime(),...
            StringBuilder detail = new StringBuilder();
            detail.append("Bắt đầu: ").append(sb.getCheckInTime());
            if (sb.getCheckOutTime() != null) {
                detail.append("\nKết thúc: ").append(sb.getCheckOutTime());
            }
            txtServiceInfo.setText(detail.toString());

            // 2e) Giá
            lblPrice.setText(
                    String.format("%,.0f VNĐ", sb.getService().getPrice())
                            .replace('.', ','));
        }
    }
}
