package bg.sofia.uni.fmi.mjt.gym.member;

import java.util.Comparator;

public class MemberByProximityComparator implements Comparator<GymMember> {
    private final Address gymAddress;

    public MemberByProximityComparator(Address gymAddress) {
        this.gymAddress = gymAddress;
    }

    @Override
    public int compare(GymMember o1, GymMember o2) {
        return Double.compare(o1.getAddress().getDistanceTo(gymAddress), o2.getAddress().getDistanceTo(gymAddress));
    }
}
