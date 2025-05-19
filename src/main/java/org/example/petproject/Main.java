package org.example.petproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.petproject.dao.HibernateUtil;
import org.example.petproject.dao.PetBoardingInfoJPADAO;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    private static final String APP_TITLE = "Pet House";
    private static final String LOGIN_FXML = "/org/example/petproject/LoginView.fxml";
    private static final String CSS_RESOURCE = "/css/style.css";
    private static final String LOGO = "/assets/logo.png";

    private final Image logo = new Image(getClass().getResourceAsStream(LOGO));

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Print class path resources to debug FXML loading issues
        URL url = getClass().getResource("/org/example/petproject/StaffBoardingListView.fxml");
        System.out.println("Resource URL: " + url);

        // Initialize database
        initializeDatabase();

        // Load màn Login
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_FXML));
        Parent root = loader.load();

        // Tạo Scene, gán stylesheet
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource(CSS_RESOURCE).toExternalForm());

        // Setup stage
        primaryStage.setTitle(APP_TITLE);
        primaryStage.getIcons().add(logo);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // Đảm bảo Hibernate SessionFactory đóng khi app kết thúc
        HibernateUtil.shutdown();
        super.stop();
    }

    private void initializeDatabase() {
        // Other initialization code

        // Initialize PetBoardingInfoJPA data
        PetBoardingInfoJPADAO dao = new PetBoardingInfoJPADAO();
        try {
            dao.synchronizeAllFromPetBoarding();
            System.out.println("PetBoardingInfo synchronized successfully");
        } catch (Exception e) {
            System.err.println("Error synchronizing PetBoardingInfo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}