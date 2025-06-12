package org.example.petproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.petproject.model.PetBoardingInfoJPA;
import org.example.petproject.model.Room;
import org.example.petproject.dao.PetBoardingInfoJPADAO;
import org.example.petproject.dao.RoomDAO;
import org.example.petproject.model.ServiceBooking;
import org.example.petproject.dao.ServiceBookingDAO;
import org.example.petproject.model.PetBoarding;
import org.example.petproject.dao.PetBoardingDAO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class StaffEditBoardingListDialogController {
    @FXML private DatePicker checkInDatePicker;
    @FXML private DatePicker checkOutDatePicker;
    @FXML private TextField petNameField;
    @FXML private ComboBox<Room.Type> roomTypeComboBox;
    @FXML private ComboBox<Room> roomComboBox;
    @FXML private Label priceLabel;
    @FXML private Label statusLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private PetBoardingInfoJPA booking;
    private Runnable onSaveCallback;
    private final PetBoardingInfoJPADAO boardingDAO = new PetBoardingInfoJPADAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private List<Room> allRooms;

    @FXML
    public void initialize() {
        roomTypeComboBox.setItems(javafx.collections.FXCollections.observableArrayList(Room.Type.values()));
        roomTypeComboBox.setOnAction(e -> updateRoomListAndPrice());
        roomComboBox.setOnAction(e -> updatePrice());
        allRooms = roomDAO.findAll();

        // Add cell factory for room display
        roomComboBox.setCellFactory(param -> new ListCell<Room>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null) {
                    setText(null);
                } else {
                    setText(room.getName() != null ? room.getName() : "");
                }
            }
        });

        // Set button cell to match the cell factory
        roomComboBox.setButtonCell(new ListCell<Room>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null) {
                    setText(null);
                } else {
                    setText(room.getName() != null ? room.getName() : "");
                }
            }
        });

        // Set converter to handle string conversion
        roomComboBox.setConverter(new StringConverter<Room>() {
            @Override
            public String toString(Room room) {
                return room != null ? room.getName() : "";
            }

            @Override
            public Room fromString(String string) {
                return null; // Not needed for display-only
            }
        });
    }

    public void setBooking(PetBoardingInfoJPA booking) {
        this.booking = booking;
        if (booking != null) {
            if (booking.getCheckInDate() != null)
                checkInDatePicker.setValue(booking.getCheckInDate().toLocalDate());
            if (booking.getCheckOutDate() != null)
                checkOutDatePicker.setValue(booking.getCheckOutDate().toLocalDate());
            if (booking.getPet() != null)
                petNameField.setText(booking.getPet().getName());
            
            // Set room type and room
            Room currentRoom = booking.getPetBoarding() != null ? booking.getPetBoarding().getRoom() : null;
            if (currentRoom != null) {
                roomTypeComboBox.setValue(currentRoom.getType());
                updateRoomListAndPrice();
                roomComboBox.setValue(currentRoom);
            } else {
                roomTypeComboBox.setValue(Room.Type.thuong);
                updateRoomListAndPrice();
            }
            statusLabel.setText(booking.getStatus());
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    private void updateRoomListAndPrice() {
        Room.Type selectedType = roomTypeComboBox.getValue();
        if (selectedType != null) {
            List<Room> filteredRooms = allRooms.stream()
                    .filter(r -> r.getType() == selectedType)
                    .collect(Collectors.toList());
            roomComboBox.setItems(javafx.collections.FXCollections.observableArrayList(filteredRooms));
            if (!filteredRooms.isEmpty()) {
                roomComboBox.setValue(filteredRooms.get(0));
            }
            updatePrice();
        }
    }

    private void updatePrice() {
        Room selectedRoom = roomComboBox.getValue();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();
        
        if (selectedRoom != null && selectedRoom.getPricePerDay() != null && checkIn != null && checkOut != null) {
            long days = ChronoUnit.DAYS.between(checkIn, checkOut);
            if (days > 0) {
                BigDecimal totalPrice = selectedRoom.getPricePerDay().multiply(BigDecimal.valueOf(days));
                priceLabel.setText(String.format("%,.0f VND", totalPrice.doubleValue()));
            } else {
                priceLabel.setText("0 VND");
            }
        } else {
            priceLabel.setText("");
        }
    }

    @FXML
    private void handleSave() {
        if (booking == null) return;
        
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();
        String petName = petNameField.getText();
        Room.Type roomType = roomTypeComboBox.getValue();
        Room room = roomComboBox.getValue();
        
        if (checkIn == null || checkOut == null || petName == null || petName.isBlank() || 
            roomType == null || room == null) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
            return;
        }
        
        if (checkIn.isAfter(checkOut)) {
            showAlert("Lỗi", "Ngày check-out phải sau ngày check-in!");
            return;
        }

        try {
            // Update ServiceBooking first
            ServiceBooking serviceBooking = null;
            if (booking.getPetBoarding() != null && booking.getPetBoarding().getBooking() != null) {
                serviceBooking = booking.getPetBoarding().getBooking();
                serviceBooking.setCheckInTime(checkIn);
                serviceBooking.setCheckOutTime(checkOut);
                serviceBooking.setNote("Phòng: " + room.getName() + ", Loại phòng: " + room.getType());
                serviceBooking = new ServiceBookingDAO().update(serviceBooking);
            }

            // Update PetBoarding
            PetBoarding petBoarding = null;
            if (booking.getPetBoarding() != null) {
                petBoarding = booking.getPetBoarding();
                petBoarding.setRoom(room);
                if (serviceBooking != null) {
                    petBoarding.setBooking(serviceBooking);
                }
                petBoarding = new PetBoardingDAO().update(petBoarding);
            }

            // Update PetBoardingInfoJPA
            booking.setCheckInDate(checkIn.atStartOfDay());
            booking.setCheckOutDate(checkOut.atStartOfDay());
            if (booking.getPet() != null) {
                booking.getPet().setName(petName);
            }
            if (petBoarding != null) {
                booking.setPetBoarding(petBoarding);
            }
            
            // Calculate and update price
            long days = ChronoUnit.DAYS.between(checkIn, checkOut);
            if (days > 0 && room.getPricePerDay() != null) {
                BigDecimal totalPrice = room.getPricePerDay().multiply(BigDecimal.valueOf(days));
                booking.setPrice(totalPrice.doubleValue());
            }
            
            // Update database
            booking = boardingDAO.update(booking);
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            closeDialog();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể cập nhật thông tin: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 