package bg.sofia.uni.fmi.mjt.itinerary.graph;

import bg.sofia.uni.fmi.mjt.itinerary.City;
import bg.sofia.uni.fmi.mjt.itinerary.Journey;
import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// this class builds the graph and links the city with the wrapper class Node
public class Graph {
    private Map<City, Node> graphNodes;

    public Graph(List<Journey> schedule){
        graphNodes = new HashMap<>();
        buildNodes(schedule);
    }

    private void buildNodes(List<Journey> schedule) {
        for (var j : schedule){
            var fromCity = j.from();
            var destCity = j.to();
            var type = j.vehicleType();
            var price = j.price();
            if(!graphNodes.containsKey(fromCity)){
                Node newNode = new Node(fromCity);
                graphNodes.put(fromCity, newNode);
            }
            if(!graphNodes.containsKey(destCity)){
                Node newNode = new Node(destCity);
                graphNodes.put(destCity, newNode);
            }
            var fromNode = graphNodes.get(fromCity);
            var destNode = graphNodes.get(destCity);
            fromNode.getNeighbours().add(new Edge(destNode, type, price));
        }
    }

    public Node getNodeByCity(City city) throws CityNotKnownException{
        if(!graphNodes.containsKey(city)){
            throw new CityNotKnownException("This city is not in the graph");
        }
        return graphNodes.get(city);
    }
}
