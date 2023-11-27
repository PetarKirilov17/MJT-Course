package bg.sofia.uni.fmi.mjt.itinerary.heuristic;

import bg.sofia.uni.fmi.mjt.itinerary.graph.Node;

import java.math.BigDecimal;

public class ManhattanDistanceHeuristicCalculator implements HeuristicAPI{
    @Override
    public BigDecimal calculateHeuristic(Node current, Node target) {
        return BigDecimal.valueOf(Math.abs(current.getCity().location().x() - target.getCity().location().x())
            + Math.abs(current.getCity().location().y() - target.getCity().location().y()));
    }
}
