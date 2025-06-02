package main.transport;

import main.model.BusStation;
import main.model.City;
import main.model.Station;

/**
 * Predstavlja polazak sa stanice ka drugom gradu.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class Departure {
    private final Station from;
    private final City toCity;
    private final int departureTime;
    private final int duration;
    private final int price;
    private final int minTransferTime;

    /**
     * Konstruktor za kreiranje polaska.
     *
     * @param from Polazna stanica.
     * @param toCity Destinacijski grad.
     * @param departureTime Vrijeme polaska u minutama.
     * @param duration Trajanje putovanja u minutama.
     * @param price Cijena putovanja.
     * @param minTransferTime Minimalno vrijeme čekanja za transfer.
     */
    public Departure(Station from, City toCity, int departureTime, int duration, int price, int minTransferTime) {
        this.from = from;
        this.toCity = toCity;
        this.departureTime = departureTime;
        this.duration = duration;
        this.price = price;
        this.minTransferTime = minTransferTime;
    }

    /**
     * Vraća destinacionu stanicu na osnovu tipa polazne stanice.
     *
     * @return Destinaciona stanica.
     */
    public Station getToStation() {
        return (from instanceof BusStation) ? toCity.getBusStation() : toCity.getTrainStation();
    }

    /**
     * Vraća vrijeme dolaska na destinaciju.
     *
     * @return Vrijeme dolaska u minutama.
     */
    public int getArrivalTime() {
        return departureTime + duration;
    }

    /**
     * Vraća polaznu stanicu.
     *
     * @return Polazna stanica.
     */
    public Station getFrom() { return from; }

    /**
     * Vraća destinacijski grad.
     *
     * @return Destinacijski grad.
     */
    public City getToCity() { return toCity; }

    /**
     * Vraća vrijeme polaska.
     *
     * @return Vrijeme polaska u minutama.
     */
    public int getDepartureTime() { return departureTime; }

    /**
     * Vraća trajanje putovanja.
     *
     * @return Trajanje u minutama.
     */
    public int getDuration() { return duration; }

    /**
     * Vraća cijenu putovanja.
     *
     * @return Cijena u novčanim jedinicama.
     */
    public int getPrice() { return price; }

    /**
     * Vraća minimalno vrijeme čekanja za transfer.
     *
     * @return Minimalno vrijeme čekanja u minutama.
     */
    public int getMinTransferTime() { return minTransferTime; }
}