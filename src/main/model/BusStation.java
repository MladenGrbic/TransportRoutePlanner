package main.model;

/**
 * Predstavlja autobusku stanicu u gradu.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class BusStation extends Station {

    /**
     * Konstruktor za kreiranje autobuske stanice.
     *
     * @param name Ime stanice.
     * @param city Grad u kojem se stanica nalazi.
     */
    public BusStation(String name, City city) {
        super(name, city);
    }
}