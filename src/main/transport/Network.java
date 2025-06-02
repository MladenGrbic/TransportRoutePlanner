package main.transport;

import main.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Predstavlja transportnu mrežu sa gradovima, stanicama i rutama.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class Network {
    private final Map<String, City> cities;
    private final Map<String, Station> stations;
    private final Graph graph;
    private int gridRows;
    private int gridCols;

    /**
     * Kreira praznu transportnu mrežu.
     */
    public Network() {
        this.cities = new HashMap<>();
        this.stations = new HashMap<>();
        this.graph = new Graph();
    }

    /**
     * Postavlja broj redova mreže.
     *
     * @param gridRows Broj redova.
     */
    public void setGridRows(int gridRows) {
        this.gridRows = gridRows;
    }

    /**
     * Postavlja broj kolona mreže.
     *
     * @param gridCols Broj kolona.
     */
    public void setGridCols(int gridCols) {
        this.gridCols = gridCols;
    }

    /**
     * Učitava transportne podatke iz JSON fajla.
     *
     * @param jsonFilePath Putanja do JSON fajla.
     */
    public void loadFromJson(String jsonFilePath) {
        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONObject json = new JSONObject(jsonString);

            JSONArray countryMap = json.getJSONArray("countryMap");
            this.gridRows = countryMap.length();
            this.gridCols = countryMap.getJSONArray(0).length();
            System.out.println("Computed Grid: " + gridRows + "x" + gridCols);

            JSONArray stationsArray = json.getJSONArray("stations");
            for (int i = 0; i < stationsArray.length(); i++) {
                JSONObject stationData = stationsArray.getJSONObject(i);
                String cityName = stationData.getString("city");
                String busStationName = stationData.getString("busStation");
                String trainStationName = stationData.getString("trainStation");

                City city = new City(cityName);
                cities.put(cityName, city);
                BusStation busStation = new BusStation(busStationName, city);
                TrainStation trainStation = new TrainStation(trainStationName, city);
                stations.put(busStationName, busStation);
                stations.put(trainStationName, trainStation);
                city.setBusStation(busStation);
                city.setTrainStation(trainStation);

                Edge transferEdge1 = new Edge(busStation, trainStation, 0, 15, 15, 0, true);
                Edge transferEdge2 = new Edge(trainStation, busStation, 0, 15, 15, 0, true);
                graph.addEdge(busStation, transferEdge1);
                graph.addEdge(trainStation, transferEdge2);
            }

            JSONArray departuresArray = json.getJSONArray("departures");
            System.out.println("Number of departures: " + departuresArray.length());
            for (int i = 0; i < departuresArray.length(); i++) {
                JSONObject depObj = departuresArray.getJSONObject(i);
                String fromStationName = depObj.getString("from");
                String toCityName = depObj.getString("to");
                String departureTimeStr = depObj.getString("departureTime");
                int duration = depObj.getInt("duration");
                int price = depObj.getInt("price");
                int minTransferTime = depObj.getInt("minTransferTime");

                Station fromStation = stations.get(fromStationName);
                City toCity = cities.get(toCityName);
                if (fromStation == null || toCity == null) {
                    continue;
                }

                int departureTime = timeToMinutes(departureTimeStr);
                Departure departure = new Departure(fromStation, toCity, departureTime, duration, price, minTransferTime);
                fromStation.addDeparture(departure);

                Station toStation = (fromStation instanceof BusStation) ? toCity.getBusStation() : toCity.getTrainStation();
                Edge edge = new Edge(fromStation, toStation, departureTime, duration, price, minTransferTime, false);
                graph.addEdge(fromStation, edge);
            }

            System.out.println("Cities loaded: " + cities.size());
            System.out.println("Stations loaded: " + stations.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Konvertuje vrijeme u formatu "HH:mm" u minute.
     *
     * @param time Vrijeme u formatu "HH:mm".
     * @return Vrijeme u minutama.
     */
    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    /**
     * Računa vrijeme čekanja za sledeći polazak do određenog grada.
     *
     * @param currentTime Trenutno vrijeme u minutama.
     * @param fromStation Polazna stanica.
     * @param toCity Destinacijski grad.
     * @return Vrijeme čekanja u minutama, ili -1 ako nema polaska.
     */
    public int getWaitingTime(int currentTime, Station fromStation, City toCity) {
        List<Departure> departures = fromStation.getDepartures();
        int minWait = Integer.MAX_VALUE;

        for (Departure departure : departures) {
            if (departure.getToCity().equals(toCity)) {
                int departureTime = departure.getDepartureTime();
                int wait = departureTime >= currentTime
                        ? departureTime - currentTime
                        : (1440 - currentTime) + departureTime;

                if (wait < minWait) {
                    minWait = wait;
                }
            }
        }

        return minWait == Integer.MAX_VALUE ? -1 : minWait;
    }

    /**
     * Pronalazi do 5 optimalnih ruta između dva grada prema zadatom kriterijumu.
     *
     * @param startCity Početni grad.
     * @param endCity Krajnji grad.
     * @param criterion Kriterijum optimizacije ("time", "price", "transfers").
     * @param startTime Vrijeme početka u minutama.
     * @return Lista ruta, sortirana po kriterijumu.
     */
    public List<Route> findRoutes(City startCity, City endCity, String criterion, int startTime) {
        PriorityQueue<RouteNode> queue = new PriorityQueue<>(Comparator.comparingInt(RouteNode::getCost));
        Set<String> uniqueRoutes = new HashSet<>();
        List<Route> result = new ArrayList<>();
        Map<String, Integer> visitedPaths = new HashMap<>();
        Map<String, Integer> cityTransferCount = new HashMap<>();
        Set<Station> endStations = new HashSet<>(Arrays.asList(endCity.getBusStation(), endCity.getTrainStation()));

        Set<String> visitedCities = new HashSet<>();
        visitedCities.add(startCity.getName());

        queue.offer(new RouteNode(startCity.getBusStation(), new ArrayList<>(), startTime, 0, 0, 0, 0, 0));
        queue.offer(new RouteNode(startCity.getTrainStation(), new ArrayList<>(), startTime, 0, 0, 0, 0, 0));

        RouteNode current = queue.poll();
        Station currentStation;
        int currentTime;
        int totalTime;
        int currentPrice;
        while (!queue.isEmpty()) {
            current = queue.poll();
            currentStation = current.station;
            currentTime = current.currentTime;
            totalTime = current.totalTime;
            currentPrice = current.totalPrice;
            int currentHops = current.hopCount;
            List<Edge> currentEdges = new ArrayList<>(current.edges);
            String currentCityName = currentStation.getCity().getName();

            String pathKey = createPathKey(currentEdges, currentStation);
            int visitCount = visitedPaths.getOrDefault(pathKey, 0);
            if (visitCount >= 10) continue;
            visitedPaths.put(pathKey, visitCount + 1);

            if (endStations.contains(currentStation)) {
                String routeKey = createRouteKey(currentEdges);
                if (!uniqueRoutes.contains(routeKey)) {
                    uniqueRoutes.add(routeKey);
                    Route route = new Route(currentEdges, totalTime, currentPrice, currentHops, startTime);
                    result.add(route);
                    if (uniqueRoutes.size() >= 5) break;
                }
                continue;
            }

            int maxHops = gridRows * gridCols * 3;
            if (currentHops >= maxHops) continue;

            List<Edge> edges = graph.getEdges(currentStation);
            for (Edge edge : edges) {
                Station nextStation = edge.getTo();
                String nextCityName = nextStation.getCity().getName();
                int nextTime, nextTotalTime, nextPrice, nextHops;

                if (edge.isTransfer()) {
                    int transferCount = cityTransferCount.getOrDefault(currentCityName, 0);
                    if (transferCount >= 1) continue;
                    cityTransferCount.put(currentCityName, transferCount + 1);

                    nextTime = (currentTime + edge.getDuration()) % 1440;
                    nextTotalTime = totalTime + edge.getDuration();
                    nextPrice = currentPrice + edge.getPrice();
                    nextHops = currentHops + 1;
                } else {
                    City toCity = nextStation.getCity();
                    int waitingTime = getWaitingTime(currentTime, currentStation, toCity);
                    if (waitingTime == -1) continue;

                    int departureTime = (currentTime + waitingTime) % 1440;
                    Departure selectedDeparture = null;
                    int minTimeDiff = Integer.MAX_VALUE;
                    for (Departure dep : currentStation.getDepartures()) {
                        if (dep.getToCity().equals(toCity)) {
                            int depTime = dep.getDepartureTime();
                            int timeDiff = depTime >= departureTime ? depTime - departureTime : (1440 - departureTime) + depTime;
                            if (timeDiff < minTimeDiff) {
                                minTimeDiff = timeDiff;
                                selectedDeparture = dep;
                            }
                        }
                    }
                    if (selectedDeparture == null) continue;

                    int arrivalTime = (selectedDeparture.getDepartureTime() + selectedDeparture.getDuration()) % 1440;
                    nextTime = (arrivalTime + selectedDeparture.getMinTransferTime()) % 1440;
                    nextTotalTime = totalTime + waitingTime + selectedDeparture.getDuration();
                    nextPrice = currentPrice + selectedDeparture.getPrice();
                    nextHops = currentHops + 1;
                    edge = new Edge(currentStation, nextStation, selectedDeparture.getDepartureTime(),
                            selectedDeparture.getDuration(), selectedDeparture.getPrice(),
                            selectedDeparture.getMinTransferTime(), false);
                }

                int nextRow = Integer.parseInt(nextCityName.split("_")[1]);
                int nextCol = Integer.parseInt(nextCityName.split("_")[2]);
                int endRow = Integer.parseInt(endCity.getName().split("_")[1]);
                int endCol = Integer.parseInt(endCity.getName().split("_")[2]);
                int distanceToEnd = Math.abs(nextRow - endRow) + Math.abs(nextCol - endCol);

                if (visitedCities.contains(nextCityName) && !nextCityName.equals(endCity.getName()) && distanceToEnd > 2) {
                    continue;
                }

                String nextPathKey = createPathKey(currentEdges, nextStation);
                int nextVisitCount = visitedPaths.getOrDefault(nextPathKey, 0);
                if (nextVisitCount >= 10) continue;

                int distancePenalty = Math.abs(nextRow - endRow) + Math.abs(nextCol - endCol);
                int transferPenalty = edge.isTransfer() ? 100 : 0;

                int cost = switch (criterion) {
                    case "time" -> nextTotalTime + distancePenalty * 10 + transferPenalty;
                    case "price" -> nextPrice + distancePenalty * 150 + transferPenalty;
                    case "transfers" -> nextHops * 1000 + distancePenalty * 10 + transferPenalty * 1000;
                    default -> throw new IllegalArgumentException("Nepoznat kriterijum: " + criterion);
                };

                List<Edge> newEdges = new ArrayList<>(currentEdges);
                newEdges.add(edge);
                RouteNode newNode = new RouteNode(nextStation, newEdges, nextTime, nextPrice,
                        nextHops, cost, nextHops, nextTotalTime);
                queue.offer(newNode);

                if (!edge.isTransfer()) {
                    visitedCities.add(nextCityName);
                }
            }
        }

        while (result.size() < 5 && !queue.isEmpty()) {
            current = queue.poll();
            currentStation = current.station;
            totalTime = current.totalTime;
            currentPrice = current.totalPrice;
            int currentHops = current.hopCount;
            List<Edge> currentEdges = new ArrayList<>(current.edges);

            String pathKey = createPathKey(currentEdges, currentStation);
            int visitCount = visitedPaths.getOrDefault(pathKey, 0);
            if (visitCount >= 10) continue;
            visitedPaths.put(pathKey, visitCount + 1);

            if (endStations.contains(currentStation)) {
                String routeKey = createRouteKey(currentEdges);
                if (!uniqueRoutes.contains(routeKey)) {
                    uniqueRoutes.add(routeKey);
                    Route route = new Route(currentEdges, totalTime, currentPrice, currentHops, startTime);
                    result.add(route);
                }
            } else {
                List<Edge> edges = graph.getEdges(currentStation);
                for (Edge edge : edges) {
                    Station nextStation = edge.getTo();
                    String nextCityName = nextStation.getCity().getName();
                    int nextTime, nextTotalTime, nextPrice, nextHops;

                    if (edge.isTransfer()) {
                        int transferCount = cityTransferCount.getOrDefault(currentStation.getCity().getName(), 0);
                        if (transferCount >= 1) continue;
                        cityTransferCount.put(currentStation.getCity().getName(), transferCount + 1);

                        nextTime = (current.currentTime + edge.getDuration()) % 1440;
                        nextTotalTime = totalTime + edge.getDuration();
                        nextPrice = currentPrice + edge.getPrice();
                        nextHops = currentHops + 1;
                    } else {
                        City toCity = nextStation.getCity();
                        int waitingTime = getWaitingTime(current.currentTime, currentStation, toCity);
                        if (waitingTime == -1) continue;

                        int departureTime = (current.currentTime + waitingTime) % 1440;
                        Departure selectedDeparture = null;
                        int minTimeDiff = Integer.MAX_VALUE;
                        for (Departure dep : currentStation.getDepartures()) {
                            if (dep.getToCity().equals(toCity)) {
                                int depTime = dep.getDepartureTime();
                                int timeDiff = depTime >= departureTime ? depTime - departureTime : (1440 - departureTime) + depTime;
                                if (timeDiff < minTimeDiff) {
                                    minTimeDiff = timeDiff;
                                    selectedDeparture = dep;
                                }
                            }
                        }
                        if (selectedDeparture == null) continue;

                        int arrivalTime = (selectedDeparture.getDepartureTime() + selectedDeparture.getDuration()) % 1440;
                        nextTime = (arrivalTime + selectedDeparture.getMinTransferTime()) % 1440;
                        nextTotalTime = totalTime + waitingTime + selectedDeparture.getDuration();
                        nextPrice = currentPrice + selectedDeparture.getPrice();
                        nextHops = currentHops + 1;
                        edge = new Edge(currentStation, nextStation, selectedDeparture.getDepartureTime(),
                                selectedDeparture.getDuration(), selectedDeparture.getPrice(),
                                selectedDeparture.getMinTransferTime(), false);
                    }

                    int nextRow = Integer.parseInt(nextCityName.split("_")[1]);
                    int nextCol = Integer.parseInt(nextCityName.split("_")[2]);
                    int endRow = Integer.parseInt(endCity.getName().split("_")[1]);
                    int endCol = Integer.parseInt(endCity.getName().split("_")[2]);
                    int distanceToEnd = Math.abs(nextRow - endRow) + Math.abs(nextCol - endCol);

                    if (visitedCities.contains(nextCityName) && !nextCityName.equals(endCity.getName()) && distanceToEnd > 2) {
                        continue;
                    }

                    String nextPathKey = createPathKey(currentEdges, nextStation);
                    int nextVisitCount = visitedPaths.getOrDefault(nextPathKey, 0);
                    if (nextVisitCount >= 10) continue;

                    int distancePenalty = Math.abs(nextRow - endRow) + Math.abs(nextCol - endCol);
                    int transferPenalty = edge.isTransfer() ? 100 : 0;

                    int cost = switch (criterion) {
                        case "time" -> nextTotalTime + distancePenalty * 10 + transferPenalty;
                        case "price" -> nextPrice + distancePenalty * 150 + transferPenalty;
                        case "transfers" -> nextHops * 1000 + distancePenalty * 10 + transferPenalty * 1000;
                        default -> throw new IllegalArgumentException("Nepoznat kriterijum: " + criterion);
                    };

                    List<Edge> newEdges = new ArrayList<>(currentEdges);
                    newEdges.add(edge);
                    RouteNode newNode = new RouteNode(nextStation, newEdges, nextTime, nextPrice,
                            nextHops, cost, nextHops, nextTotalTime);
                    queue.offer(newNode);

                    if (!edge.isTransfer()) {
                        visitedCities.add(nextCityName);
                    }
                }
            }
        }

        result.sort((r1, r2) -> {
            int primary = switch (criterion) {
                case "time" -> Integer.compare(r1.getTotalTime(), r2.getTotalTime());
                case "price" -> Integer.compare(r1.getTotalPrice(), r2.getTotalPrice());
                case "transfers" -> Integer.compare(r1.getTransferCount(), r2.getTransferCount());
                default -> 0;
            };
            if (primary == 0 && "transfers".equals(criterion)) {
                return Integer.compare(r1.getTotalTime(), r2.getTotalTime());
            }
            return primary;
        });

        System.out.println("Pronađeno ruta: " + result.size());
        return result.subList(0, Math.min(result.size(), 5));
    }

    /**
     * Kreira ključ za rutu na osnovu ivica.
     *
     * @param edges Lista ivica u ruti.
     * @return Ključ rute.
     */
    private String createRouteKey(List<Edge> edges) {
        StringBuilder key = new StringBuilder();
        for (Edge edge : edges) {
            key.append(edge.getFrom().getName()).append("->").append(edge.getTo().getName()).append(";");
        }
        return key.toString();
    }

    /**
     * Kreira ključ za put na osnovu ivica i krajnje stanice.
     *
     * @param edges Lista ivica.
     * @param endStation Krajnja stanica.
     * @return Ključ puta.
     */
    private String createPathKey(List<Edge> edges, Station endStation) {
        StringBuilder key = new StringBuilder();
        for (Edge edge : edges) {
            key.append(edge.getFrom().getName()).append("->").append(edge.getTo().getName()).append(";");
        }
        key.append(endStation.getName());
        return key.toString();
    }

    /**
     * Vraća mapu gradova u mreži.
     *
     * @return Mapa gradova.
     */
    public Map<String, City> getCities() { return cities; }

    /**
     * Vraća graf mreže.
     *
     * @return Graf stanica i veza.
     */
    public Graph getGraph() { return graph; }
}