package org.example.petproject.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.example.petproject.controller.Dashboard.DashboardControllerBase;
import org.example.petproject.controller.Dashboard.StaffDashboardController;
import org.example.petproject.dao.PetBoardingInfoJPADAO;
import org.example.petproject.dao.PetBoardingDAO;
import org.example.petproject.dao.ServiceBookingDAO;
import org.example.petproject.dao.RoomDAO;
import org.example.petproject.model.PetBoardingInfoJPA;
import org.example.petproject.model.PetBoarding;
import org.example.petproject.model.User;
import org.example.petproject.model.ServiceBooking;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class StaffBoardingListController implements Initializable, DashboardControllerBase {

    @FXML
    private TableView<BoardingWrapper> boardingTableView;
    @FXML
    private TableColumn<BoardingWrapper, Boolean> selectColumn;
    @FXML
    private TableColumn<BoardingWrapper, LocalDateTime> checkInColumn;
    @FXML
    private TableColumn<BoardingWrapper, LocalDateTime> checkOutColumn;
    @FXML
    private TableColumn<BoardingWrapper, String> petNameColumn;
    @FXML
    private TableColumn<BoardingWrapper, String> roomNameColumn;
    @FXML
    private TableColumn<BoardingWrapper, String> roomTypeColumn;
    @FXML
    private TableColumn<BoardingWrapper, String> statusColumn;
    @FXML
    private TableColumn<BoardingWrapper, Double> priceColumn;
    @FXML
    private TableColumn<BoardingWrapper, Void> editColumn;
    @FXML
    private ImageView logoImageView;
    @FXML
    private ImageView userAvatarImageView;
    @FXML
    private Label userNameLabel;
    @FXML
    private Button backButton;

    private User currentUser;
    private PetBoardingInfoJPADAO petBoardingInfoJPADAO;
    private ObservableList<BoardingWrapper> boardingData;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Wrapper class to handle selection state
    public static class BoardingWrapper {
        private final PetBoardingInfoJPA boarding;
        private final SimpleBooleanProperty selected = new SimpleBooleanProperty(false);

        public BoardingWrapper(PetBoardingInfoJPA boarding) {
            this.boarding = boarding;
        }

        public PetBoardingInfoJPA getBoarding() {
            return boarding;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        public SimpleBooleanProperty selectedProperty() {
            return selected;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        petBoardingInfoJPADAO = new PetBoardingInfoJPADAO();

        // Initialize table columns
        setupTableColumns();

        // Load logo image
        try {
            logoImageView.setImage(new Image(getClass().getResourceAsStream("/assets/logo.png")));
        } catch (Exception e) {
            System.err.println("Lỗi tải logo : " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        // Setup select column with checkbox
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(col -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();
            {
                checkBox.setAlignment(Pos.CENTER);
                checkBox.setOnAction(e -> {
                    BoardingWrapper wrapper = getTableRow().getItem();
                    if (wrapper != null) {
                        wrapper.setSelected(checkBox.isSelected());
                    }
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                }
            }
        });
        selectColumn.setEditable(true);

        // Setup for datetime columns with custom formatting
        checkInColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBoarding().getCheckInDate()));
        checkInColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(DATE_FORMATTER.format(date));
                }
            }
        });

        checkOutColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBoarding().getCheckOutDate()));
        checkOutColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(DATE_FORMATTER.format(date));
                }
            }
        });

        // Setup for string columns
        petNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBoarding().getPetName()));
        roomNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBoarding().getRoomName()));
        roomTypeColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getBoarding().getRoomType() != null) {
                return new SimpleStringProperty(cellData.getValue().getBoarding().getRoomType().name());
            }
            return new SimpleStringProperty("");
        });

        // Setup status column without colors
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBoarding().getStatus()));

        // Setup for price column with currency formatting
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                cellData.getValue().getBoarding().getPrice() != null ? cellData.getValue().getBoarding().getPrice() : 0.0).asObject());
        priceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f VND", price));
                }
            }
        });

        // Add edit column
        editColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Chỉnh sửa");
            {
                editButton.setOnAction(e -> {
                    BoardingWrapper wrapper = getTableRow().getItem();
                    if (wrapper != null) {
                        showEditDialog(wrapper.getBoarding());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
    }

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        if (userNameLabel != null) {
            userNameLabel.setText(user.getFullName());
        }

        if (userAvatarImageView != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
            try {
                userAvatarImageView.setImage(new Image(user.getAvatarUrl()));
            } catch (Exception e) {
                System.err.println("Could not load user avatar: " + e.getMessage());
            }
        }
    }

    public void initData(List<PetBoardingInfoJPA> boardingInfoList) {
        // Create observable list and set it to table
        boardingData = FXCollections.observableArrayList();
        boardingInfoList.forEach(boarding -> boardingData.add(new BoardingWrapper(boarding)));
        boardingTableView.setItems(boardingData);

        // Add selection handler for row details
        boardingTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        System.out.println("Selected boarding info: " + newSelection.getBoarding().getInfoId());
                        // Can add more detailed view logic here
                    }
                });
                
        // Refresh data to ensure latest records are shown
        refreshTableData();
    }

    @FXML
    public void handleBackAction(ActionEvent event) {
        try {
            // Navigate back to the management screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/petproject/StaffBoardingManagementView.fxml"));
            Parent root = loader.load();

            StaffBoardingManagementController controller = loader.getController();
            controller.initUser(currentUser);

            Stage stage = (Stage) boardingTableView.getScene().getWindow();

            // Get the current scene
            Scene scene = stage.getScene();

            // Instead of creating a new scene, just set the root of the existing scene
            // This helps maintain the state of the interface
            if (scene != null) {
                scene.setRoot(root);
            } else {
                // If there's no scene (unlikely), create a new one
                scene = new Scene(root);
                stage.setScene(scene);
            }
            stage.setTitle("Pet Boarding Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not return to management view: " + e.getMessage());
        }
    }

    @FXML
    public void handleConfirmAction(ActionEvent actionEvent) {
        List<BoardingWrapper> selectedItems = boardingData.stream()
                .filter(BoardingWrapper::isSelected)
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            showAlert("Yêu cầu chọn", "Vui lòng chọn ít nhất một lịch hẹn để xác nhận.");
            return;
        }

        for (BoardingWrapper wrapper : selectedItems) {
            PetBoardingInfoJPA boarding = wrapper.getBoarding();
            if ("pending".equals(boarding.getStatus())) {
                boarding.setStatus("in_progress");
                petBoardingInfoJPADAO.update(boarding);
            }
        }

        // Refresh table data
        refreshTableData();
        showAlert("Thành công", "Đã xác nhận các lịch hẹn đã chọn!");
    }

    @FXML
    public void handleRejectAction(ActionEvent actionEvent) {
        List<BoardingWrapper> selectedItems = boardingData.stream()
                .filter(BoardingWrapper::isSelected)
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            showAlert("Yêu cầu chọn", "Vui lòng chọn ít nhất một lịch hẹn để từ chối.");
            return;
        }

        // Confirm rejection
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận từ chối");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn hủy các lịch hẹn này?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                for (BoardingWrapper wrapper : selectedItems) {
                    PetBoardingInfoJPA boarding = wrapper.getBoarding();
                    boarding.setStatus("cancelled");
                    petBoardingInfoJPADAO.update(boarding);
                }
                // Refresh table data
                refreshTableData();
                showAlert("Thành công", "Đã hủy các lịch hẹn đã chọn!");
            }
        });
    }

    void refreshTableData() {
        try {
            // Get all current boarding records from database
            List<PetBoardingInfoJPA> allBoardings = petBoardingInfoJPADAO.findAll();
            
            // Get all PetBoarding records
            PetBoardingDAO petBoardingDAO = new PetBoardingDAO();
            List<PetBoarding> petBoardings = petBoardingDAO.findAll();
            
            // Update PetBoardingInfoJPA records based on PetBoarding
            for (PetBoarding pb : petBoardings) {
                boolean exists = false;
                PetBoardingInfoJPA existingInfo = null;
                
                // Check if PetBoardingInfoJPA record exists
                for (PetBoardingInfoJPA info : allBoardings) {
                    if (info.getPetBoarding() != null && 
                        info.getPetBoarding().getBoardingId().equals(pb.getBoardingId())) {
                        exists = true;
                        existingInfo = info;
                        break;
                    }
                }
                
                if (exists) {
                    // Update existing record with latest dates from ServiceBooking
                    if (pb.getBooking() != null) {
                        existingInfo.setCheckInDate(pb.getBooking().getCheckInTime().atStartOfDay());
                        existingInfo.setCheckOutDate(pb.getBooking().getCheckOutTime().atStartOfDay());
                        existingInfo.setNote(pb.getBooking().getNote());
                        petBoardingInfoJPADAO.update(existingInfo);
                    }
                } else {
                    // Create new PetBoardingInfoJPA record
                    PetBoardingInfoJPA newInfo = new PetBoardingInfoJPA();
                    newInfo.setPetBoarding(pb);
                    newInfo.setStatus("pending");
                    
                    // Set dates and note from ServiceBooking
                    if (pb.getBooking() != null) {
                        newInfo.setCheckInDate(pb.getBooking().getCheckInTime().atStartOfDay());
                        newInfo.setCheckOutDate(pb.getBooking().getCheckOutTime().atStartOfDay());
                        newInfo.setNote(pb.getBooking().getNote());
                        if (pb.getBooking().getPet() != null) {
                            newInfo.setPet(pb.getBooking().getPet());
                        }
                    }
                    
                    // Set price from room
                    if (pb.getRoom() != null) {
                        newInfo.setPrice(pb.getRoom().getPricePerDay().doubleValue());
                    }
                    
                    petBoardingInfoJPADAO.save(newInfo);
                }
            }
            
            // Get updated list after syncing
            allBoardings = petBoardingInfoJPADAO.findAll();
            
            // Clear existing data
            boardingData.clear();
            
            // Add all current records to the table
            allBoardings.forEach(boarding -> boardingData.add(new BoardingWrapper(boarding)));
            
            // Force table refresh
            boardingTableView.refresh();
            
            // Log for debugging
            System.out.println("Refreshed table with " + allBoardings.size() + " records");
        } catch (Exception e) {
            System.err.println("Error refreshing table data: " + e.getMessage());
            e.printStackTrace();
            showAlert("Lỗi", "Không thể cập nhật dữ liệu: " + e.getMessage());
        }
    }

    private void showAssignRoomDialog(PetBoardingInfoJPA boarding) {
        // Room assignment dialog implementation would go here
        // This is simplified for the example
        showAlert("Phân phòng", "Chức năng phân phòng sẽ được triển khai tại đây.");
    }

    private void showEditDialog(PetBoardingInfoJPA booking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/petproject/StaffEditBoardingListDialog.fxml"));
            DialogPane dialogPane = loader.load();
            StaffEditBoardingListDialogController controller = loader.getController();
            controller.setBooking(booking);
            controller.setOnSaveCallback(() -> {
                // Update ServiceBooking
                ServiceBooking serviceBooking = booking.getPetBoarding().getBooking();
                serviceBooking.setCheckInTime(booking.getCheckInDate().toLocalDate());
                serviceBooking.setCheckOutTime(booking.getCheckOutDate().toLocalDate());
                serviceBooking.setNote(booking.getNote());
                serviceBooking.setStatus(ServiceBooking.Status.valueOf(booking.getStatus().toUpperCase()));
                new ServiceBookingDAO().update(serviceBooking);

                // Update PetBoarding
                PetBoarding petBoarding = booking.getPetBoarding();
                petBoarding.setRoom(new RoomDAO().findByName(booking.getRoomName()));
                new PetBoardingDAO().update(petBoarding);

                // Update PetBoardingInfoJPA
                new PetBoardingInfoJPADAO().update(booking);

                // Refresh table data
                refreshTableData();
            });

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Chỉnh sửa thông tin lưu trú");
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở hộp thoại chỉnh sửa: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleLogoClick(MouseEvent event) {
        try {
            // Load the staff dashboard view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/petproject/StaffDashboardView.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the current user
            StaffDashboardController controller = loader.getController();
            controller.initUser(currentUser);

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Get the current scene
            Scene scene = stage.getScene();

            // Instead of creating a new scene, just set the root of the existing scene
            // This helps maintain the state of the interface
            if (scene != null) {
                scene.setRoot(root);
            } else {
                // If there's no scene (unlikely), create a new one
                scene = new Scene(root);
                stage.setScene(scene);
            }

            stage.setTitle("Staff Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải giao diện bảng điều khiển: " + e.getMessage());
        }
    }
}
