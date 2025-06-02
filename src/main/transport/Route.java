package main.transport;

import main.model.BusStation;
import main.model.City;
import main.model.Edge;
import main.model.Station;

import java.util.ArrayList;
import java.util.List;

/**
 * Predstavlja rutu između stanica sa informacijama o ivicama, vremenu i cijeni.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class Route {
    private List<Edge> edges;
    private int totalTime;
    private int totalPrice;
    private int transferCount;
    private int startTime;

    /**
     * Konstruktor za kreiranje rute.
     *
     * @param edges Lista ivica u ruti.
     * @param totalTime Ukupno vrijeme putovanja u minutama.
     * @param totalPrice Ukupna cijena rute.
     * @param transferCount Broj transfera na ruti.
     * @param startTime Vrijeme početka rute u minutama.
     */
    public Route(List<Edge> edges, int totalTime, int totalPrice, int transferCount, int startTime) {
        this.edges = edges;
        this.totalTime = totalTime;
        this.totalPrice = totalPrice;
        this.transferCount = transferCount;
        this.startTime = startTime;
    }

    /**
     * Vraća listu ivica u ruti.
     *
     * @return Lista ivica.
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Vraća početnu stanicu rute.
     *
     * @return Početna stanica, ili null ako je ruta prazna.
     */
    public Station startStation() {
        return edges.isEmpty() ? null : edges.get(0).getFrom();
    }

    /**
     * Vraća vrijeme početka rute.
     *
     * @return Vrijeme početka u minutama.
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * Vraća ukupno vrijeme putovanja.
     *
     * @return Ukupno vrijeme u minutama.
     */
    public int getTotalTime() {
        return totalTime;
    }

    /**
     * Vraća ukupnu cijenu rute.
     *
     * @return Cijena u novčanim jedinicama.
     */
    public int getTotalPrice() {
        return totalPrice;
    }

    /**
     * Vraća broj transfera na ruti.
     *
     * @return Broj transfera.
     */
    public int getTransferCount() {
        return transferCount;
    }

    /**
     * Generiše tekstualni opis rute sa segmentima i ukupnim parametrima.
     *
     * @param endCity Krajnji grad rute.
     * @return Tekstualni opis rute.
     */
    public String getSegmentedDescription(City endCity) {
        if (edges.isEmpty() || endCity == null) return "Nema rute";
        List<String> segments = new ArrayList<>();
        Station currentStation = startStation();
        String transportType = currentStation instanceof BusStation ? "Autobus" : "Voz";
        int departureTime = edges.get(0).getDepartureTime();

        Station lastNonTransferStation = currentStation;
        String lastTransportType = transportType;

        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            Station nextStation = edge.getTo();
            String nextTransport = nextStation instanceof BusStation ? "Autobus" : "Voz";
            int arrivalTime = (edge.getDepartureTime() + edge.getDuration()) % 1440;

            int[] currentCoords = getRowCol(currentStation.getCity().getName());
            int[] nextCoords = getRowCol(nextStation.getCity().getName());
            if (edge.isTransfer() && currentCoords[0] == nextCoords[0] && currentCoords[1] == nextCoords[1]) {
                transportType = nextTransport;
                continue;
            }

            if (!edge.isTransfer() && !transportType.equals(nextTransport) && i > 0) {
                segments.add(String.format("%s_%s_%s do %s_%s_%s (%s)",
                        lastNonTransferStation.getName().charAt(0), getRowCol(lastNonTransferStation.getCity().getName())[0],
                        getRowCol(lastNonTransferStation.getCity().getName())[1],
                        currentStation.getName().charAt(0), getRowCol(currentStation.getCity().getName())[0],
                        getRowCol(currentStation.getCity().getName())[1],
                        lastTransportType));
                segments.add(String.format("Transfer na %s_%s_%s (%s)",
                        nextStation.getName().charAt(0), getRowCol(nextStation.getCity().getName())[0],
                        getRowCol(nextStation.getCity().getName())[1], nextTransport));
                lastNonTransferStation = nextStation;
                lastTransportType = nextTransport;
            }

            currentStation = nextStation;
            transportType = nextTransport;
        }

        if (segments.isEmpty()) {
            segments.add(String.format("%s_%s_%s do %s_%s_%s",
                    startStation().getName().charAt(0), getRowCol(startStation().getCity().getName())[0],
                    getRowCol(startStation().getCity().getName())[1],
                    endCity.getName().charAt(0), getRowCol(endCity.getName())[0],
                    getRowCol(endCity.getName())[1]));
        } else {
            segments.add(String.format("%s_%s_%s do %s_%s_%s",
                    lastNonTransferStation.getName().charAt(0), getRowCol(lastNonTransferStation.getCity().getName())[0],
                    getRowCol(lastNonTransferStation.getCity().getName())[1],
                    endCity.getName().charAt(0), getRowCol(endCity.getName())[0],
                    getRowCol(endCity.getName())[1]));
        }

        return String.join("\n", segments) + "\nUkupno: " + formatTotalTime(totalTime) + ", " +
                totalPrice + " novčanih jedinica.";
    }

    /**
     * Formatira ukupno vrijeme u formatu "XhYm".
     *
     * @param totalTime Ukupno vrijeme u minutama.
     * @return Formatirano vrijeme.
     */
    private String formatTotalTime(int totalTime) {
        int hours = totalTime / 60;
        int minutes = totalTime % 60;
        return String.format("%dh%02dm", hours, minutes);
    }

    /**
     * Parsira koordinate grada iz imena.
     *
     * @param cityName Ime grada u formatu "Grad_red_kolona".
     * @return Niz sa redom i kolonom.
     */
    private int[] getRowCol(String cityName) {
        String[] parts = cityName.split("_");
        return new int[]{Integer.parseInt(parts[1]), Integer.parseInt(parts[2])};
    }

    /**
     * Vraća tekstualni opis rute sa detaljima o stanicama i vremenima.
     *
     * @return Opis rute.
     */
    @Override
    public String toString() {
        int interCityHop = 0;
        int hours = totalTime / 60;
        int minutes = totalTime % 60;
        StringBuilder path = new StringBuilder();
        int currentTime = startTime;
        path.append(startStation().getName()).append(" (").append(minutesToTime(startTime)).append(")");
        int cumulativeTime = 0;
        for (Edge edge : edges) {
            if (edge.isTransfer()) {
                cumulativeTime += edge.getDuration();
                currentTime = (startTime + cumulativeTime) % 1440;
                path.append(" -> ").append(edge.getTo().getName())
                        .append(" (transfer, ").append(minutesToTime(currentTime)).append(")");
                interCityHop++;
            } else {
                int wait = edge.getDepartureTime() >= (currentTime % 1440)
                        ? edge.getDepartureTime() - (currentTime % 1440)
                        : (1440 - (currentTime % 1440)) + edge.getDepartureTime();
                cumulativeTime += wait + edge.getDuration();
                currentTime = (edge.getDepartureTime() + edge.getDuration()) % 1440;
                path.append(" -> ").append(edge.getTo().getName())
                        .append(" (").append(minutesToTime(currentTime)).append(")");
            }
        }
        return String.format("Ruta: %s | Ukupno: %dh%02dm | Cena: %d | Transferi: %d",
                path, hours, minutes, totalPrice, transferCount + interCityHop);
    }

    /**
     * Konvertuje minute u format "HH:mm".
     *
     * @param minutes Vrijeme u minutama.
     * @return Formatirano vrijeme.
     */
    private String minutesToTime(int minutes) {
        int hours = (minutes / 60) % 24;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
}