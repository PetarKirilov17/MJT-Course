package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;
import bg.sofia.uni.fmi.mjt.itinerary.graph.Edge;
import bg.sofia.uni.fmi.mjt.itinerary.graph.Graph;
import bg.sofia.uni.fmi.mjt.itinerary.graph.Node;
import bg.sofia.uni.fmi.mjt.itinerary.heuristic.HeuristicAPI;
import bg.sofia.uni.fmi.mjt.itinerary.heuristic.ManhattanDistanceHeuristicCalculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.SequencedCollection;

public class RideRight implements ItineraryPlanner {
    static final BigDecimal PRICE_FOR_KM = BigDecimal.valueOf(20);
    static final BigDecimal METRES_TO_KM = BigDecimal.valueOf(1000);
    private final Graph graph;
    private HeuristicAPI heuristicCalculator;

    public RideRight(List<Journey> schedule) {
        validateSchedule(schedule);
        graph = new Graph(schedule);
        heuristicCalculator = new ManhattanDistanceHeuristicCalculator();
    }

    private void validateSchedule(List<Journey> schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule cannot be null");
        }
        if (schedule.isEmpty()) {
            throw new IllegalArgumentException("This schedule does not contain any journeys!");
        }
    }

    @Override
    public SequencedCollection<Journey> findCheapestPath(City start, City destination, boolean allowTransfer)
        throws CityNotKnownException, NoPathToDestinationException {
        Node startNode;
        Node endNode;
        try {
            startNode = this.graph.getNodeByCity(start);
            endNode = this.graph.getNodeByCity(destination);
        } catch (CityNotKnownException exc) {
            System.out.println(exc.getMessage());
            throw exc;
        }

        if (!allowTransfer) {
            var startNeighbours = startNode.getNeighbours();
            boolean isFound = false;
            Edge foundEdge = null;
            BigDecimal foundPrice = BigDecimal.valueOf(Double.MAX_VALUE);
            for (var n : startNeighbours){
                if(n.to().equals(endNode) && n.price().compareTo(foundPrice) < 0){
                    isFound = true;
                    foundEdge = n;
                    foundPrice = n.price();
                }
            }
            if(!isFound){
                throw new NoPathToDestinationException("There is no path between these cities!");
            }
            Journey resultJourney = new Journey(foundEdge.vehicleType(), start, destination, foundEdge.price());
            SequencedCollection<Journey> result = new ArrayList<>();
            result.add(resultJourney);
            return result;
        }

        var result = aStarAlgo(startNode, endNode);
        if (result == null) {
            throw new NoPathToDestinationException("There is no path between these cities!");
        }

        return backTrackResult(result);
    }

    private Node aStarAlgo(Node start, Node target) {
        PriorityQueue<Node> closedList = new PriorityQueue<>();
        PriorityQueue<Node> openList = new PriorityQueue<>();

        start.setG(BigDecimal.valueOf(0));
        start.setF(start.getG().add(heuristicCalculator.calculateHeuristic(start, target).divide(METRES_TO_KM).multiply(PRICE_FOR_KM)));
        openList.add(start);

        while (!openList.isEmpty()) {
            Node n = openList.peek();
            if (n == target) {
                return n;
            }

            for (Edge edge : n.getNeighbours()) {
                Node currNeighbour = edge.to();
                BigDecimal wholePrice = edge.price().add(edge.price().multiply(edge.vehicleType().getGreenTax()));
                BigDecimal totalWeight = n.getG().add(wholePrice).add(heuristicCalculator.calculateHeuristic(n, currNeighbour).divide(METRES_TO_KM).multiply(PRICE_FOR_KM));

                if (!openList.contains(currNeighbour) && !closedList.contains(currNeighbour)) {
                    currNeighbour.setParent(n);
                    currNeighbour.setParentInfo(edge.vehicleType(), edge.price());
                    currNeighbour.setG(totalWeight);
                    var distance = heuristicCalculator.calculateHeuristic(currNeighbour, target).divide(METRES_TO_KM).multiply(PRICE_FOR_KM);
                    currNeighbour.setF(currNeighbour.getG()
                        .add(distance));

                    openList.add(currNeighbour);
                } else {
                    if (totalWeight.compareTo(currNeighbour.getG()) < 0 ||
                        (totalWeight.compareTo(currNeighbour.getG()) == 0 && currNeighbour.getParent().getCity().name().compareTo(n.getCity().name()) > 0)) {
                        currNeighbour.setParent(n);
                        currNeighbour.setParentInfo(edge.vehicleType(), edge.price());
                        currNeighbour.setG(totalWeight);
                        var distance = heuristicCalculator.calculateHeuristic(currNeighbour, target).divide(METRES_TO_KM).multiply(PRICE_FOR_KM);
                        currNeighbour.setF(
                            currNeighbour.getG().add(distance));

                        if (closedList.contains(currNeighbour)) {
                            closedList.remove(currNeighbour);
                            openList.add(currNeighbour);
                        }
                    }
                }
            }

            openList.remove(n);
            closedList.add(n);
        }
        return null;
    }

    private SequencedCollection<Journey> backTrackResult(Node target){
        SequencedCollection<Journey> result = new ArrayList<>();
        Node curr = target;
        Node parent = curr.getParent();
        while (parent != null){
            var from = parent.getCity();
            var to = curr.getCity();
            var vehicleType = curr.getParentInfo().type();
            var price = curr.getParentInfo().price();
            Journey toBeAdded = new Journey(vehicleType, from, to, price);
            result.addFirst(toBeAdded);

            curr = parent;
            parent = parent.getParent();
        }
        return result;
    }
}
