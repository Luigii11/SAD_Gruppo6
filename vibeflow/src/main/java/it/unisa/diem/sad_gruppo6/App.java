package it.unisa.diem.sad_gruppo6;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Locale;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) {
        Locale.setDefault(Locale.ENGLISH);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/home/Home.fxml"));
            Parent root = fxmlLoader.load();
            
            scene = new Scene(root);
            stage.setTitle("VibeFlow");
            try {
                Image icon = new Image(getClass().getResourceAsStream("/images/VibeFlow.png"));
                stage.getIcons().add(icon);
            } catch (Exception e) {
                System.out.println("Nota: Impossibile caricare l'icona della finestra: " + e.getMessage());
            }
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Errore nel caricamento del file FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static <T> T setRootAndGetController(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/" + fxml + ".fxml"));
        Parent root = loader.load();
        scene.setRoot(root);
        return loader.getController();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}