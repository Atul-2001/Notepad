module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;
    requires java.mail;

    opens org.signature.ui to javafx.fxml;
    exports org.signature.ui;
    exports org.signature.model;
}