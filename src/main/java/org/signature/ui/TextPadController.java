package org.signature.ui;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.signature.model.TextFile;
import org.signature.util.ReadFile;
import org.signature.util.WriteFile;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Optional;

public class TextPadController {

    private final String DEFAULT_FILE_LOCATION = System.getProperty("user.home") + File.separator + "Documents";
    private final String DEFAULT_FILENAME = "Untitled";
    private final String DEFAULT_FILE_EXTENSION = "txt";

    @FXML
    private BorderPane root;
    @FXML
    private SwingNode swingNode;
    @FXML
    private HBox statusBar;
    @FXML
    private CheckMenuItem wordWrap;
    @FXML
    private CheckMenuItem showStatusBar;
    @FXML
    private ProgressBar progressBar;
    private final JTextArea writingPad = new JTextArea();

    private TextFile textFile;

    private Stage window;
    private final StringProperty windowTitle = new SimpleStringProperty("Untitled");
    private final BooleanProperty isEdited = new SimpleBooleanProperty(false);
    private boolean isNewFile;

    public void initialize() {
        textFile = new TextFile(DEFAULT_FILE_LOCATION, DEFAULT_FILENAME, DEFAULT_FILE_EXTENSION);
        isNewFile = true;
        createSwingTextArea(swingNode);

        windowTitle.addListener((observable, oldValue, newValue) -> window.setTitle(newValue + " - Notepad"));

        isEdited.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                window.setTitle("*" + windowTitle.get() + " - Notepad");
            } else {
                window.setTitle(windowTitle.get() + " - Notepad");
            }
        });

        Platform.runLater(() -> window = App.getStage());
    }

    private void createSwingTextArea(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            JScrollPane scrollPane = new JScrollPane(writingPad,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            swingNode.setContent(scrollPane);
        });
    }

    @FXML
    public void handleEditEvent(KeyEvent keyEvent) {
        String keyChar = keyEvent.getCharacter();
        if (keyChar.matches("(?i)[a-z|0-9|\\p{Punct}|\\s|\\t]")) {
            if (!isEdited.get()) {
                isEdited.set(true);
            }
        }

        if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.X)) {
            if (!isEdited.get()) {
                isEdited.set(true);
            }
        }

        if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.V)) {
            if (!isEdited.get()) {
                isEdited.set(true);
            }
        }

        if (keyEvent.getCode().equals(KeyCode.BACK_SPACE)) {
            if (!isEdited.get()) {
                isEdited.set(true);
            }
        }

        if (keyEvent.getCode().equals(KeyCode.DELETE)) {
            if (!isEdited.get()) {
                isEdited.set(true);
            }
        }
    }

    @FXML
    public void handleNewFile(ActionEvent event) {
        if (isEdited.get()) {
            Alert fileEditedAlert = new Alert(Alert.AlertType.CONFIRMATION);
            fileEditedAlert.setContentText("Do you want to save the changes to " + windowTitle.get() + "?");
            ButtonType save = new ButtonType("Save");
            ButtonType dontSave = new ButtonType("Don't Save");
            fileEditedAlert.getButtonTypes().setAll(save, dontSave, ButtonType.CANCEL);
            Optional<ButtonType> result = fileEditedAlert.showAndWait();
            //button - Save, Don't Save, Cancel

            if (result.isPresent() && result.get().equals(save)) {
                if (!handleSaveFile(null)) {
                    return;
                }
            } else if (result.isPresent() && result.get().equals(ButtonType.CANCEL)) {
                return;
            }
        }

        writingPad.setText("");
        textFile = new TextFile(DEFAULT_FILE_LOCATION, DEFAULT_FILENAME, DEFAULT_FILE_EXTENSION);
        windowTitle.set(DEFAULT_FILENAME);
        isEdited.set(false);
        isNewFile = true;
    }

    @FXML
    public void handleOpenFile(ActionEvent event) {
        FileChooser openFileDialog = new FileChooser();
        openFileDialog.setTitle("Open File");
        openFileDialog.setInitialDirectory(new File(DEFAULT_FILE_LOCATION));
        openFileDialog.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File source = openFileDialog.showOpenDialog(window);

        if (source != null) {
            textFile = new TextFile(source);
            windowTitle.set(source.getName());
            isNewFile = false;

            Task<Void> readTask = new ReadFile(writingPad, source);
            progressBar.visibleProperty().bind(readTask.runningProperty());
            progressBar.progressProperty().bind(readTask.progressProperty());
            Thread readThread = new Thread(readTask);
            readThread.setName("Read");
            readThread.setDaemon(true);
            readThread.start();
        }
    }

    @FXML
    public void handleNewWindow(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("TextPad.fxml"));
            Stage stage = new Stage();
            stage.setTitle(DEFAULT_FILENAME + " - Notepad");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setContentText("Failed to open new window!\nPlease try again.");
            alert.showAndWait();
        }
    }

    @FXML
    public boolean handleSaveFile(ActionEvent actionEvent) {
        File destination = textFile.toFile();

        if (isNewFile) {
            FileChooser saveFileDialog = new FileChooser();
            saveFileDialog.setTitle("Save File");
            saveFileDialog.setInitialDirectory(new File(DEFAULT_FILE_LOCATION));
            saveFileDialog.setInitialFileName(DEFAULT_FILENAME + "." + DEFAULT_FILE_EXTENSION);
            saveFileDialog.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text", "*.txt"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            destination = saveFileDialog.showSaveDialog(window);
        }

        if (destination != null) {
            Service<Boolean> writeToFileService = new WriteFile(writingPad, destination);
            progressBar.visibleProperty().bind(writeToFileService.runningProperty());
            progressBar.progressProperty().bind(writeToFileService.progressProperty());

            File finalDestination = destination;
            writeToFileService.setOnSucceeded(event -> {
                if (writeToFileService.getValue()) {
                    if (!Files.exists(finalDestination.toPath())) {
                        Alert saveFailedAlert = new Alert(Alert.AlertType.ERROR);
                        saveFailedAlert.setTitle("Save failed!");
                        saveFailedAlert.setContentText("Failed to save the file. Would you like to try again!");
                        ButtonType tryAgain = new ButtonType("Try Again");
                        ButtonType dontSave = new ButtonType("Don't Save");
                        saveFailedAlert.getButtonTypes().setAll(tryAgain, dontSave, ButtonType.CANCEL);
                        Optional<ButtonType> result = saveFailedAlert.showAndWait();

                        if (result.isPresent() && result.get().equals(tryAgain)) {
                            writeToFileService.restart();
                        } else if (result.isPresent() && result.get().equals(dontSave)) {
                            System.out.println("File not saved");
                        } else if (result.isPresent()) {
                            return;
                        }
                    }

                    textFile = new TextFile(finalDestination);
                    windowTitle.set(finalDestination.getName());
                    isEdited.set(false);
                    isNewFile = false;
                }
            });

            writeToFileService.setOnFailed(event -> writeToFileService.restart());

            writeToFileService.start();
            return true;
        } else {
            return false;
        }
    }

    @FXML
    public void handleWordWrap(ActionEvent actionEvent) {
        if (wordWrap.isSelected()) {
            writingPad.setLineWrap(true);
            writingPad.setWrapStyleWord(true);
        } else {
            writingPad.setLineWrap(false);
            writingPad.setWrapStyleWord(false);
        }
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