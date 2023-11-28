package bg.sofia.uni.fmi.mjt.itinerary;

import java.util.Objects;

public record Location(int x, int y) {
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Location)) {
            return false;
        }
        Location other = (Location) obj;
        return this.x() == other.x() && this.y() == other.y();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
