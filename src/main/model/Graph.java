package main.model;

import java.util.*;

/**
 * Predstavlja graf stanica i veza (polazaka i transfera).
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class Graph {
    private final Map<Station, List<Edge>> adjacencyList;

    /**
     * Kreira prazan graf.
     */
    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Dodaje čvor (stanicu) u graf.
     *
     * @param station Stanica koja se dodaje.
     */
    public void addNode(Station station) {
        adjacencyList.putIfAbsent(station, new ArrayList<>());
    }

    /**
     * Dodaje ivicu (polazak ili transfer) između stanica.
     *
     * @param from Polazna stanica.
     * @param edge Ivica sa destinacijom i parametrima.
     */
    public void addEdge(Station from, Edge edge) {
        adjacencyList.computeIfAbsent(from, k -> new ArrayList<>()).add(edge);
    }

    /**
     * Vraća sve stanice u grafu.
     *
     * @return Skup stanica.
     */
    public Set<Station> getNodes() {
        return adjacencyList.keySet();
    }

    /**
     * Vraća ivice iz date stanice.
     *
     * @param station Polazna stanica.
     * @return Lista ivica, ili prazna lista ako stanica nema ivica.
     */
    public List<Edge> getEdges(Station station) {
        return adjacencyList.getOrDefault(station, Collections.emptyList());
    }
}