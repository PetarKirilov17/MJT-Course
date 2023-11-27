package bg.sofia.uni.fmi.mjt.itinerary.graph;

import bg.sofia.uni.fmi.mjt.itinerary.City;
import bg.sofia.uni.fmi.mjt.itinerary.vehicle.VehicleType;

import java.math.BigDecimal;
import java.util.Objects;

public record Edge(Node to, VehicleType vehicleType, BigDecimal price) {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Edge)) {
            return false;
        }
        Edge edge = (Edge) obj;
        return to.equals(edge.to) && vehicleType == edge.vehicleType && price.equals(edge.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, vehicleType, price);
    }

//    @Override
//    public int compareTo(Edge o) {
//        return this.to.getCity().name().compareTo(o.to.getCity().name());
//    }
}
