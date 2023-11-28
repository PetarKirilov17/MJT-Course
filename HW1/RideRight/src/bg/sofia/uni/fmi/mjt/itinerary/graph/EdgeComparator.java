package bg.sofia.uni.fmi.mjt.itinerary.graph;

import java.util.Comparator;

public class EdgeComparator implements Comparator<Edge> {
    @Override
    public int compare(Edge e1, Edge e2) {
        if (e1.to().getCity().name().compareTo(e2.to().getCity().name()) == 0) {
            if (e1.vehicleType().compareTo(e2.vehicleType()) == 0) {
                return e1.price().compareTo(e2.price());
            }
            return e1.vehicleType().compareTo(e2.vehicleType());
        }
        return e1.to().getCity().name().compareTo(e2.to().getCity().name());
    }
}
