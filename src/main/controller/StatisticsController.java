package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import main.model.Ticket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kontroler za prikaz statistike prodatih karata.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class StatisticsController {
    @FXML private Label ticketsSoldLabel;
    @FXML private Label totalRevenueLabel;

    /**
     * Inicijalizuje kontroler i prikazuje statistiku prodatih karata.
     */
    @FXML
    private void initialize() {
        Statistics stats = calculateStatistics();
        ticketsSoldLabel.setText("Ukupan broj prodatih karata: " + stats.getTotalTickets());
        totalRevenueLabel.setText("Ukupan prihod: " + stats.getTotalRevenue() + " novčanih jedinica");
    }

    /**
     * Izračunava statistiku prodatih karata na osnovu fajlova u direktorijumu "racuni".
     *
     * @return Objekat sa ukupnim brojem karata i prihodom.
     */
    private Statistics calculateStatistics() {
        Path receiptsDir = Paths.get("racuni");
        if (!Files.exists(receiptsDir)) {
            return new Statistics(0, 0);
        }

        try {
            List<Ticket> tickets = Files.list(receiptsDir)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .map(path -> {
                        try {
                            String content = Files.readString(path);
                            return Ticket.fromFileFormat(content);
                        } catch (IOException | IllegalArgumentException e) {
                            System.err.println("Greška pri učitavanju računa: " + path + " - " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(ticket -> ticket != null)
                    .collect(Collectors.toList());

            int totalTickets = tickets.size();
            int totalRevenue = tickets.stream().mapToInt(Ticket::getPrice).sum();

            return new Statistics(totalTickets, totalRevenue);
        } catch (IOException e) {
            System.err.println("Greška pri čitanju foldera racuni: " + e.getMessage());
            return new Statistics(0, 0);
        }
    }

    /**
     * Unutrašnja klasa za čuvanje statističkih podataka.
     */
    private static class Statistics {
        private final int totalTickets;
        private final int totalRevenue;

        /**
         * Konstruktor za statistiku.
         *
         * @param totalTickets Ukupan broj prodatih karata.
         * @param totalRevenue Ukupan prihod od prodatih karata.
         */
        public Statistics(int totalTickets, int totalRevenue) {
            this.totalTickets = totalTickets;
            this.totalRevenue = totalRevenue;
        }

        /**
         * Vraća ukupan broj prodatih karata.
         *
         * @ return Broj karata.
         */
        public int getTotalTickets() {
            return totalTickets;
        }

        /**
         * Vraća ukupan prihod od prodatih karata.
         *
         * @return Prihod u novčanim jedinicama.
         */
        public int getTotalRevenue() {
            return totalRevenue;
        }
    }
}