module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    opens org.signature.ui to javafx.fxml;
    exports org.signature.ui;
}