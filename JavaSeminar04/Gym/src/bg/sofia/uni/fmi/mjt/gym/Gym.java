package bg.sofia.uni.fmi.mjt.gym;

import bg.sofia.uni.fmi.mjt.gym.member.Address;
import bg.sofia.uni.fmi.mjt.gym.member.GymMember;
import bg.sofia.uni.fmi.mjt.gym.member.MemberByNameComparator;
import bg.sofia.uni.fmi.mjt.gym.member.MemberByProximityComparator;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Gym implements GymAPI {
    private final int capacity;
    private final Address address;
    private SortedSet<GymMember> gymMembers;

    public Gym(int capacity, Address address) {
        this.capacity = capacity;
        this.address = address;
        gymMembers = new TreeSet<>();
    }

    @Override
    public SortedSet<GymMember> getMembers() {
        return gymMembers;
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByName() {
        var result = new TreeSet<GymMember>(new MemberByNameComparator());
        result.addAll(this.gymMembers);
        return Collections.unmodifiableSortedSet(result);
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByProximityToGym() {
        var result = new TreeSet<GymMember>(new TreeSet<GymMember>(new MemberByProximityComparator(this.address)));
        result.addAll(this.gymMembers);
        return Collections.unmodifiableSortedSet(result);
    }

    private void validateGymCap(int size) throws GymCapacityExceededException {
        if (size > capacity) {
            throw new GymCapacityExceededException("Gym is full!");
        }
    }

    @Override
    public void addMember(GymMember member) throws GymCapacityExceededException {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null!");
        }
        validateGymCap(this.gymMembers.size() + 1);
        this.gymMembers.add(member);
    }

    @Override
    public void addMembers(Collection<GymMember> members) throws GymCapacityExceededException {
        if (members == null) {
            throw new IllegalArgumentException("Member cannot be null!");
        }
        if (members.isEmpty()) {
            throw new IllegalArgumentException("Members collection is empty!");
        }
        validateGymCap(this.gymMembers.size() + members.size());
        this.gymMembers.addAll(members);
    }

    @Override
    public boolean isMember(GymMember member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null!");
        }
        return this.gymMembers.contains(member);
    }

    @Override
    public boolean isExerciseTrainedOnDay(String exerciseName, DayOfWeek day) {
        if (day == null) {
            throw new IllegalArgumentException("Day cannot be null!");
        }
        if (exerciseName == null) {
            throw new IllegalArgumentException("Name of the exercise cannot be null!");
        }
        if (exerciseName.isBlank()) {
            throw new IllegalArgumentException("Name of the exercise cannot be empty!");
        }

        for (var m : gymMembers) {
            var program = m.getTrainingProgram();
            var workout = program.get(day);
            if (workout == null) {
                continue;
            }
            for (var ex : workout.exercises()) {
                if (ex.name().equals(exerciseName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Map<DayOfWeek, List<String>> getDailyListOfMembersForExercise(String exerciseName) {
        if (exerciseName == null) {
            throw new IllegalArgumentException("Name of exercise cannot ne null!");
        }
        if (exerciseName.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty!");
        }
        Map<DayOfWeek, List<String>> result = new HashMap<>();
        for (var m : gymMembers) {
            var program = m.getTrainingProgram().entrySet();
            for (var p : program) {
                for (var ex : p.getValue().exercises()) {
                    if (!ex.name().equals(exerciseName)) {
                        continue;
                    }
                    var l = result.get(p.getKey());
                    if (l == null) {
                        l = new ArrayList<>();
                        result.put(p.getKey(), l);
                    }
                    l.add(m.getName());
                }
            }
        }
        return Collections.unmodifiableMap(result);
    }
}
