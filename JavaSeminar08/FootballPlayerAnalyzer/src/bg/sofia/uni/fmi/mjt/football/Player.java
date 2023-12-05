package bg.sofia.uni.fmi.mjt.football;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public record Player(String name, String fullName, LocalDate birthDate, int age, double heightCm, double weightKg,
                     List<Position> positions, String nationality, int overallRating, int potential, long valueEuro,
                     long wageEuro, Foot preferredFoot) {
    private static final Character DELIMETER = ';';

    private static LocalDate buildDate(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        return LocalDate.parse(str, formatter);
    }

    private static List<Position> buildPositionsList(String str) {
        String[] tokens = str.split(",");
        List<Position> result = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            result.add(Position.valueOf(tokens[i]));
        }
        return result;
    }

    public static Player of(String line) {
        String[] tokens = line.split(DELIMETER.toString());
        int tokenIdx = 0;
        String name = tokens[tokenIdx++];
        String fullName = tokens[tokenIdx++];
        String dateString = tokens[tokenIdx++];
        int age = Integer.parseInt(tokens[tokenIdx++]);
        double height = Double.parseDouble(tokens[tokenIdx++]);
        double weight = Double.parseDouble(tokens[tokenIdx++]);
        String positionsString = tokens[tokenIdx++];
        String nationality = tokens[tokenIdx++];
        int rating = Integer.parseInt(tokens[tokenIdx++]);
        int potential = Integer.parseInt(tokens[tokenIdx++]);
        long value = Long.parseLong(tokens[tokenIdx++]);
        long wage = Long.parseLong(tokens[tokenIdx++]);
        String foot = tokens[tokenIdx];

        LocalDate birthDate = buildDate(dateString);
        List<Position> positions = buildPositionsList(positionsString);

        return new Player(name, fullName, birthDate, age, height, weight, positions, nationality, rating, potential,
            value, wage, Foot.valueOf(foot.toUpperCase()));
    }

    public boolean hasSamePosition(List<Position> pos) {
        for (var p : pos) {
            if (positions().contains(p)) {
                return true;
            }
        }
        return false;
    }
}
