package org.signature.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.signature.util.MailUtil;

public class FeedbackController {

    @FXML
    private DialogPane dialog;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private SVGPath r1;
    @FXML
    private SVGPath r2;
    @FXML
    private SVGPath r3;
    @FXML
    private SVGPath r4;
    @FXML
    private SVGPath r5;
    @FXML
    private Label reset;
    @FXML
    private TextArea reviewField;
    @FXML
    private Button submit;

    int rating = 0;

    public void initialize() {

        /*Setting effect for rating stars.*/
        r1.setOnMouseEntered(event -> r1.setEffect(new DropShadow()));
        r1.setOnMouseExited(event -> r1.setEffect(null));
        r2.setOnMouseEntered(event -> r2.setEffect(new DropShadow()));
        r2.setOnMouseExited(event -> r2.setEffect(null));
        r3.setOnMouseEntered(event -> r3.setEffect(new DropShadow()));
        r3.setOnMouseExited(event -> r3.setEffect(null));
        r4.setOnMouseEntered(event -> r4.setEffect(new DropShadow()));
        r4.setOnMouseExited(event -> r4.setEffect(null));
        r5.setOnMouseEntered(event -> r5.setEffect(new DropShadow()));
        r5.setOnMouseExited(event -> r5.setEffect(null));
        /*-------------------------------------------------------------*/

        r1.setOnMouseClicked(event -> setRatingValue(1));
        r2.setOnMouseClicked(event -> setRatingValue(2));
        r3.setOnMouseClicked(event -> setRatingValue(3));
        r4.setOnMouseClicked(event -> setRatingValue(4));
        r5.setOnMouseClicked(event -> setRatingValue(5));

        reset.setOnMouseClicked(event -> {
            clearRating();
            rating = 0;
            reset.setVisible(false);
        });

        submit.disableProperty().bind(nameField.textProperty().isEmpty().or(emailField.textProperty().isEmpty()));
    }

    private void setRatingValue(int value) {
        clearRating();

        if (value >= 1 && value <= 5) {
            reset.setVisible(true);

            r1.setFill(Color.GOLD);
            rating = 1;
            if (value == 1) return;

            r2.setFill(Color.GOLD);
            rating = 2;
            if (value == 2) return;

            r3.setFill(Color.GOLD);
            rating = 3;
            if (value == 3) return;

            r4.setFill(Color.GOLD);
            rating = 4;
            if (value == 4) return;

            r5.setFill(Color.GOLD);
            rating = 5;
        }
    }

    private void clearRating() {
        r1.setFill(Color.WHITE);
        r2.setFill(Color.WHITE);
        r3.setFill(Color.WHITE);
        r4.setFill(Color.WHITE);
        r5.setFill(Color.WHITE);
    }

    @FXML
    void handleSubmit(ActionEvent event) {
        String user = nameField.getText();
        StringBuilder messageContent = new StringBuilder();
        messageContent.append("<html><body>");
        messageContent.append("<h1>User Detail</h1>");
        messageContent.append("<p>Name : ").append(user).append("<br>");
        messageContent.append("Email : ").append(emailField.getText()).append("</p><br>");
        messageContent.append("<center><h1><font size = '48'>").append(user).append(" has given ").append(rating).append(" star to Notepad</font></h1></center>");
        messageContent.append("<h2>Review : </h2>");
        messageContent.append("<h3>").append(reviewField.getText()).append("</h3>");
        messageContent.append("</body></html>");

        if (MailUtil.sendMail(user, messageContent.toString())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successful");
            alert.setHeaderText(null);
            alert.setContentText("Thank you for you review!");
            alert.showAndWait();
            handleCancel(null);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Failed to submit review!\nPlease try again!");
            alert.showAndWait();
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        Window window = dialog.getScene().getWindow();
        dialog.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
