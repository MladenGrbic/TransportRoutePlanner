package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import main.controller.MapSizeController;
import main.controller.RoutePlanningController;
import main.transport.Network;

import java.io.IOException;

/* -global user.email "you@example.com" git config --global user.name "Your Name" to set your account's default identity. Omit --global to set the identity only in this repository.*/

/**
 * Glavna klasa JavaFX aplikacije za planiranje transportnih ruta.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class TransportMain extends Application {
    private Network network;

    /**
     * Pokreće JavaFX aplikaciju i inicijalizuje scenu za unos dimenzija mreže.
     *
     * @param primaryStage Glavna scena aplikacije.
     * @throws IOException Ako dođe do greške pri učitavanju FXML fajlova.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        network = new Network();

        // Učitaj MapSize.fxml
        FXMLLoader mapSizeLoader = new FXMLLoader(getClass().getResource("/MapSize.fxml"));
        if (mapSizeLoader.getLocation() == null) {
            throw new IOException("Ne može se pronaći MapSize.fxml");
        }
        Scene mapSizeScene = new Scene(mapSizeLoader.load(), 400, 300);
        MapSizeController mapSizeController = mapSizeLoader.getController();

        // Učitaj RoutePlanning.fxml
        FXMLLoader routePlanningLoader = new FXMLLoader(getClass().getResource("/RoutePlanning.fxml"));
        if (routePlanningLoader.getLocation() == null) {
            throw new IOException("Ne može se pronaći RoutePlanning.fxml");
        }
        Scene routePlanningScene = new Scene(routePlanningLoader.load(), 1000, 600);
        RoutePlanningController routePlanningController = routePlanningLoader.getController();

        // Postavi zavisnosti
        mapSizeController.setNetwork(network);
        mapSizeController.setPrimaryStage(primaryStage);
        mapSizeController.setRoutePlanningController(routePlanningController);
        routePlanningController.setNetwork(network);
        routePlanningController.setScene(routePlanningScene);

        // Postavi početnu scenu
        primaryStage.setTitle("Veličina države");
        primaryStage.setScene(mapSizeScene);
        primaryStage.show();
    }

    /**
     * Prikazuje dijalog sa obaveštenjem.
     *
     * @param title Naslov dijaloga.
     * @param message Poruka dijaloga.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Ulazna tačka aplikacije.
     *
     * @param args Argumenti komandne linije.
     */
    public static void main(String[] args) {
        launch(args);
    }
}