package main.model;

/**
 * Predstavlja grad sa koordinatama na mreži i povezanim stanicama.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class City {
    private final String name;
    private final int row;
    private final int column;
    private BusStation busStation;
    private TrainStation trainStation;

    /**
     * Konstruktor za kreiranje grada na osnovu imena sa koordinatama.
     *
     * @param name Ime grada u formatu "Grad_red_kolona" (npr. "Grad_1_2").
     */
    public City(String name) {
        this.name = name;
        String[] parts = name.split("_");
        this.row = Integer.parseInt(parts[1]);
        this.column = Integer.parseInt(parts[2]);
    }

    /**
     * Vraća ime grada.
     *
     * @return Ime grada.
     */
    public String getName() { return name; }

    /**
     * Vraća red koordinatu grada na mreži.
     *
     * @return Red koordinata.
     */
    public int getRow() { return row; }

    /**
     * Vraća kolonu koordinatu grada na mreži.
     *
     * @return Kolona koordinata.
     */
    public int getColumn() { return column; }

    /**
     * Vraća autobusku stanicu grada.
     *
     * @return Autobuska stanica, ili null ako nije postavljena.
     */
    public BusStation getBusStation() { return busStation; }

    /**
     * Vraća železničku stanicu grada.
     *
     * @return Železnička stanica, ili null ako nije postavljena.
     */
    public TrainStation getTrainStation() { return trainStation; }

    /**
     * Postavlja autobusku stanicu za grad.
     *
     * @param busStation Autobuska stanica.
     */
    public void setBusStation(BusStation busStation) { this.busStation = busStation; }

    /**
     * Postavlja železničku stanicu za grad.
     *
     * @param trainStation Železnička stanica.
     */
    public void setTrainStation(TrainStation trainStation) { this.trainStation = trainStation; }
}