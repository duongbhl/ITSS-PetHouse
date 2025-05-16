module org.example.petproject {
    // JavaFX modules
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;

    // Hibernate & JPA
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.sql;

    // Optional UI framework
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    // Cho phép JavaFX launcher và FXMLLoader khởi tạo Main & Controllers
    opens org.example.petproject to javafx.graphics, javafx.fxml;
    opens org.example.petproject.controller to javafx.fxml;
    opens org.example.petproject.controller.Dashboard to javafx.fxml;

    // Cho phép Hibernate truy cập entity
    opens org.example.petproject.model to org.hibernate.orm.core;
    opens org.example.petproject.dao to org.hibernate.orm.core;

    // Nếu bạn muốn dùng entity ở bên ngoài (ví dụ tests), thì export
    exports org.example.petproject.model;
    exports org.example.petproject.controller;
    exports org.example.petproject.controller.Dashboard;
    exports org.example.petproject;

}
