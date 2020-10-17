package org.signature.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;

public class HelpController {

    @FXML
    private TableView<Shortcut> help;

    public void initialize() {
        ObservableList<Shortcut> shortcuts = FXCollections.observableArrayList();
        shortcuts.add(new Shortcut("New File", "Ctrl+N"));
        shortcuts.add(new Shortcut("New Window", "Ctrl+Shift+N"));
        shortcuts.add(new Shortcut("Open File", "Ctrl+O"));
        shortcuts.add(new Shortcut("Save File", "Ctrl+S"));
        shortcuts.add(new Shortcut("Save As", "Ctrl+Shift+S"));
        shortcuts.add(new Shortcut("Print", "Ctrl+P"));

        shortcuts.add(new Shortcut("Cut", "Ctrl+X"));
        shortcuts.add(new Shortcut("Copy", "Ctrl+C"));
        shortcuts.add(new Shortcut("Paste", "Ctrl+V"));
        shortcuts.add(new Shortcut("Delete", "delete"));
        shortcuts.add(new Shortcut("Search with google", "Ctrl+E"));
        shortcuts.add(new Shortcut("Find", "Ctrl+F"));
        shortcuts.add(new Shortcut("Find next", "F3"));
        shortcuts.add(new Shortcut("Replace", "Ctrl+H"));
        shortcuts.add(new Shortcut("Go To", "Ctrl+G"));
        shortcuts.add(new Shortcut("Select All", "Ctrl+A"));
        shortcuts.add(new Shortcut("Time/Date", "F5"));

        shortcuts.add(new Shortcut("Zoom IN", "Ctrl+Plus"));
        shortcuts.add(new Shortcut("Zoom OUT", "Ctrl+Minus"));
        shortcuts.add(new Shortcut("Default Zoom", "Ctrl+0"));
        help.setItems(shortcuts);
    }

    public static class Shortcut {
        SimpleStringProperty command = new SimpleStringProperty();
        SimpleStringProperty keyValue = new SimpleStringProperty();

        public Shortcut (String command, String keyValue) {
            this.command.set(command);
            this.keyValue.set(keyValue);
        }

        public String getCommand() {
            return command.get();
        }

        public SimpleStringProperty commandProperty() {
            return command;
        }

        public String getKeyValue() {
            return keyValue.get();
        }

        public SimpleStringProperty keyValueProperty() {
            return keyValue;
        }
    }
}
