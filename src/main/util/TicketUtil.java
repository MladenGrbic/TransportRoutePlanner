package main.util;

import main.model.City;
import main.model.Ticket;
import main.transport.Route;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Uslužna klasa za generisanje računa za kupljene karte.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class TicketUtil {

    /**
     * Generiše račun za datu rutu i čuva ga kao tekstualni fajl.
     *
     * @param route Ruta za koju se generiše račun.
     * @param startCity Početni grad.
     * @param endCity Krajnji grad.
     * @return Apsolutna putanja do generisanog računa.
     * @throws IOException Ako dođe do greške pri kreiranju direktorijuma ili pisanju fajla.
     */
    public String generateReceipt(Route route, City startCity, City endCity) throws IOException {
        Path receiptsDir = Paths.get("racuni");
        if (!Files.exists(receiptsDir)) {
            Files.createDirectories(receiptsDir);
        }

        // Kreiraj Ticket objekat
        LocalDateTime now = LocalDateTime.now();
        String travelRelation = startCity.getName() + " -> " + endCity.getName();
        String segmentedRoute = route.getSegmentedDescription(endCity);
        int totalTime = route.getTotalTime();
        int price = route.getTotalPrice();
        int transferCount = route.getTransferCount();
        Ticket ticket = new Ticket(travelRelation, segmentedRoute, totalTime, price, transferCount, now);

        // Generiši jedinstveno ime fajla
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fileName = "racun_" + now.format(formatter) + ".txt";
        Path receiptPath = receiptsDir.resolve(fileName);

        // Sačuvaj račun u fajl
        Files.writeString(receiptPath, ticket.toFileFormat());

        return receiptPath.toAbsolutePath().toString();
    }
}