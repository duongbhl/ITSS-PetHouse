package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.example.petproject.dao.ServiceBookingDAO;
import org.example.petproject.model.PetBoardingInfo;
import org.example.petproject.model.ServiceBooking;

import java.net.URL;

/**
 * Controller cho ô hiển thị dịch vụ làm đẹp & vệ sinh.
 */
public class PetCardController {

    @FXML
    private ImageView imgPet;
    @FXML
    private Label lblPetName;
    @FXML
    private Label lblEmpName;
    @FXML
    private Label lblEmpPhone;
    @FXML
    private TextArea txtServiceInfo;
    @FXML
    private Label lblPrice;

    /**
     * Thiết lập dữ liệu cho card.
     */
    public void setData(PetBoardingInfo info) {
        // Lấy đầy đủ booking từ DB để lấy ảnh
        ServiceBooking sb = new ServiceBookingDAO().findBookingById(info.getBookingId());
        String imgUrl = null;
        if (sb != null && sb.getPet().getPhotoUrl() != null && !sb.getPet().getPhotoUrl().isBlank()) {
            imgUrl = sb.getPet().getPhotoUrl();
        }
        // Ảnh pet, nếu null thì dùng default
        if (imgUrl != null) {
            imgPet.setImage(new Image(imgUrl, true));
        } else {
            URL defaultUrl = getClass().getResource("/assets/icons/pets.png");
            if (defaultUrl != null) {
                imgPet.setImage(new Image(defaultUrl.toExternalForm(), true));
            }
        }

        // Tên thú cưng
        lblPetName.setText(info.getPetName());

        // Nhân viên phụ trách
        lblEmpName.setText(info.getStaffName());
        lblEmpPhone.setText(info.getStaffPhone());

        // Nội dung chi tiết: các dịch vụ con & ghi chú
        txtServiceInfo.setText(info.getDetail());
    }
}