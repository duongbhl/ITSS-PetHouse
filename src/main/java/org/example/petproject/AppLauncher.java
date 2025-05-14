package org.example.petproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.petproject.model.User;
import org.example.petproject.util.SessionManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class AppLauncher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // 1) Tạo SessionFactory thủ công từ hibernate.cfg.xml
        Configuration cfg = new Configuration().configure();
        SessionFactory factory = cfg.buildSessionFactory();

        // 2) Mở session, load User test
        Session session = factory.openSession();
        User testOwner = session.get(User.class, "U002");
        session.close();
        factory.close();

        // 3) Set vào SessionManager để controller xài
        SessionManager.setCurrentUser(testOwner);

        // 4) Load FXML, show stage
        Parent root = FXMLLoader.load(
                getClass().getResource("/org/example/petproject/MedicalHistoryView.fxml")
//                getClass().getResource("/org/example/petproject/NotificationView.fxml")
        );
        stage.setTitle("Lịch sử khám bệnh");
        stage.setScene(new Scene(root, 900, 700));
        stage.show();
    }
}
