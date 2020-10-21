package org.signature.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.signature.preferences.UserPreferences;
import org.signature.util.MatcherResultList;
import org.signature.util.ResultIterator;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplaceTextDialogController {

    @FXML
    private DialogPane dialog;
    @FXML
    private TextField searchField, replaceableTextField;
    @FXML
    private Button findButton, replaceButton, replaceAllButton;
    @FXML
    private CheckBox matchCase, wrapAround;

    private JTextArea writingPad;
    private final AtomicBoolean isChanged = new AtomicBoolean(false);

    public void initialize() {
        matchCase.setSelected(UserPreferences.getInstance().getBoolean(UserPreferences.Key.MATCH_CASES, UserPreferences.DEFAULT_IS_MATCH_CASES));
        wrapAround.setSelected(UserPreferences.getInstance().getBoolean(UserPreferences.Key.WRAP_AROUND, UserPreferences.DEFAULT_IS_WRAP_AROUND));
        searchField.setText(UserPreferences.getInstance().get(UserPreferences.Key.FIND_TEXT, UserPreferences.DEFAULT_FIND_TEXT));

        findButton.disableProperty().bind(searchField.textProperty().isEmpty());
        replaceButton.disableProperty().bind(searchField.textProperty().isEmpty());
        replaceAllButton.disableProperty().bind(searchField.textProperty().isEmpty());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                isChanged.set(true);
            }
            UserPreferences.getInstance().set(UserPreferences.Key.FIND_TEXT, newValue);
        });

        matchCase.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!(oldValue && newValue)) {
                isChanged.set(true);
            }
            UserPreferences.getInstance().setBoolean(UserPreferences.Key.MATCH_CASES, newValue);
        });

        wrapAround.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!(oldValue && newValue)) {
                isChanged.set(true);
            }
            UserPreferences.getInstance().setBoolean(UserPreferences.Key.WRAP_AROUND, newValue);
        });

    }

    @FXML
    public void handleFind(ActionEvent actionEvent) {
        if (isChanged.get()) {
            String query = searchField.getText().trim();
            Pattern pattern;
            Matcher matcher;

            if (matchCase.isSelected()) {
                pattern = Pattern.compile("(?<![\\w*])" + Pattern.quote(query) + "(?![\\w*])");
            } else {
                pattern = Pattern.compile("(?<![\\w*])" + Pattern.quote(query) + "(?![\\w*])", Pattern.CASE_INSENSITIVE);
            }

            if (wrapAround.isSelected()) {

                matcher = pattern.matcher(writingPad.getText());
                ResultIterator.getInstance().setWrapAround(wrapAround.isSelected());
                ResultIterator.getInstance().setDirection(false);
                ResultIterator.getInstance().setQueryAndResult(query, MatcherResultList.getList(matcher));
                ResultIterator.getInstance().setIterationStatus(-1);

            } else {

                matcher = pattern.matcher(writingPad.getText());
                ResultIterator.getInstance().setWrapAround(wrapAround.isSelected());
                ResultIterator.getInstance().setDirection(false);
                try {

                    matcher.region(writingPad.getCaretPosition(), writingPad.getDocument().getLength());
                    ResultIterator.getInstance().setQueryAndResult(query, MatcherResultList.getList(matcher));
                    ResultIterator.getInstance().setIterationStatus(-1);

                } catch (IndexOutOfBoundsException ignored) {

                    ResultIterator.getInstance().setQueryAndResult(query, MatcherResultList.getList(matcher));
                    ResultIterator.getInstance().setIterationStatus(-1);

                }

            }
            isChanged.set(false);
        }

        ResultIterator.getInstance().findNext();
    }

    @FXML
    public void handleReplace(ActionEvent actionEvent) {
        if (isChanged.get()) {
            String query = searchField.getText().trim();
            Pattern pattern;
            Matcher matcher;

            if (matchCase.isSelected()) {
                pattern = Pattern.compile("(?<![\\w*])" + Pattern.quote(query) + "(?![\\w*])");
            } else {
                pattern = Pattern.compile("(?<![\\w*])" + Pattern.quote(query) + "(?![\\w*])", Pattern.CASE_INSENSITIVE);
            }

            matcher = pattern.matcher(writingPad.getText());
            if (!wrapAround.isSelected()) {
                matcher.region(writingPad.getCaretPosition(), writingPad.getDocument().getLength());
            }

            ResultIterator.getInstance().setWrapAround(wrapAround.isSelected());
            ResultIterator.getInstance().setDirection(false);
            ResultIterator.getInstance().setQueryAndResult(query, MatcherResultList.getList(matcher));
            ResultIterator.getInstance().setReplaceableText(replaceableTextField.getText());
            ResultIterator.getInstance().setIterationStatus(-1);

            isChanged.set(false);
        }

        ResultIterator.getInstance().replace();
    }

    @FXML
    public void handleReplaceAll(ActionEvent actionEvent) {
        if (isChanged.get()) {
            String query = searchField.getText().trim();
            Pattern pattern;
            Matcher matcher;

            if (matchCase.isSelected()) {
                pattern = Pattern.compile("(?<![\\w*])" + Pattern.quote(query) + "(?![\\w*])");
            } else {
                pattern = Pattern.compile("(?<![\\w*])" + Pattern.quote(query) + "(?![\\w*])", Pattern.CASE_INSENSITIVE);
            }

            matcher = pattern.matcher(writingPad.getText());
            ResultIterator.getInstance().setWrapAround(wrapAround.isSelected());
            ResultIterator.getInstance().setDirection(false);
            ResultIterator.getInstance().setQueryAndResult(query, MatcherResultList.getList(matcher));
            ResultIterator.getInstance().setReplaceableText(replaceableTextField.getText());
            ResultIterator.getInstance().setIterationStatus(-1);
        }

        ResultIterator.getInstance().replaceAll();
    }

    @FXML
    public void handleCancel(ActionEvent actionEvent) {
        Window window = dialog.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    protected void setSource(JTextArea textPad) {
        this.writingPad = textPad;
        ResultIterator.getInstance().setSource(textPad);
    }

//    private String formatQuery(String query) {
//        return query.trim().replaceAll("(?=[]\\[\\p{Punct}])", "\\\\");
//    }
}
