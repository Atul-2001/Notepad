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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.signature.model.TextFile;
import org.signature.preferences.UserPreferences;
import org.signature.util.ReadFile;
import org.signature.util.ResultIterator;
import org.signature.util.WriteFile;
import org.signature.util.Zoom;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TextPadController {

    private String DEFAULT_FILE_LOCATION = UserPreferences.getInstance().get(UserPreferences.Key.FILE_DIRECTORY, UserPreferences.DEFAULT_FILE_DIRECTORY);
    private final String DEFAULT_FILENAME = "Untitled";
    private final String DEFAULT_FILE_EXTENSION = "txt";

    @FXML
    private BorderPane root;
    @FXML
    private SwingNode swingNode;
    @FXML
    private HBox statusBar;
    @FXML
    MenuItem undo, cut, copy, paste, delete, find, findNext, findPrevious, replace, searchG, goTo;
    @FXML
    private CheckMenuItem wordWrap;
    @FXML
    private CheckMenuItem showStatusBar;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressMsg;
    @FXML
    private Label caretPositionIndicator;
    @FXML
    private Label zoomIndicator;
    @FXML
    private Label eolFormatIndicator;


    private final JTextArea writingPad = new JTextArea();
    private final UndoManager undoRedoManager = new UndoManager();

    private TextFile textFile;

    private Stage window;
    private final StringProperty windowTitle = new SimpleStringProperty("Untitled");
    private final BooleanProperty isEdited = new SimpleBooleanProperty(false);
    private boolean isNewFile, isOpenedFile = false;

    private final PrinterJob printerJob = PrinterJob.getPrinterJob();
    private final Clipboard clipboard = Clipboard.getSystemClipboard();

    private final ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();
    private final StringProperty caretPosition = new SimpleStringProperty();
    private final IntegerProperty zoomLevel = new SimpleIntegerProperty(100);
    private final StringProperty EndOfLine = new SimpleStringProperty();

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

        undo.disableProperty().bind(isEdited.not());

        caretPosition.addListener((observable, oldValue, newValue) -> caretPositionIndicator.setText(newValue));
        zoomLevel.addListener((observable, oldValue, newValue) -> zoomIndicator.setText(newValue.intValue() + "%"));
        EndOfLine.addListener((observable, oldValue, newValue) -> eolFormatIndicator.setText(newValue));

        Platform.runLater(() -> {
            window = App.getStage();
            window.setOnCloseRequest(event -> {
                if (!saveIfFileEdited()) {
                    event.consume();
                } else {
                    scheduledService.shutdown();
                    System.exit(0);
                }
            });
        });

        Runnable command = () -> {
            Platform.runLater(() -> paste.setDisable(!clipboard.hasString()));

            boolean isTextPadEmpty = writingPad.getText().isEmpty();
            find.setDisable(isTextPadEmpty);
            findNext.setDisable(isTextPadEmpty);
            findPrevious.setDisable(isTextPadEmpty);
            replace.setDisable(isTextPadEmpty);
            delete.setDisable(isTextPadEmpty);
            goTo.setDisable(isTextPadEmpty);

            boolean isTextSelected = (writingPad.getSelectedText() != null);
            cut.setDisable(!isTextSelected);
            copy.setDisable(!isTextSelected);
            searchG.setDisable(!isTextSelected);
        };
        scheduledService.scheduleWithFixedDelay(command, 1, 1, TimeUnit.SECONDS);
        Zoom.setControls(writingPad, zoomLevel);

        caretPosition.set("Ln 1, Col 1");
        zoomLevel.set(100);
        String os = System.getProperty("os.name");
        if (os.contains("Windows") || os.contains("windows")) {
            EndOfLine.set(TextFile.EOLFormat.WINDOW);
        } else if (os.contains("Linux") || os.contains("linux")) {
            EndOfLine.set(TextFile.EOLFormat.LINUX);
        } else if (os.contains("Mac") || os.contains("mac") || os.contains("Unix") || os.contains("unix")) {
            EndOfLine.set(TextFile.EOLFormat.UNIX_MACOS);
        }

        wordWrap.setSelected(UserPreferences.getInstance().getBoolean(UserPreferences.Key.WORD_WRAP, UserPreferences.DEFAULT_WORD_WRAP));
        showStatusBar.setSelected(UserPreferences.getInstance().getBoolean(UserPreferences.Key.STATUS_BAR, UserPreferences.DEFAULT_SHOW_STATUS_BAR));
        handleStatusBar(null);
    }

    private void createSwingTextArea(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            JScrollPane scrollPane = new JScrollPane(writingPad,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            writingPad.setLocale(Locale.getDefault());
            writingPad.setBackground(Color.WHITE);
            writingPad.setForeground(Color.BLACK);
            undoRedoManager.setLimit(1_00_000);
            writingPad.getDocument().addUndoableEditListener(undoRedoManager);
            writingPad.setFont(UserPreferences.getInstance().getFont());
            writingPad.setSelectionColor(new Color(72, 133, 237));
            writingPad.setSelectedTextColor(Color.WHITE);
            writingPad.getInputMap().put(KeyStroke.getKeyStroke("ctrl H"), "none");
            writingPad.getInputMap().put(KeyStroke.getKeyStroke("ctrl Z"), "none");
            writingPad.getInputMap().put(KeyStroke.getKeyStroke("ctrl X"), "none");
            writingPad.getInputMap().put(KeyStroke.getKeyStroke("ctrl C"), "none");
            writingPad.getInputMap().put(KeyStroke.getKeyStroke("ctrl V"), "none");
            writingPad.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "none");
            writingPad.getInputMap().put(KeyStroke.getKeyStroke("ctrl A"), "none");

            writingPad.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    String keyChar = String.valueOf(keyEvent.getKeyChar());
                    if (keyChar.matches("(?i)[a-z|0-9|\\p{Punct}|\\s|\\t]") && !keyEvent.isAltDown() && !keyEvent.isControlDown()) {
                        if (!isEdited.get()) {
                            Platform.runLater(() -> isEdited.set(true));
                        }
                    }

                    if (keyEvent.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                        if (!isEdited.get()) {
                            Platform.runLater(() -> isEdited.set(true));
                        }

                        if (isNewFile) {
                            if (writingPad.getText().isEmpty()) {
                                Platform.runLater(() -> isEdited.set(false));
                            }
                        }
                    }

                    if (keyEvent.getKeyChar() == KeyEvent.VK_DELETE) {
                        if (!isEdited.get()) {
                            Platform.runLater(() -> isEdited.set(true));
                        }

                        if (isNewFile) {
                            if (writingPad.getText().isEmpty()) {
                                Platform.runLater(() -> isEdited.set(false));
                            }
                        }
                    }
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                }
            });

            writingPad.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (!isEdited.get()) {
                        Platform.runLater(() -> isEdited.set(true));
                    }

                    if (isOpenedFile) {
                        Platform.runLater(() -> isEdited.set(false));
                        isOpenedFile = false;
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (!isEdited.get()) {
                        Platform.runLater(() -> isEdited.set(true));
                    }

                    if (isNewFile) {
                        if (writingPad.getText().isEmpty()) {
                            Platform.runLater(() -> isEdited.set(false));
                        }
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });

            writingPad.addCaretListener(event -> {
                int caretPos = event.getDot();
                try {
                    int row = writingPad.getLineOfOffset(caretPos);
                    int column = caretPos - writingPad.getLineStartOffset(row);
                    Platform.runLater(() -> caretPosition.set("Ln " + (row + 1) + ", Col " + (column + 1)));
                } catch (BadLocationException ignored) {}
            });

            swingNode.setContent(scrollPane);
        });
    }

    private boolean saveIfFileEdited() {
        if (isEdited.get()) {
            Alert fileEditedAlert = new Alert(Alert.AlertType.CONFIRMATION);
            fileEditedAlert.setTitle("Notepad");
            fileEditedAlert.setHeaderText(null);
            fileEditedAlert.setContentText("Do you want to save the changes to " + windowTitle.get() + "?");
            ButtonType save = new ButtonType("Save");
            ButtonType dontSave = new ButtonType("Don't Save");
            fileEditedAlert.getButtonTypes().setAll(save, dontSave, ButtonType.CANCEL);
            Optional<ButtonType> result = fileEditedAlert.showAndWait();

            if (result.isPresent() && result.get().equals(save)) {
                handleSaveFile(null);
                return true;
            } else if (result.isPresent() && result.get().equals(dontSave)) {
                return true;
            } else if (result.isPresent() && result.get().equals(ButtonType.CANCEL)) {
                return false;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    /*
     * All Functionality of File Menu
     *
     *  */

    @FXML
    public void handleNewFile(ActionEvent event) {
        if (saveIfFileEdited()) {
            isNewFile = true;
            writingPad.setCaretPosition(0);
            writingPad.setText("");
            textFile = new TextFile(DEFAULT_FILE_LOCATION, DEFAULT_FILENAME, DEFAULT_FILE_EXTENSION);
            windowTitle.set(DEFAULT_FILENAME);
            isEdited.set(false);
        }
    }

    @FXML
    public void handleOpenFile(ActionEvent actionEvent) {
        if (saveIfFileEdited()) {
            FileChooser openFileDialog = new FileChooser();
            openFileDialog.setTitle("Open File");
            openFileDialog.setInitialDirectory(new File(DEFAULT_FILE_LOCATION));
            openFileDialog.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text", "*.txt"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            File source = openFileDialog.showOpenDialog(window);

            if (source != null) {
                if (new TextFile(source).getSizeInMB() > 10) {
                    Alert fileSizeAlert = new Alert(Alert.AlertType.INFORMATION);
                    fileSizeAlert.setTitle("Notepad");
                    fileSizeAlert.setHeaderText(null);
                    fileSizeAlert.setContentText("File size is too large to open\nin this text editor.");
                    fileSizeAlert.showAndWait();
                    return;
                }
                
                textFile = new TextFile(source);
                windowTitle.set(source.getName());
                isEdited.set(false);
                isNewFile = false;
                isOpenedFile = true;
                DEFAULT_FILE_LOCATION = textFile.getFilepath();
                UserPreferences.getInstance().set(UserPreferences.Key.FILE_DIRECTORY, textFile.getFilepath());

                Task<Void> readTask = new ReadFile(writingPad, source, EndOfLine);
                progressMsg.visibleProperty().bind(readTask.runningProperty());
                progressMsg.textProperty().bind(readTask.messageProperty());
                progressBar.visibleProperty().bind(readTask.runningProperty());
                progressBar.progressProperty().bind(readTask.progressProperty());

                ExecutorService service = Executors.newSingleThreadExecutor(runnable -> {
                    Thread thread = Executors.defaultThreadFactory().newThread(runnable);
                    thread.setDaemon(true);
                    return thread;
                });

                readTask.setOnSucceeded(event -> {
                    service.shutdown();
                    isOpenedFile = true;
                    isEdited.set(false);
                });
                readTask.setOnFailed(event -> {
                    service.shutdown();
                    isOpenedFile = true;
                    isEdited.set(false);
                });
                readTask.setOnCancelled(event -> {
                    service.shutdown();
                    isOpenedFile = true;
                    isEdited.set(false);
                });

                service.execute(readTask);
            }
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
            alert.setTitle("Notepad");
            alert.setHeaderText(null);
            alert.setContentText("Failed to open new window!\nPlease try again. " + e.getLocalizedMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void handleSaveFile(ActionEvent actionEvent) {
        if (isNewFile) {
            handleSaveAs(null);
        } else if (isEdited.get()) {
            File destination = textFile.toFile();

            Service<Boolean> writeToFileService = new WriteFile(writingPad, destination, EndOfLine);
            ExecutorService service = Executors.newFixedThreadPool(2);
            writeToFileService.setExecutor(service);

            progressMsg.visibleProperty().bind(writeToFileService.runningProperty());
            progressMsg.textProperty().bind(writeToFileService.messageProperty());
            progressBar.visibleProperty().bind(writeToFileService.runningProperty());
            progressBar.progressProperty().bind(writeToFileService.progressProperty());

            writeToFileService.setOnSucceeded(event -> {
                if (writeToFileService.getValue()) {
                    if (!Files.exists(destination.toPath())) {
                        Alert saveFailedAlert = new Alert(Alert.AlertType.ERROR);
                        saveFailedAlert.setTitle("Notepad");
                        saveFailedAlert.setContentText(null);
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

//                    textFile = new TextFile(destination);
//                    windowTitle.set(destination.getName());
                    isEdited.set(false);
                    isNewFile = false;
                }
                service.shutdown();
            });

            writeToFileService.setOnFailed(event -> writeToFileService.restart());

            writeToFileService.setOnCancelled(event -> service.shutdown());

            writeToFileService.start();
        }
    }

    @FXML
    public void handleSaveAs(ActionEvent actionEvent) {
        FileChooser saveFileDialog = new FileChooser();
        saveFileDialog.setTitle("Save As");
        saveFileDialog.setInitialDirectory(new File(textFile.getFilepath()));
        saveFileDialog.setInitialFileName(textFile.getFilename() + "." + textFile.getFileExtension());
        saveFileDialog.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File destination = saveFileDialog.showSaveDialog(window);

        if (destination != null) {
            Service<Boolean> writeToFileService = new WriteFile(writingPad, destination, EndOfLine);
            ExecutorService service = Executors.newFixedThreadPool(2);
            writeToFileService.setExecutor(service);

            progressMsg.visibleProperty().bind(writeToFileService.runningProperty());
            progressMsg.textProperty().bind(writeToFileService.messageProperty());
            progressBar.visibleProperty().bind(writeToFileService.runningProperty());
            progressBar.progressProperty().bind(writeToFileService.progressProperty());

            writeToFileService.setOnSucceeded(event -> {
                if (writeToFileService.getValue()) {
                    if (!Files.exists(destination.toPath())) {
                        Alert saveFailedAlert = new Alert(Alert.AlertType.ERROR);
                        saveFailedAlert.setTitle("Notepad");
                        saveFailedAlert.setHeaderText(null);
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

                    textFile = new TextFile(destination);
                    DEFAULT_FILE_LOCATION = textFile.getFilepath();
                    UserPreferences.getInstance().set(UserPreferences.Key.FILE_DIRECTORY, textFile.getFilepath());
                    windowTitle.set(destination.getName());
                    isEdited.set(false);
                    isNewFile = false;
                }
                service.shutdown();
            });

            writeToFileService.setOnFailed(event -> writeToFileService.restart());

            writeToFileService.setOnCancelled(event -> service.shutdown());

            writeToFileService.start();
        }
    }

    private void printerExceptionMsg(String msg) {
        Alert noPrinterAlert = new Alert(Alert.AlertType.INFORMATION);
        noPrinterAlert.setTitle("Notepad");
        noPrinterAlert.setHeaderText(null);
        noPrinterAlert.setContentText(msg);
        noPrinterAlert.showAndWait();
    }

    @FXML
    public void handlePageSetup(ActionEvent actionEvent) {
        try {
            printerJob.defaultPage(printerJob.pageDialog(new PageFormat()));
        } catch (Exception exception) {
            String message;
            if (exception.getMessage() != null) {
                if (exception.getMessage().isEmpty()) {
                    message = "Error occurred! Please try again!";
                } else {
                    message = exception.getMessage();
                }
            } else {
                message = "Error occurred! Please try again!";
            }
            printerExceptionMsg(message);
        }
    }

    @FXML
    public void handlePagePrinting(ActionEvent actionEvent) {
        Task<Void> printTasK = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    updateMessage("Getting things ready...");
                    printerJob.setPrintable(writingPad.getPrintable(null, null));
                    printerJob.setJobName(textFile.getFilename());
                    if (printerJob.printDialog()) {
                        updateMessage("Printing...");
                        printerJob.print();
                    } else {
                        cancel(true);
                    }
                } catch (PrinterException | NullPointerException exception) {
                    String message = "Error occurred! Please try again\n" + exception.getMessage();
                    printerExceptionMsg(message);
                    this.failed();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                updateMessage("Done!");
            }

            @Override
            protected void cancelled() {
                updateMessage("Cancelled!");
            }

            @Override
            protected void failed() {
                updateMessage("Failed!");
            }
        };

        progressMsg.visibleProperty().bind(printTasK.runningProperty());
        progressMsg.textProperty().bind(printTasK.messageProperty());
        progressBar.visibleProperty().bind(printTasK.runningProperty());
        progressBar.progressProperty().bind(printTasK.progressProperty());

        new Thread(printTasK).start();
    }

    @FXML
    public void handleExit(ActionEvent actionEvent) {
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /*
     *
     * Functionality of Edit Menu
     * */

    @FXML
    public void handleUndo(ActionEvent actionEvent) {
        try {
            undoRedoManager.undo();
        } catch (CannotUndoException ignored) {
        }
    }

    @FXML
    public void handleRedo(ActionEvent actionEvent) {
        try {
            undoRedoManager.redo();
        } catch (CannotRedoException ignored) {
        }
    }

    @FXML
    public void handleCut(ActionEvent actionEvent) {
        writingPad.cut();
    }

    @FXML
    public void handleCopy(ActionEvent actionEvent) {
        writingPad.copy();
    }

    @FXML
    public void handlePaste(ActionEvent actionEvent) {
        int currentPos = writingPad.getCaretPosition();
        int finalPos = currentPos + clipboard.getString().length();
        writingPad.paste();
        writingPad.setCaretPosition(finalPos);
    }

    @FXML
    public void handleDelete(ActionEvent actionEvent) {

        if (writingPad.getSelectedText() == null) {
            int caretPos = writingPad.getCaretPosition();
            try {
                writingPad.replaceRange("", caretPos, caretPos + 1);
            } catch (IllegalArgumentException ignored) {
            }
        } else {
            int selectionStart = writingPad.getSelectionStart();
            writingPad.replaceSelection("");
            writingPad.setCaretPosition(selectionStart);
        }
    }

    @FXML
    public void handleSearchSelectedTextWithGoogle(ActionEvent actionEvent) {
        String str = writingPad.getSelectedText();
        if (str != null) {
            if (!str.isEmpty()) {

                Task<Void> searchTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            URI search = new URI("https://www.google.com/search?q=" + URLEncoder.encode(str, StandardCharsets.UTF_8));
                            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                Desktop.getDesktop().browse(search);
                            } else {
                                updateMessage("Browser not supported");
                                System.out.println("Browser not supported");
                                cancel(true);
                            }
                        } catch (IOException | URISyntaxException e) {
                            System.out.println(e.getMessage());
                            failed();
                        }
                        return null;
                    }

                    @Override
                    protected void running() {
                        updateMessage("Launching browser...");
                    }

                    @Override
                    protected void succeeded() {
                        updateMessage("Done!");
                    }

                    @Override
                    protected void cancelled() {
                        updateMessage("Cancelled!");
                    }

                    @Override
                    protected void failed() {
                        updateMessage("Failed!");
                    }
                };

                progressMsg.visibleProperty().bind(searchTask.runningProperty());
                progressMsg.textProperty().bind(searchTask.messageProperty());
                progressBar.visibleProperty().bind(searchTask.runningProperty());
                progressBar.progressProperty().bind(searchTask.progressProperty());

                Thread searchThread = new Thread(searchTask);
                searchThread.setDaemon(true);
                searchThread.start();
            }
        }
    }

    @FXML
    public void handleFind(ActionEvent actionEvent) {
        final Dialog<Void> findTextDialog = new Dialog<>();
        findTextDialog.initOwner(window);
        findTextDialog.setTitle("Find");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FindTextDialog.fxml"));
        try {
            findTextDialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        FindTextDialogController controller = loader.getController();
        controller.setSource(writingPad);
        findTextDialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> findTextDialog.close());
        findTextDialog.showAndWait();
    }

    @FXML
    public void handleFindNext(ActionEvent actionEvent) {
        if (!ResultIterator.getInstance().findNext()) {
            handleFind(null);
        }
    }

    @FXML
    public void handleFindPrevious(ActionEvent actionEvent) {
        if (!ResultIterator.getInstance().findPrevious()) {
            handleFind(null);
        }
    }

    @FXML
    public void handleReplaceText(ActionEvent actionEvent) {
        final Dialog<Void> replaceTextDialog = new Dialog<>();
        replaceTextDialog.initOwner(window);
        replaceTextDialog.setTitle("Replace");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ReplaceTextDialog.fxml"));
        try {
            replaceTextDialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        ReplaceTextDialogController controller = loader.getController();
        controller.setSource(writingPad);
        replaceTextDialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> replaceTextDialog.close());
        replaceTextDialog.showAndWait();
    }

    @FXML
    public void handleGoToLine(ActionEvent actionEvent) {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.initOwner(window);
        inputDialog.setTitle("Go To");
        inputDialog.setHeaderText(null);
        inputDialog.setContentText("Enter line number : ");
        TextField lineNumberField = inputDialog.getEditor();
        lineNumberField.setOnKeyPressed(event -> lineNumberField.setEditable(event.getCode().isDigitKey() ||
                event.getCode().isNavigationKey() ||
                event.getCode().equals(KeyCode.BACK_SPACE) ||
                event.getCode().equals(KeyCode.DELETE)));

        Optional<String> result = inputDialog.showAndWait();
        if (result.isPresent()) {
            String value = result.get();
            if (!value.isEmpty()) {
                int lineNumber = Integer.parseInt(value);
                if (lineNumber <= 0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Go To");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid line no : " + lineNumber);
                    alert.showAndWait();
                } else {
                    try {
                        int caretPosition = writingPad.getLineStartOffset(lineNumber-1);
                        writingPad.setCaretPosition(caretPosition);
                        writingPad.setSelectionStart(caretPosition);
                        writingPad.setSelectionEnd(writingPad.getLineEndOffset(lineNumber-1));
                    } catch (BadLocationException ignored) {}
                }
            }
        }
    }

    @FXML
    public void handleSelectAll(ActionEvent actionEvent) {
        writingPad.selectAll();
    }

    @FXML
    public void handlePrintDateTime(ActionEvent actionEvent) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
        writingPad.insert(dateTime.format(formatter), writingPad.getCaretPosition());
        isEdited.set(true);
    }

    /*
     *
     * Functionality of Format Menu
     * */

    @FXML
    public void handleWordWrap(ActionEvent actionEvent) {
        SwingUtilities.invokeLater(() -> {
            if (wordWrap.isSelected()) {
                writingPad.setLineWrap(true);
                writingPad.setWrapStyleWord(true);
            } else {
                writingPad.setLineWrap(false);
                writingPad.setWrapStyleWord(false);
            }
            UserPreferences.getInstance().setBoolean(UserPreferences.Key.WORD_WRAP, wordWrap.isSelected());
        });
    }

    @FXML
    public void handleFont(ActionEvent actionEvent) {
        Dialog<Void> fontDialog = new Dialog<>();
        fontDialog.initOwner(window);
        fontDialog.setTitle("Font");
        try {
            FontDialogController.setSource(writingPad);
            fontDialog.getDialogPane().setContent(FXMLLoader.load(getClass().getResource("FontDialog.fxml")));
        } catch (IOException e) {
            System.out.println("Failed to load font selection window " + e.getLocalizedMessage());
            return;
        }
        fontDialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> fontDialog.close());
        fontDialog.showAndWait();
    }

    /*
     *
     * View Menu
     * */

    @FXML
    public void handleZoomIn(ActionEvent actionEvent) {
        Zoom.in();
    }

    @FXML
    public void handleZoomOut(ActionEvent actionEvent) {
        Zoom.out();
    }

    @FXML
    public void handleRestoreDefaultZoom(ActionEvent actionEvent) {
        Zoom.restore();
    }

    @FXML
    public void handleStatusBar(ActionEvent event) {
        if (showStatusBar.isSelected()) {
            root.setBottom(statusBar);
        } else {
            root.setBottom(null);
        }
        UserPreferences.getInstance().setBoolean(UserPreferences.Key.STATUS_BAR, showStatusBar.isSelected());
    }

    /*
    *
    * Help Menu
    * */

    @FXML
    public void handleHelpPage(ActionEvent actionEvent) {
        Dialog<ButtonType> helpDialog = new Dialog<>();
        helpDialog.initOwner(window);
        helpDialog.setTitle("Notepad - Help");
        try {
            helpDialog.getDialogPane().setContent(FXMLLoader.load(getClass().getResource("Help.fxml")));
        } catch (IOException e) {
            System.out.println("Failed to load Help window " + e.getLocalizedMessage());
            return;
        }
        helpDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        helpDialog.showAndWait();
    }

    @FXML
    public void handleSendFeedback(ActionEvent actionEvent) {
        Dialog<Void> feedbackDialog = new Dialog<>();
        feedbackDialog.initOwner(window);
        feedbackDialog.setTitle("Notepad - Feedback");
        try {
            feedbackDialog.getDialogPane().setContent(FXMLLoader.load(getClass().getResource("Feedback.fxml")));
        } catch (IOException e) {
            System.out.println("Failed to load Feedback window " + e.getLocalizedMessage());
            return;
        }
        feedbackDialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> feedbackDialog.close());
        feedbackDialog.showAndWait();
    }

    @FXML
    public void handleAboutPage(ActionEvent actionEvent) {
        Dialog<ButtonType> aboutDialog = new Dialog<>();
        aboutDialog.initOwner(window);
        aboutDialog.setTitle("Notepad - About");
        try {
            aboutDialog.getDialogPane().setContent(FXMLLoader.load(getClass().getResource("About.fxml")));
        } catch (IOException e) {
            System.out.println("Failed to load About window " + e.getLocalizedMessage());
            return;
        }
        aboutDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        aboutDialog.showAndWait();
    }

}
