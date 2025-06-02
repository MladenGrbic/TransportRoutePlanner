package main.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.model.*;
import main.transport.Network;
import main.transport.Route;
import main.util.TicketUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Kontroler za planiranje ruta u JavaFX aplikaciji za pretraživanje transportnih ruta.
 * Omogućava odabir početnog i krajnjeg grada, kriterijuma pretrage i prikaz optimalne rute.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class RoutePlanningController {
    @FXML private ComboBox<String> startCityCombo;
    @FXML private ComboBox<String> endCityCombo;
    @FXML private RadioButton timeRadio;
    @FXML private RadioButton priceRadio;
    @FXML private RadioButton transfersRadio;
    @FXML private ToggleGroup criteriaGroup;
    @FXML private Button findRoutesButton;
    @FXML private Label pathLabel;
    @FXML private Label timeLabel;
    @FXML private Label priceLabel;
    @FXML private Label transfersLabel;
    @FXML private Button buyButton;
    @FXML private Canvas canvas;

    private Network network;
    private Scene scene;
    private Route selectedRoute;
    private City endCity;
    private Button showTopRoutesButton;
    private Stage topRoutesStage;
    private TopRoutesController topRoutesController;
    private City previousStartCity;
    private City previousEndCity;

    /**
     * Postavlja transportnu mrežu za kontroler.
     *
     * @param network Transportna mreža.
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * Postavlja scenu za kontroler.
     *
     * @param scene JavaFX scena.
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    /**
     * Vraća trenutnu scenu.
     *
     * @return JavaFX scena.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Inicijalizuje ComboBox-ove sa imenima gradova i priprema ekran za crtanje.
     */
    public void setup() {
        if (network == null) {
            showAlert("Greška", "Mreža nije inicijalizovana.");
            System.out.println("Network je null!");
            return;
        }
        Map<String, City> cities = network.getCities();
        System.out.println("Broj gradova u setup: " + cities.size());
        List<String> cityNames = cities.keySet().stream().sorted().collect(Collectors.toList());
        System.out.println("Gradovi u setup: " + cityNames);
        startCityCombo.getItems().setAll(cityNames);
        endCityCombo.getItems().setAll(cityNames);
        drawGraph(null);
    }

    /**
     * Inicijalizuje kontroler, postavlja grupu radio dugmadi i akcije za dugmad.
     */
    @FXML
    private void initialize() {
        criteriaGroup = new ToggleGroup();
        timeRadio.setToggleGroup(criteriaGroup);
        priceRadio.setToggleGroup(criteriaGroup);
        transfersRadio.setToggleGroup(criteriaGroup);

        findRoutesButton.setOnAction(event -> {
            if (network == null) {
                showAlert("Greška", "Mreža nije inicijalizovana.");
                return;
            }
            String startCityName = startCityCombo.getValue();
            String endCityName = endCityCombo.getValue();
            if (startCityName == null || endCityName == null) {
                showAlert("Greška u izboru", "Izaberite početni i krajnji grad.");
                return;
            }

            Map<String, City> cities = network.getCities();
            City startCity = cities.get(startCityName);
            this.endCity = cities.get(endCityName);
            String criterion = timeRadio.isSelected() ? "time" : priceRadio.isSelected() ? "price" : "transfers";
            int startTime = 0;

            List<Route> routes = network.findRoutes(startCity, endCity, criterion, startTime);

            if (routes.isEmpty()) {
                showAlert("Nema ruta", "Nisu pronađene rute između izabranih gradova.");
                selectedRoute = null;
                pathLabel.setText("");
                timeLabel.setText("");
                priceLabel.setText("");
                transfersLabel.setText("");
                buyButton.setDisable(true);
                drawGraph(null);
                previousStartCity = null;
                previousEndCity = null;
                return;
            }

            selectedRoute = routes.get(0);
            pathLabel.setText(selectedRoute.getSegmentedDescription(endCity));
            int totalTime = selectedRoute.getTotalTime();
            int hours = totalTime / 60;
            int minutes = totalTime % 60;
            timeLabel.setText(String.format("%dh%02dm", hours, minutes));
            priceLabel.setText(String.valueOf(selectedRoute.getTotalPrice()));
            transfersLabel.setText(String.valueOf(selectedRoute.getTransferCount()));
            buyButton.setDisable(false);

            System.out.println("Optimalna ruta od " + startCityName + " do " + endCityName + " (kriterijum: " + criterion + "):");
            System.out.print("Čvorovi: " + selectedRoute.startStation().getName());
            for (Edge edge : selectedRoute.getEdges()) {
                System.out.print(" -> " + edge.getTo().getName());
            }
            System.out.println();
            System.out.println("Detalji rute: " + selectedRoute.getSegmentedDescription(endCity));

            drawGraph(selectedRoute, startCity, endCity);

            if (showTopRoutesButton == null) {
                showTopRoutesButton = new Button("Prikaz dodatnih ruta");
                ((VBox) findRoutesButton.getParent()).getChildren().add(showTopRoutesButton);
            }

            showTopRoutesButton.setOnAction(e -> {
                try {
                    if (topRoutesStage == null || !topRoutesStage.isShowing()) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TopRoutesWindow.fxml"));
                        Parent root = loader.load();
                        topRoutesController = loader.getController();
                        topRoutesController.setNetwork(network);
                        topRoutesController.setTopRoutes(routes, startCity, endCity);
                        topRoutesController.setEndCity(endCity);
                        topRoutesStage = new Stage();
                        topRoutesStage.setTitle("Top 5 ruta");
                        topRoutesStage.setScene(new Scene(root, 600, 400));
                        topRoutesStage.show();
                    } else {
                        topRoutesController.setTopRoutes(routes, startCity, endCity);
                        topRoutesController.setEndCity(endCity);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            previousStartCity = startCity;
            previousEndCity = endCity;
        });

        buyButton.setOnAction(event -> {
            if (selectedRoute != null) {
                try {
                    TicketUtil newTicketUtil = new TicketUtil();
                    City startCity = network.getCities().get(startCityCombo.getValue());
                    String receiptPath = newTicketUtil.generateReceipt(selectedRoute, startCity, endCity);
                    System.out.println("Račun kreiran: " + receiptPath);
                    showAlert("Kupovina uspešna", "Karta je uspešno kupljena! Račun je sačuvan na: " + receiptPath);
                } catch (IOException e) {
                    showAlert("Greška", "Došlo je do greške prilikom kreiranja računa: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Crta graf gradova i ruta na ekranu.
     *
     * @param selectedRoute Odabrana ruta za prikaz, ili null ako se prikazuje samo graf.
     * @param startCity Početni grad, ili null ako nije specificiran.
     * @param endCity Krajnji grad, ili null ako nije specificiran.
     */
    private void drawGraph(Route selectedRoute, City startCity, City endCity) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (network == null) return;

        double width = canvas.getWidth();
        double height = canvas.getHeight();
        int rows = network.getCities().values().stream().mapToInt(City::getRow).max().orElse(4) + 1;
        int cols = network.getCities().values().stream().mapToInt(City::getColumn).max().orElse(4) + 1;
        double cellWidth = width / cols;
        double cellHeight = height / rows;

        List<City> routeCities = new ArrayList<>();
        if (selectedRoute != null && selectedRoute.getEdges() != null && !selectedRoute.getEdges().isEmpty()) {
            Station startStation = selectedRoute.startStation();
            if (startStation != null && startStation.getCity() != null) routeCities.add(startStation.getCity());
            for (Edge edge : selectedRoute.getEdges()) {
                if (!edge.isTransfer() && edge.getTo() != null && edge.getTo().getCity() != null) {
                    routeCities.add(edge.getTo().getCity());
                }
            }
        }

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        for (Station station : network.getGraph().getNodes()) {
            if (station == null || station.getCity() == null) continue;
            City fromCity = station.getCity();
            double x1 = fromCity.getColumn() * cellWidth + cellWidth / 2;
            double y1 = fromCity.getRow() * cellHeight + cellHeight / 2;
            for (Edge edge : network.getGraph().getEdges(station)) {
                if (!edge.isTransfer() && edge.getTo() != null && edge.getTo().getCity() != null) {
                    City toCity = edge.getTo().getCity();
                    double x2 = toCity.getColumn() * cellWidth + cellWidth / 2;
                    double y2 = toCity.getRow() * cellHeight + cellHeight / 2;
                    gc.strokeLine(x1, y1, x2, y2);
                }
            }
        }

        if (!routeCities. isEmpty() && routeCities.size() > 1) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(4);
            for (int i = 0; i < routeCities.size() - 1; i++) {
                City fromCity = routeCities.get(i);
                City toCity = routeCities.get(i + 1);
                if (fromCity != null && toCity != null) {
                    double x1 = fromCity.getColumn() * cellWidth + cellWidth / 2;
                    double y1 = fromCity.getRow() * cellHeight + cellHeight / 2;
                    double x2 = toCity.getColumn() * cellWidth + cellWidth / 2;
                    double y2 = toCity.getRow() * cellHeight + cellHeight / 2;
                    gc.strokeLine(x1, y1, x2, y2);
                }
            }
        }

        for (City city : network.getCities().values()) {
            if (city == null) continue;
            double x = city.getColumn() * cellWidth + cellWidth / 2;
            double y = city.getRow() * cellHeight + cellHeight / 2;

            if (city.equals(startCity)) {
                gc.setFill(Color.GREEN);
            } else if (city.equals(endCity)) {
                gc.setFill(Color.BLUE);
            } else {
                gc.setFill(Color.GRAY);
            }

            gc.fillOval(x - 10, y - 10, 20, 20);
            gc.setFill(Color.BLACK);
            gc.fillText(city.getName(), x + 10, y - 10);
        }
    }

    /**
     * Crta graf bez specifične rute.
     *
     * @param selectedRoute Odabrana ruta za prikaz, ili null.
     */
    private void drawGraph(Route selectedRoute) {
        drawGraph(selectedRoute, null, null);
    }

    /**
     * Prikazuje dijalog sa obavještenjem.
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