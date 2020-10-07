package org.signature.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.signature.model.Position;
import org.signature.util.MatcherResultList;
import org.signature.util.ResultIterator;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindTextDialogController {

    @FXML
    private DialogPane dialog;
    @FXML
    private TextField searchField;
    @FXML
    private Button findButton;
    @FXML
    private CheckBox matchCase, wrapAround;
    @FXML
    private RadioButton upDirection, downDirection;

    private JTextArea writingPad;
    private final AtomicBoolean isChanged = new AtomicBoolean(false);
    private static Map<String, Boolean> previousStatus;

    public void initialize() {
        previousStatus = TextPadController.getPreviousStatus();
        if (previousStatus.isEmpty()) {
            previousStatus.put(matchCase.getText(), false);
            previousStatus.put(wrapAround.getText(), false);
            previousStatus.put(upDirection.getText(), false);
            previousStatus.put(downDirection.getText(), true);
        }

        matchCase.setSelected(previousStatus.get(matchCase.getText()));
        wrapAround.setSelected(previousStatus.get(wrapAround.getText()));
        upDirection.setSelected(previousStatus.get(upDirection.getText()));
        downDirection.setSelected(previousStatus.get(downDirection.getText()));

        findButton.disableProperty().bind(searchField.textProperty().isEmpty());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                isChanged.set(true);
            }
        });

        matchCase.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!(oldValue && newValue)) {
                isChanged.set(true);
            }
            previousStatus.replace(matchCase.getText(), newValue);
        });

        wrapAround.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!(oldValue && newValue)) {
                isChanged.set(true);
            }
            previousStatus.replace(wrapAround.getText(), newValue);
        });

        upDirection.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!(oldValue && newValue)) {
                isChanged.set(true);
            }
            previousStatus.replace(upDirection.getText(), newValue);
        });

        downDirection.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!(oldValue && newValue)) {
                isChanged.set(true);
            }
            previousStatus.replace(downDirection.getText(), newValue);
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
                ResultIterator.getInstance().setDirection(upDirection.isSelected());
                ResultIterator.getInstance().setQueryAndResult(query, MatcherResultList.getList(matcher));
                ResultIterator.getInstance().setIterationStatus(-1);

            } else {

                matcher = pattern.matcher(writingPad.getText());
                if (upDirection.isSelected()) {

                    ResultIterator.getInstance().setWrapAround(wrapAround.isSelected());
                    ResultIterator.getInstance().setDirection(upDirection.isSelected());
                    try {

                        matcher.region(0, writingPad.getCaretPosition());
                        List<Position> resultList = MatcherResultList.getList(matcher);
                        ResultIterator.getInstance().setQueryAndResult(query, resultList);
                        ResultIterator.getInstance().setIterationStatus(resultList.size());

                    } catch (IndexOutOfBoundsException ignored) {

                        List<Position> resultList = MatcherResultList.getList(matcher);
                        ResultIterator.getInstance().setQueryAndResult(query, resultList);
                        ResultIterator.getInstance().setIterationStatus(resultList.size());

                    }
                } else if (downDirection.isSelected()) {

                    ResultIterator.getInstance().setWrapAround(wrapAround.isSelected());
                    ResultIterator.getInstance().setDirection(upDirection.isSelected());
                    try {

                        matcher.region(writingPad.getCaretPosition(), writingPad.getDocument().getLength());
                        ResultIterator.getInstance().setQueryAndResult(query, MatcherResultList.getList(matcher));
                        ResultIterator.getInstance().setIterationStatus(-1);

                    } catch (IndexOutOfBoundsException ignored) {

                        ResultIterator.getInstance().setQueryAndResult(query, MatcherResultList.getList(matcher));
                        ResultIterator.getInstance().setIterationStatus(-1);

                    }
                } else {

                    matcher = pattern.matcher(writingPad.getText());
                    ResultIterator.getInstance().setQueryAndResult(query, MatcherResultList.getList(matcher));
                    ResultIterator.getInstance().setIterationStatus(-1);

                }

            }
            isChanged.set(false);
        }

        ResultIterator.getInstance().findNext();
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
//       return query.trim().replaceAll("(?=[]\\[\\p{Punct}])", "\\\\");
//    }
}
