module org.example.petproject {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.naming;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.sql;

    requires org.kordamp.bootstrapfx.core;

    // Cho phép FXMLLoader truy cập reflect vào package chứa controller
    opens org.example.petproject.controller to javafx.fxml;

    // Nếu FXML nằm trong cùng package với AppLauncher, hoặc controller tham chiếu
    // tới các @FXML trong package gốc, bạn cũng có thể mở thêm:


    opens org.example.petproject.model to org.hibernate.orm.core;
    exports org.example.petproject.model;


    opens org.example.petproject to javafx.fxml;
    exports org.example.petproject;
}