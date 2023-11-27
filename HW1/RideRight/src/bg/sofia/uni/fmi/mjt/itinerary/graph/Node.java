package bg.sofia.uni.fmi.mjt.itinerary.graph;

import bg.sofia.uni.fmi.mjt.itinerary.City;
import bg.sofia.uni.fmi.mjt.itinerary.vehicle.VehicleType;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

//wrapper class of City
//defines a single node in the graph
public class Node implements Comparable<Node> {
    private final City city;
    private BigDecimal f;
    private BigDecimal g;
    private Set<Edge> neighbours;
    private Node parent;
    private ParentInfo parentInfo;

    public Node(City city) {
        this.city = city;
        this.f = BigDecimal.valueOf(Double.MAX_VALUE); // sufficiently big for our purpose
        this.g = BigDecimal.valueOf(Double.MAX_VALUE); // sufficiently big for our purpose
        this.neighbours = new TreeSet<>(new EdgeComparator());
        this.parent = null;
        this.parentInfo = null;
    }

    @Override
    public int compareTo(Node o) {
        if(this.f.compareTo(o.f) == 0){
            return this.getCity().name().compareTo(o.getCity().name());
        }
        return this.f.compareTo(o.f);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }
        Node node = (Node) obj;
        return city.equals(node.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city);
    }

    public City getCity() {
        return this.city;
    }

    public BigDecimal getF() {
        return f;
    }

    public BigDecimal getG() {
        return g;
    }

    public Set<Edge> getNeighbours() {
        return neighbours;
    }

    public Node getParent() {
        return parent;
    }

    public ParentInfo getParentInfo() {
        return parentInfo;
    }

    public void setF(BigDecimal value) {
        this.f = value;
    }

    public void setG(BigDecimal value) {
        this.g = value;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setParentInfo(VehicleType type, BigDecimal price) {
        this.parentInfo = new ParentInfo(type, price);
    }
}
