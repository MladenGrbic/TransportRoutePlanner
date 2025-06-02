package main.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Predstavlja kartu kupljenu za putovanje između dva grada.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class Ticket {
    private final String route; // Format: "G_0_0 -> G_1_1"
    private final String segmentedRoute; // Detalji putanje iz Route.getSegmentedDescription
    private final int totalTime; // U minutama
    private final int price; // U novčanim jedinicama
    private final int transfers;
    private final LocalDateTime purchaseDate;

    /**
     * Konstruktor za kreiranje karte.
     *
     * @param route Relacija putovanja (npr. "G_0_0 -> G_1_1").
     * @param segmentedRoute Detaljan opis rute.
     * @param totalTime Ukupno vrijeme putovanja u minutama.
     * @param price Cijena karte u novčanim jedinicama.
     * @param transfers Broj presjedanja na ruti.
     * @param purchaseDate Datum i vrijeme kupovine karte.
     */
    public Ticket(String route, String segmentedRoute, int totalTime, int price, int transfers, LocalDateTime purchaseDate) {
        this.route = route;
        this.segmentedRoute = segmentedRoute;
        this.totalTime = totalTime;
        this.price = price;
        this.transfers = transfers;
        this.purchaseDate = purchaseDate;
    }

    /**
     * Vraća relaciju putovanja.
     *
     * @return Relacija u formatu "G_0_0 -> G_1_1".
     */
    public String getRoute() {
        return route;
    }

    /**
     * Vraća detaljan opis rute.
     *
     * @return Opis rute iz Route.getSegmentedDescription.
     */
    public String getSegmentedRoute() {
        return segmentedRoute;
    }

    /**
     * Vraća ukupno vrijeme putovanja.
     *
     * @return Vrijeme u minutama.
     */
    public int getTotalTime() {
        return totalTime;
    }

    /**
     * Vraća cenu karte.
     *
     * @return Cijena u novčanim jedinicama.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Vraća broj presjedanja na ruti.
     *
     * @return Broj presjedanja.
     */
    public int getTransfers() {
        return transfers;
    }

    /**
     * Vraća datum i vrijeme kupovine karte.
     *
     * @return Datum i vrijeme kupovine.
     */
    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * Serijalizuje kartu u tekstualni format za čuvanje u fajl.
     *
     * @return Tekstualni format računa.
     */
    public String toFileFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String sb = "Racun\n" +
                "Datum kupovine: " + purchaseDate.format(formatter) + "\n" +
                "Relacija: " + route + "\n" +
                "Putanja: " + segmentedRoute.replace("\n", "|") + "\n" +
                "Vrijeme trajanja: " + totalTime + "\n" +
                "Cijena: " + price + "\n" +
                "Broj presjedanja: " + transfers + "\n" +
                "Kraj\n";
        return sb;
    }

    /**
     * Parsira kartu iz tekstualnog formata.
     *
     * @param content Tekstualni sadržaj računa.
     * @return Objekat karte.
     * @throws IllegalArgumentException Ako je format neispravan ili nedostaju podaci.
     */
    public static Ticket fromFileFormat(String content) {
        String[] lines = content.split("\n");
        if (!lines[0].equals("Racun") || !lines[lines.length - 1].equals("Kraj")) {
            throw new IllegalArgumentException("Neispravan format računa");
        }

        String route = null;
        String segmentedRoute = null;
        int totalTime = 0;
        int price = 0;
        int transfers = 0;
        LocalDateTime purchaseDate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (int i = 1; i < lines.length - 1; i++) {
            String[] parts = lines[i].split(": ", 2);
            if (parts.length != 2) continue;
            switch (parts[0]) {
                case "Datum kupovine":
                    purchaseDate = LocalDateTime.parse(parts[1], formatter);
                    break;
                case "Relacija":
                    route = parts[1];
                    break;
                case "Putanja":
                    segmentedRoute = parts[1].replace("|", "\n");
                    break;
                case "Vrijeme trajanja":
                    totalTime = Integer.parseInt(parts[1]);
                    break;
                case "Cijena":
                    price = Integer.parseInt(parts[1]);
                    break;
                case "Broj presjedanja":
                    transfers = Integer.parseInt(parts[1]);
                    break;
            }
        }

        if (route == null || segmentedRoute == null || purchaseDate == null) {
            throw new IllegalArgumentException("Nedostaju podaci u računu");
        }

        return new Ticket(route, segmentedRoute, totalTime, price, transfers, purchaseDate);
    }
}