package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.vehicle.VehicleType;

import java.math.BigDecimal;
import java.util.Objects;

public record Journey(VehicleType vehicleType, City from, City to, BigDecimal price) {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Journey)) {
            return false;
        }
        Journey journey = (Journey) obj;
        return vehicleType == journey.vehicleType && from.equals(journey.from) && to.equals(journey.to) &&
            price.equals(journey.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleType, from, to, price);
    }
}
