package main.model;

/**
 * Predstavlja ivicu u grafu, koja može biti polazak ili transfer između stanica.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class Edge {
    private final Station from;
    private final Station to;
    private final int departureTime;
    private final int duration;
    private final int price;
    private final int minTransferTime;
    private final boolean isTransfer;

    /**
     * Konstruktor za kreiranje ivice u grafu.
     *
     * @param from Polazna stanica.
     * @param to Destinaciona stanica.
     * @param departureTime vrijeme polaska u minutama.
     * @param duration Trajanje putovanja u minutama.
     * @param price Cijena putovanja.
     * @param minTransferTime Minimalno vrijeme čekanja za transfer u minutama.
     * @param isTransfer Da li je ivica transfer unutar grada.
     */
    public Edge(Station from, Station to, int departureTime, int duration, int price, int minTransferTime, boolean isTransfer) {
        this.from = from;
        this.to = to;
        this.departureTime = departureTime;
        this.duration = duration;
        this.price = price;
        this.minTransferTime = minTransferTime;
        this.isTransfer = isTransfer;
    }

    /**
     * Vraća destinacionu stanicu.
     *
     * @return Destinaciona stanica.
     */
    public Station getTo() { return to; }

    /**
     * Vraća polaznu stanicu.
     *
     * @return Polazna stanica.
     */
    public Station getFrom() { return from; }

    /**
     * Vraća vrijeme polaska.
     *
     * @return vrijeme polaska u minutama.
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

    /**
     * Proverava da li je ivica transfer.
     *
     * @return true ako je ivica transfer, inače false.
     */
    public boolean isTransfer() { return isTransfer; }

    /**
     * Vraća vrijeme dolaska na destinaciju.
     *
     * @return Vrijeme dolaska u minutama.
     */
    public int getArrivalTime() {
        return isTransfer ? -1 : departureTime + duration;
    }
}