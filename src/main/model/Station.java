package main.model;

import main.transport.Departure;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Apstraktna klasa koja predstavlja stanicu (autobusku ili železničku) u gradu.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public abstract class Station {
    protected final String name;
    protected final City city;
    protected final List<Departure> departures;

    /**
     * Konstruktor za kreiranje stanice.
     *
     * @param name Ime stanice.
     * @param city Grad u kojem se stanica nalazi.
     */
    public Station(String name, City city) {
        this.name = name;
        this.city = city;
        this.departures = new ArrayList<>();
    }

    /**
     * Dodaje polazak sa stanice.
     *
     * @param departure Polazak koji se dodaje.
     */
    public void addDeparture(Departure departure) {
        departures.add(departure);
    }

    /**
     * Sortira polaske po vremenu polaska.
     */
    public void sortDepartures() {
        departures.sort(Comparator.comparingInt(Departure::getDepartureTime));
    }

    /**
     * Vraća ime stanice.
     *
     * @return Ime stanice.
     */
    public String getName() { return name; }

    /**
     * Vraća grad u kojem se stanica nalazi.
     *
     * @return Grad stanice.
     */
    public City getCity() { return city; }

    /**
     * Vraća listu polazaka sa stanice.
     *
     * @return Lista polazaka.
     */
    public List<Departure> getDepartures() { return departures; }
}