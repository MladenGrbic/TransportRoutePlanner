package main.controller;

import dataGenerator.TransportDataGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import main.transport.Network;

import java.io.IOException;

/**
 * Kontroler za unos dimenzija mreže gradova i generisanje transportnih podataka.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class MapSizeController {
    @FXML private TextField nField;
    @FXML private TextField mField;
    @FXML private Button createButton;
    @FXML private Button showStatisticsButton;

    private Network network;
    private Stage primaryStage;
    private RoutePlanningController routePlanningController;

    /**
     * Postavlja transportnu mrežu za kontroler.
     *
     * @param network Transportna mreža.
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * Postavlja primarnu scenu za kontroler.
     *
     * @param primaryStage Glavna JavaFX scena.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Postavlja kontroler za planiranje ruta.
     *
     * @param controller Kontroler za planiranje ruta.
     */
    public void setRoutePlanningController(RoutePlanningController controller) {
        this.routePlanningController = controller;
    }

    /**
     * Inicijalizuje kontroler, postavlja akcije za dugmad.
     */
    @FXML
    private void initialize() {
        createButton.setOnAction(event -> {
            try {
                int n = Integer.parseInt(nField.getText());
                int m = Integer.parseInt(mField.getText());
                TransportDataGenerator generator = new TransportDataGenerator(n, m);
                TransportDataGenerator.TransportData data = generator.generateData();
                generator.saveToJson(data, "src/main/resources/transport_data.json");
                network.loadFromJson("src/main/resources/transport_data.json");
                System.out.println("Učitani gradovi nakon JSON-a: " + network.getCities().size());
                network.setGridRows(n);
                network.setGridCols(m);
                routePlanningController.setup();
                primaryStage.setScene(routePlanningController.getScene());
                primaryStage.setMaximized(true);
            } catch (NumberFormatException ex) {
                showAlert("Nevažeći unos", "Unesite validne brojeve za n i m.");
            } catch (Exception ex) {
                showAlert("Greška", "Greška pri učitavanju JSON-a: " + ex.getMessage());
            }
        });

        showStatisticsButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/StatisticsWindow.fxml"));
                Parent root = loader.load();
                Stage statisticsStage = new Stage();
                statisticsStage.setTitle("Statistika prodaje");
                statisticsStage.setScene(new Scene(root, 300, 150));
                statisticsStage.show();
            } catch (IOException e) {
                showAlert("Greška", "Došlo je do greške pri otvaranju prozora sa statistikom: " + e.getMessage());
                e.printStackTrace();
            }
        });
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
}