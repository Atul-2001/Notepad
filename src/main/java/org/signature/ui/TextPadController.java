package org.signature.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class TextPadController {

    @FXML
    private BorderPane root;
    @FXML
    private CheckMenuItem showStatusBar;
    @FXML
    private HBox statusBar;

    public void initialize() {
        root.setBottom(null);
    }

    @FXML
    public void handleStatusBar(ActionEvent event) {
        if (showStatusBar.isSelected()) {
            root.setBottom(statusBar);
        } else {
            root.setBottom(null);
        }
    }
}
