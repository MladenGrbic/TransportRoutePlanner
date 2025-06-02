package main.transport;

import main.model.Edge;
import main.model.Station;

import java.util.List;

/**
 * Predstavlja čvor u pretrazi ruta sa informacijama o stanici i parametrima rute.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class RouteNode {
    Station station;
    List<Edge> edges;
    int currentTime;
    int totalPrice;
    int transferCount;
    int cost;
    int hopCount;
    int totalTime;

    /**
     * Konstruktor za kreiranje čvora u pretrazi ruta.
     *
     * @param station Trenutna stanica.
     * @param edges Lista ivica do trenutne stanice.
     * @param currentTime Trenutno vrijeme u minutama (0-1439).
     * @param totalPrice Ukupna cijena rute.
     * @param transferCount Broj transfera na ruti.
     * @param cost Ukupni trošak prema kriterijumu pretrage.
     * @param hopCount Broj skokova (ivica) u ruti.
     * @param totalTime Ukupno vrijeme putovanja u minutama.
     */
    RouteNode(Station station, List<Edge> edges, int currentTime, int totalPrice,
              int transferCount, int cost, int hopCount, int totalTime) {
        this.station = station;
        this.edges = edges;
        this.currentTime = currentTime;
        this.totalPrice = totalPrice;
        this.transferCount = transferCount;
        this.cost = cost;
        this.hopCount = hopCount;
        this.totalTime = totalTime;
    }

    /**
     * Vraća trošak čvora prema kriterijumu pretrage.
     *
     * @return Trošak čvora.
     */
    int getCost() {
        return cost;
    }

    /**
     * Vraća trenutnu stanicu.
     *
     * @return Trenutna stanica.
     */
    public Station getStation() { return station; }
}