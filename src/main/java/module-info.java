module org.example.petproject {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.naming;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.sql;

    requires org.kordamp.bootstrapfx.core;

    opens model to org.hibernate.orm.core;
    exports model;


    opens org.example.petproject to javafx.fxml;
    exports org.example.petproject;
}