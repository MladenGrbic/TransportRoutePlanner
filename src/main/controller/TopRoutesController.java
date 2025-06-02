package main.controller;

import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.model.City;
import main.util.TicketUtil;
import main.transport.Network;
import main.transport.Route;
import java.io.IOException;

/**
 * Kontroler za prikaz top 5 ruta između dva grada.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class TopRoutesController {
    @FXML private TableView<Route> routesTable;
    private List<Route> topRoutes;
    private City endCity;
    private City startCity;

    /**
     * Postavlja transportnu mrežu za kontroler.
     *
     * @param network Transportna mreža.
     */
    public void setNetwork(Network network) {
    }

    /**
     * Postavlja listu ruta i gradove za prikaz.
     *
     * @param topRoutes Lista ruta za prikaz.
     * @param startCity Početni grad.
     * @param endCity Krajnji grad.
     */
    public void setTopRoutes(List<Route> topRoutes, City startCity, City endCity) {
        this.topRoutes = topRoutes;
        this.startCity = startCity;
        this.endCity = endCity;
        System.out.println("Broj prosleđenih ruta za TopRoutes: " + topRoutes.size());
        initializeTable();
    }

    /**
     * Postavlja krajnji grad i osvježava tabelu.
     *
     * @param endCity Krajnji grad.
     */
    public void setEndCity(City endCity) {
        this.endCity = endCity;
        if (topRoutes != null) {
            initializeTable();
        }
    }

    /**
     * Inicijalizuje tabelu sa rutama i postavlja kolone.
     */
    private void initializeTable() {
        TableColumn<Route, String> pathColumn = new TableColumn<>("Putanja");
        pathColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSegmentedDescription(endCity)));

        TableColumn<Route, String> timeColumn = new TableColumn<>("Vrijeme");
        timeColumn.setCellValueFactory(cellData -> {
            int totalTime = cellData.getValue().getTotalTime();
            int hours = totalTime / 60;
            int minutes = totalTime % 60;
            return new SimpleStringProperty(String.format("%dh%02dm", hours, minutes));
        });

        TableColumn<Route, Integer> priceColumn = new TableColumn<>("Cijena");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        TableColumn<Route, Integer> transfersColumn = new TableColumn<>("Presjedanja");
        transfersColumn.setCellValueFactory(new PropertyValueFactory<>("transferCount"));

        TableColumn<Route, Void> actionColumn = new TableColumn<>("Akcija");
        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button buyButton = new Button("Kupi kartu");

            {
                buyButton.setOnAction(event -> {
                    Route route = getTableView().getItems().get(getIndex());
                    try {
                        TicketUtil newTicketUtil = new TicketUtil();
                        String receiptPath = newTicketUtil.generateReceipt(route, startCity, endCity);
                        System.out.println("Račun kreiran: " + receiptPath);
                        showAlert("Kupovina uspešna", "Karta je uspešno kupljena! Račun je sačuvan na: " + receiptPath);
                    } catch (IOException e) {
                        showAlert("Greška", "Došlo je do greške prilikom kreiranja računa: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buyButton);
            }
        });

        routesTable.getColumns().setAll(pathColumn, timeColumn, priceColumn, transfersColumn, actionColumn);
        routesTable.getItems().setAll(topRoutes);
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