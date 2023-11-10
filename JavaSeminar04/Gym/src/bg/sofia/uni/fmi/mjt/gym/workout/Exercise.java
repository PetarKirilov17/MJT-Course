package bg.sofia.uni.fmi.mjt.gym.workout;

import java.util.Objects;

public record Exercise(String name, int sets, int repetitions) {
    @Override
    public boolean equals(Object obj) {
        // If the object is compared with itself then return true
        if (obj == this) {
            return true;
        }
        /* Check if o is an instance of Exercise or not
          "null instanceof [type]" also returns false */
        if (!(obj instanceof Exercise)) {
            return false;
        }
        Exercise e = (Exercise) obj;
        return name.equals(e.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
