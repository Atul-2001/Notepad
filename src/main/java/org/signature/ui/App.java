package org.signature.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("TextPad"));
        stage.setScene(scene);
        stage.setTitle("Untitled - Notepad");
        stage.setMinWidth(900.0);
        stage.setMinHeight(400.0);
        stage.getIcons().add(new Image(getClass().getResource("/icons/notepad-icon.png").toString()));
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Stage getStage() {
        return (Stage) scene.getWindow();
    }

    public static void main(String[] args) {
        launch();
    }
}