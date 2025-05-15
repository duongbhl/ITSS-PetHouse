module org.example.petproject {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.naming;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.sql;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    // Cho Hibernate
    opens org.example.petproject.model to org.hibernate.orm.core;
    exports org.example.petproject.model;

    // Cho FXML (JavaFX)
    opens org.example.petproject.controller to javafx.fxml;
    exports org.example.petproject.controller;

    // Nếu bạn load FXML từ thư mục gốc (resources/org/example/petproject), thì nên mở package gốc:
    opens org.example.petproject to javafx.fxml;
    exports org.example.petproject;

}