package main.model;

/**
 * Predstavlja železničku stanicu u gradu.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class TrainStation extends Station {

    /**
     * Konstruktor za kreiranje željezničke stanice.
     *
     * @param name Ime stanice.
     * @param city Grad u kojem se stanica nalazi.
     */
    public TrainStation(String name, City city) {
        super(name, city);
    }
}