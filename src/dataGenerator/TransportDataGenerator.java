package dataGenerator;

import java.io.*;
import java.util.*;

/**
 * Generiše transportne podatke za simulaciju mreže gradova, stanica i polazaka.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class TransportDataGenerator {
    private final int rows; // Broj redova mreže
    private final int cols; // Broj kolona mreže
    private static final int DEPARTURES_PER_STATION = 20; // Broj polazaka po stanici
    private static final Random random = new Random(); // Generator slučajnih brojeva

    /**
     * Konstruktor za inicijalizaciju generatora sa dimenzijama mreže.
     *
     * @param rows Broj redova mreže.
     * @param cols Broj kolona mreže.
     */
    public TransportDataGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * Struktura za čuvanje generisanih transportnih podataka.
     */
    public static class TransportData {
        public String[][] countryMap; // Matrica gradova
        public List<Station> stations; // Lista stanica
        public List<Departure> departures; // Lista polazaka
    }

    /**
     * Predstavlja stanicu sa autobuskom i željezničkom stanicom u gradu.
     */
    public static class Station {
        public String city; // Ime grada (npr. "G_0_0")
        public String busStation; // Ime autobuske stanice (npr. "A_0_0")
        public String trainStation; // Ime železničke stanice (npr. "Z_0_0")
    }

    /**
     * Predstavlja polazak sa stanice ka drugom gradu.
     */
    public static class Departure {
        public String type; // Tip prevoza ("autobus" ili "voz")
        public String from; // Polazna stanica
        public String to; // Destinacijski grad
        public String departureTime; // Vreme polaska (format "HH:mm")
        public int duration; // Trajanje putovanja u minutama
        public int price; // Cena putovanja
        public int minTransferTime; // Minimalno vreme za transfer u minutama
    }

    /**
     * Generiše kompletne transportne podatke (mapu, stanice, polaske).
     *
     * @return Objekat sa generisanim podacima.
     */
    public TransportData generateData() {
        TransportData data = new TransportData();
        data.countryMap = generateCountryMap();
        data.stations = generateStations();
        data.departures = generateDepartures(data.stations);
        return data;
    }

    /**
     * Generiše matricu gradova u mreži.
     *
     * @return Matrica sa imenima gradova (npr. "G_x_y").
     */
    private String[][] generateCountryMap() {
        String[][] countryMap = new String[rows][cols];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                countryMap[x][y] = "G_" + x + "_" + y;
            }
        }
        return countryMap;
    }

    /**
     * Generiše listu stanica za sve gradove u mreži.
     *
     * @return Lista stanica sa autobuskim i željezničkim stanicama.
     */
    private List<Station> generateStations() {
        List<Station> stations = new ArrayList<>();
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                Station station = new Station();
                station.city = "G_" + x + "_" + y;
                station.busStation = "A_" + x + "_" + y;
                station.trainStation = "Z_" + x + "_" + y;
                stations.add(station);
            }
        }
        return stations;
    }

    /**
     * Generiše polaske za sve stanice.
     *
     * @param stations Lista stanica iz kojih se generišu polasci.
     * @return Lista generisanih polazaka.
     */
    private List<Departure> generateDepartures(List<Station> stations) {
        List<Departure> departures = new ArrayList<>();
        for (Station station : stations) {
            int x = Integer.parseInt(station.city.split("_")[1]);
            int y = Integer.parseInt(station.city.split("_")[2]);
            for (int i = 0; i < DEPARTURES_PER_STATION; i++) {
                departures.add(generateDeparture("autobus", station.busStation, x, y));
                departures.add(generateDeparture("voz", station.trainStation, x, y));
            }
        }
        return departures;
    }

    /**
     * Generiše pojedinačni polazak za datu stanicu.
     *
     * @param type Tip prevoza ("autobus" ili "voz").
     * @param from Ime polazne stanice.
     * @param x Red koordinata grada.
     * @param y Kolona koordinata grada.
     * @return Generisani polazak.
     */
    private Departure generateDeparture(String type, String from, int x, int y) {
        Departure departure = new Departure();
        departure.type = type;
        departure.from = from;
        List<String> neighbors = getNeighbors(x, y);
        departure.to = neighbors.isEmpty() ? from : neighbors.get(random.nextInt(neighbors.size()));
        int hour = random.nextInt(24);
        int minute = random.nextInt(4) * 15; // 0, 15, 30, 45
        departure.departureTime = String.format("%02d:%02d", hour, minute);
        departure.duration = 30 + random.nextInt(151); // 30–180 minuta
        departure.price = 100 + random.nextInt(901); // 100–1000
        departure.minTransferTime = 5 + random.nextInt(26); // 5–30 minuta
        return departure;
    }

    /**
     * Pronalazi susjedne gradove za dati grad u mreži.
     *
     * @param x Red koordinata grada.
     * @param y Kolona koordinata grada.
     * @return Lista imena susjednih gradova.
     */
    private List<String> getNeighbors(int x, int y) {
        List<String> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < rows && ny >= 0 && ny < cols) {
                neighbors.add("G_" + nx + "_" + ny);
            }
        }
        return neighbors;
    }

    /**
     * Čuva generisane podatke u JSON fajl.
     *
     * @param data Transportni podaci za čuvanje.
     * @param filename Ime fajla za čuvanje.
     */
    public void saveToJson(TransportData data, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            StringBuilder json = new StringBuilder();
            json.append("{\n");

            json.append("  \"countryMap\": [\n");
            for (int i = 0; i < rows; i++) {
                json.append("    [");
                for (int j = 0; j < cols; j++) {
                    json.append("\"").append(data.countryMap[i][j]).append("\"");
                    if (j < cols - 1) json.append(", ");
                }
                json.append("]");
                if (i < rows - 1) json.append(",");
                json.append("\n");
            }
            json.append("  ],\n");

            json.append("  \"stations\": [\n");
            for (int i = 0; i < data.stations.size(); i++) {
                Station s = data.stations.get(i);
                json.append("    {\"city\": \"").append(s.city)
                        .append("\", \"busStation\": \"").append(s.busStation)
                        .append("\", \"trainStation\": \"").append(s.trainStation)
                        .append("\"}");
                if (i < data.stations.size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("  ],\n");

            json.append("  \"departures\": [\n");
            for (int i = 0; i < data.departures.size(); i++) {
                Departure d = data.departures.get(i);
                json.append("    {\"type\": \"").append(d.type)
                        .append("\", \"from\": \"").append(d.from)
                        .append("\", \"to\": \"").append(d.to)
                        .append("\", \"departureTime\": \"").append(d.departureTime)
                        .append("\", \"duration\": ").append(d.duration)
                        .append(", \"price\": ").append(d.price)
                        .append(", \"minTransferTime\": ").append(d.minTransferTime)
                        .append("}");
                if (i < data.departures.size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("  ]\n");

            json.append("}");
            file.write(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}