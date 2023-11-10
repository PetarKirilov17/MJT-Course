package bg.sofia.uni.fmi.mjt.gym.member;

import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Member implements GymMember {
    private final Address address;
    private final String name;
    private final int age;
    private final String personalIdNumber;
    private final Gender gender;
    private Map<DayOfWeek, Workout> workoutProgram;

    public Member(Address address, String name, int age, String personalIdNumber, Gender gender) {
        this.address = address;
        this.name = name;
        this.age = age;
        this.personalIdNumber = personalIdNumber;
        this.gender = gender;
        workoutProgram = new HashMap<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public String getPersonalIdNumber() {
        return this.personalIdNumber;
    }

    @Override
    public Gender getGender() {
        return this.gender;
    }

    @Override
    public Address getAddress() {
        return this.address;
    }

    @Override
    public Map<DayOfWeek, Workout> getTrainingProgram() {
        return this.workoutProgram;
    }

    @Override
    public void setWorkout(DayOfWeek day, Workout workout) {
        if (day == null) {
            throw new IllegalArgumentException("Day of week cannot be null!");
        }
        if (workout == null) {
            throw new IllegalArgumentException("Workout cannot be null!");
        }
        workoutProgram.putIfAbsent(day, workout);
    }

    @Override
    public Collection<DayOfWeek> getDaysFinishingWith(String exerciseName) {
        if (exerciseName == null) {
            throw new IllegalArgumentException("Name of the exercise cannot be null!");
        }
        if (exerciseName.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty!");
        }
        Collection<DayOfWeek> result = new HashSet<>();

        var workoutSet = this.workoutProgram.entrySet();
        for (var me : workoutSet) {
            if (me.getValue().exercises().getLast().name().equals(exerciseName)) {
                result.add(me.getKey());
            }
        }
        return result;
    }

    @Override
    public void addExercise(DayOfWeek day, Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise cannot be null!");
        }
        Workout workout;
        try {
            workout = validateDayInProgram(day);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
        workout.exercises().addLast(exercise);
    }

    @Override
    public void addExercises(DayOfWeek day, List<Exercise> exercises) {
        if (exercises == null) {
            throw new IllegalArgumentException("Exercises cannot be null!");
        }
        if (exercises.isEmpty()) {
            throw new IllegalArgumentException("Exercises cannot be empty!");
        }
        Workout workout;
        try {
            workout = validateDayInProgram(day);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
        workout.exercises().addAll(exercises);
    }

    private Workout validateDayInProgram(DayOfWeek day) {
        if (day == null) {
            throw new IllegalArgumentException("Day cannot be null!");
        }
        var workout = this.workoutProgram.get(day);
        if (workout == null) {
            throw new DayOffException(day.name() + " is day off!");
        }
        return workout;
    }

    @Override
    public int compareTo(GymMember o) {
        return personalIdNumber.compareTo(o.getPersonalIdNumber());
    }

    @Override
    public boolean equals(Object obj) {
        // If the object is compared with itself then return true
        if (obj == this) {
            return true;
        }
         /* Check if o is an instance of Member or not
          "null instanceof [type]" also returns false */
        if (!(obj instanceof Member)) {
            return false;
        }
        Member e = (Member) obj;
        return personalIdNumber.equals(e.personalIdNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personalIdNumber);
    }
}
