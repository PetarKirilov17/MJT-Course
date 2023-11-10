package bg.sofia.uni.fmi.mjt.gym.member;

public record Address(double longitude, double latitude) {
    public double getDistanceTo(Address other) {
        return Math.sqrt(Math.pow(other.latitude - latitude, 2) + Math.pow(other.longitude - longitude, 2));
    }
}
