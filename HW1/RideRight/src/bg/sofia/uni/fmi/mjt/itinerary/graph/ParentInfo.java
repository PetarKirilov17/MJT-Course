package bg.sofia.uni.fmi.mjt.itinerary.graph;

import bg.sofia.uni.fmi.mjt.itinerary.vehicle.VehicleType;

import java.math.BigDecimal;

public record ParentInfo(VehicleType type, BigDecimal price) {
}
