package org.example.petproject.controller.Dashboard;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.petproject.dao.PetBoardingInfoJPADAO;
import org.example.petproject.model.PetBoardingInfoJPA;
import org.example.petproject.model.User;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
public class StaffBoardingListController implements Initializable, DashboardControllerBase {

    @FXML
    private TableView<PetBoardingInfoJPA> boardingTableView;
    @FXML
    private TableColumn<PetBoardingInfoJPA, LocalDateTime> checkInColumn;
    @FXML
    private TableColumn<PetBoardingInfoJPA, LocalDateTime> checkOutColumn;
    @FXML
    private TableColumn<PetBoardingInfoJPA, String> petNameColumn;
    @FXML
    private TableColumn<PetBoardingInfoJPA, String> roomNameColumn;
    @FXML
    private TableColumn<PetBoardingInfoJPA, String> roomTypeColumn;
    @FXML
    private TableColumn<PetBoardingInfoJPA, String> statusColumn;
    @FXML
    private TableColumn<PetBoardingInfoJPA, Double> priceColumn;

    @FXML
    private Label userNameLabel;
    @FXML
    private ImageView userAvatarImageView;
    @FXML
    private ImageView logoImageView;
    @FXML
    private Button backButton;

    private User currentUser;
    private PetBoardingInfoJPADAO petBoardingInfoJPADAO;
    private ObservableList<PetBoardingInfoJPA> boardingData;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        petBoardingInfoJPADAO = new PetBoardingInfoJPADAO();

        // Initialize table columns
        setupTableColumns();

        // Load logo image
        try {
            logoImageView.setImage(new Image(getClass().getResourceAsStream("/assets/logo.png")));
        } catch (Exception e) {
            System.err.println("Could not load logo image: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        // Setup for datetime columns with custom formatting
        checkInColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCheckInDate()));
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

        checkOutColumn
                .setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCheckOutDate()));
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
        petNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPetName()));

        roomNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoomName()));

        roomTypeColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getRoomType() != null) {
                return new SimpleStringProperty(cellData.getValue().getRoomType().name());
            }
            return new SimpleStringProperty("");
        });

        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    // Apply style based on status
                    switch (status) {
                        case "in_progress":
                            setStyle("-fx-background-color: lightgreen;");
                            break;
                        case "confirmed":
                            setStyle("-fx-background-color: lightyellow;");
                            break;
                        case "pending":
                            setStyle("-fx-background-color: lightblue;");
                            break;
                        case "cancelled":
                            setStyle("-fx-background-color: lightpink;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        // Setup for price column with currency formatting
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                cellData.getValue().getPrice() != null ? cellData.getValue().getPrice() : 0.0).asObject());
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
        boardingData = FXCollections.observableArrayList(boardingInfoList);
        boardingTableView.setItems(boardingData);

        // Add selection handler for row details
        boardingTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        System.out.println("Selected boarding info: " + newSelection.getInfoId());
                        // Can add more detailed view logic here
                    }
                });
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
        PetBoardingInfoJPA selected = boardingTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a boarding entry to confirm.");
            return;
        }

        // Only allow confirmation of pending entries
        if (!"pending".equals(selected.getStatus())) {
            showAlert("Status Error", "Only pending entries can be confirmed.");
            return;
        }

        // Update status in database
        selected.setStatus("in_progress");
        petBoardingInfoJPADAO.update(selected);

        // Refresh table data
        refreshTableData();
    }

    @FXML
    public void handleRejectAction(ActionEvent actionEvent) {
        PetBoardingInfoJPA selected = boardingTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a boarding entry to reject.");
            return;
        }

        // Confirm rejection
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Rejection");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to cancel this boarding?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Update status in database
                selected.setStatus("cancelled");
                petBoardingInfoJPADAO.update(selected);

                // Refresh table data
                refreshTableData();
            }
        });
    }

    void refreshTableData() {
        // Get current filters from parent controller or re-fetch data
        boardingData.setAll(petBoardingInfoJPADAO.findAll());
    }

    private void showAssignRoomDialog(PetBoardingInfoJPA boarding) {
        // Room assignment dialog implementation would go here
        // This is simplified for the example
        showAlert("Room Assignment", "Room assignment functionality will be implemented here.");
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
            showAlert("Error", "Could not load the staff dashboard view: " + e.getMessage());
        }
    }
}
