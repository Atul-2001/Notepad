package org.signature.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class FontDialogController {

    @FXML
    private DialogPane dialog;
    @FXML
    private TextField fontName;
    @FXML
    private ListView<String> fontNameList;
    @FXML
    private TextField fontStyle;
    @FXML
    private ListView<String> fontStyleList;
    @FXML
    private TextField fontSize;
    @FXML
    private ListView<String> fontSizeList;
    @FXML
    private Label sampleText;
    @FXML
    private Button ok;

    private static JTextArea writingPad;

    public void initialize() {
        java.awt.Font currentFont = writingPad.getFont();
        String currentFontStyle = "";
        switch (currentFont.getStyle()) {
            case java.awt.Font.PLAIN:
                currentFontStyle = "Regular";
                break;
            case java.awt.Font.ITALIC:
                currentFontStyle = "Italic";
                break;
            case java.awt.Font.BOLD:
                currentFontStyle = "Bold";
                break;
            case java.awt.Font.BOLD + java.awt.Font.ITALIC:
                currentFontStyle = "Bold italic";
                break;
        }
        fontNameList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        fontStyleList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        fontSizeList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fontNameList.setItems(FXCollections.observableArrayList(graphicsEnvironment.getAvailableFontFamilyNames(Locale.US)));
        fontStyleList.setItems(FXCollections.observableArrayList("Regular", "Italic", "Bold", "Bold italic"));
        fontSizeList.setItems(FXCollections.observableArrayList("8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"));

        fontNameList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item);
                            setFont(Font.font(item, FontPosture.REGULAR, 14));
                        }
                    }
                };
            }
        });

        fontStyleList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item);
                            switch (item) {
                                case "Regular":
                                    setFont(Font.font("Roboto", FontPosture.REGULAR, 14));
                                    break;
                                case "Italic":
                                    setFont(Font.font("Roboto", FontPosture.ITALIC, 14));
                                    break;
                                case "Bold":
                                    setFont(Font.font("Roboto", FontWeight.BOLD, 14));
                                    break;
                                case "Bold italic":
                                    setFont(Font.font("Roboto", FontWeight.BOLD, FontPosture.ITALIC, 14));
                                    break;
                            }
                        }
                    }
                };
            }
        });

        fontName.setText(currentFont.getFontName());
        fontStyle.setText(currentFontStyle);
        fontSize.setText(String.valueOf(currentFont.getSize()));

        fontName.textProperty().addListener((observable, oldValue, newValue) -> {
            fontNameList.getSelectionModel().select(newValue);
            fontNameList.scrollTo(newValue);
            setSampleTextFont(newValue, fontStyle.getText(), fontSize.getText());
        });
        fontStyle.textProperty().addListener((observable, oldValue, newValue) -> {
            fontStyleList.getSelectionModel().select(newValue);
            setSampleTextFont(fontName.getText(), newValue, fontSize.getText());
        });
        fontSize.textProperty().addListener((observable, oldValue, newValue) -> {
            fontSizeList.getSelectionModel().select(newValue);
            fontSizeList.scrollTo(newValue);
            setSampleTextFont(fontName.getText(), fontStyle.getText(), newValue);
        });

        fontNameList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fontName.setText(newValue);
            }
        });
        fontStyleList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fontStyle.setText(newValue);
            }
        });
        fontSizeList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fontSize.setText(newValue);
            }
        });

        String finalCurrentFontStyle = currentFontStyle;
        Platform.runLater(() -> {
            fontNameList.getSelectionModel().select(currentFont.getFontName());
            fontStyleList.getSelectionModel().select(finalCurrentFontStyle);
            fontSizeList.getSelectionModel().select(String.valueOf(currentFont.getSize()));
            fontNameList.scrollTo(currentFont.getFontName());
            fontSizeList.scrollTo(String.valueOf(currentFont.getSize()));
            setSampleTextFont(currentFont.getFontName(), finalCurrentFontStyle, String.valueOf(currentFont.getSize()));
            ok.requestFocus();
        });
    }

    private void setSampleTextFont(String family, String style, String size) {
        if (size.isEmpty()) {
            size = "0";
        }
        double newSize = Double.parseDouble(size);
        if (newSize < 0.0) {
            newSize = -newSize;
        }
        switch (style) {
            case "Regular":
                sampleText.setFont(Font.font(family, FontPosture.REGULAR, newSize));
                break;
            case "Italic":
                sampleText.setFont(Font.font(family, FontPosture.ITALIC, newSize));
                break;
            case "Bold":
                sampleText.setFont(Font.font(family, FontWeight.BOLD, newSize));
                break;
            case "Bold italic":
                sampleText.setFont(Font.font(family, FontWeight.BOLD, FontPosture.ITALIC, newSize));
                break;
            default:
                sampleText.setFont(Font.font(family, FontPosture.findByName(style), newSize));
        }
    }

    @FXML
    void handleOkButton(ActionEvent event) {
        if (fontName.textProperty().isEmpty().get()) {
            showMessageAlert("There is no font with that name.\nChoose a font from the list of fonts.");
            return;
        }

        if (fontStyle.textProperty().isEmpty().get()) {
            showMessageAlert("This font is not available in that style.\nChoose a style from the list of styles.");
            return;
        }

        if (fontSize.textProperty().isEmpty().get()) {
            showMessageAlert("This is not a valid size.\nChoose a size from the list of sizes or any size above 0.");
            return;
        }

        String fontFamily = fontName.getText();
        if (!fontNameList.getItems().contains(fontFamily)) {
            showMessageAlert("There is no font with that name.\nChoose a font from the list of fonts.");
            return;
        }

        String FontStyle = fontStyle.getText();
        if (!fontStyleList.getItems().contains(FontStyle)) {
            showMessageAlert("This font is not available in that style.\nChoose a style from the list of styles.");
            return;
        }

        int FontSize = Integer.parseInt(fontSize.getText());
        if (FontSize < 0) {
            FontSize = -FontSize;
        }

        switch (FontStyle) {
            case "Regular":
                writingPad.setFont(new java.awt.Font(fontFamily, java.awt.Font.PLAIN, FontSize));
                break;
            case "Italic":
                writingPad.setFont(new java.awt.Font(fontFamily, java.awt.Font.ITALIC, FontSize));
                break;
            case "Bold":
                writingPad.setFont(new java.awt.Font(fontFamily, java.awt.Font.BOLD, FontSize));
                break;
            case "Bold italic":
                writingPad.setFont(new java.awt.Font(fontFamily, java.awt.Font.BOLD + java.awt.Font.ITALIC, FontSize));
                break;
        }

        handleCancelButton(null);
    }

    private void showMessageAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(dialog.getScene().getWindow());
        alert.setTitle("Font");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void handleCancelButton(ActionEvent event) {
        Window window = dialog.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public static void setSource(JTextArea textArea) {
        writingPad = textArea;
    }

}
