package bg.sofia.uni.fmi.mjt.itinerary.heuristic;

import bg.sofia.uni.fmi.mjt.itinerary.graph.Node;

import java.math.BigDecimal;

public interface HeuristicAPI {
    BigDecimal calculateHeuristic(Node current, Node target);
}
