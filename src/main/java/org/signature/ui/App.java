package org.signature.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static GraphicsEnvironment graphicsEnvironment;

    @Override
    public void init() throws Exception {
        super.init();
        try {
            graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Poppins-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Poppins-Italic.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Poppins-BoldItalic.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Poppins-Thin.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Kalam-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Kalam-Bold.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Karma-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Karma-Bold.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Galada-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/HindSiliguri-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/HindSiliguri-Bold.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Farsan-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/HindVadodara-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/HindVadodara-Bold.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/MuktaVaani-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/MuktaVaani-Bold.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Shrikhand-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/MuktaMahee-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/MuktaMahee-Bold.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/BalooChettan2-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/BalooChettan2-Bold.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/BalooBhaina2-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/BalooBhaina2-Bold.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/HindMadurai-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/HindMadurai-Bold.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/HindGuntur-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/HindGuntur-Bold.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Italic.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Bold.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-BoldItalic.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Thin.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-ThinItalic.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Medium.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-MediumItalic.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Black.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-BlackItalic.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Light.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-LightItalic.ttf")));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("TextPad"));
        stage.setScene(scene);
        stage.setTitle("Untitled - Notepad");
        stage.setMinWidth(640.0);
        stage.setMinHeight(240.0);
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

    public static GraphicsEnvironment getGraphicsEnvironment() {
        return graphicsEnvironment;
    }

    public static void main(String[] args) {
        launch();
    }

}