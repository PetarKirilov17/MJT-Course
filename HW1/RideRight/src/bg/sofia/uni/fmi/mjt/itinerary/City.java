package bg.sofia.uni.fmi.mjt.itinerary;

import java.util.Objects;

public record City(String name, Location location) {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof City)) {
            return false;
        }
        City city = (City) obj;
        return name.equals(city.name) && location.equals(city.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location);
    }
}
