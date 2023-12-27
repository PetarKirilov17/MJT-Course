package bg.sofia.uni.fmi.mjt.space.rocket;

import java.util.Optional;

public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) {
    private static final int TWO_LENGTH = 2;
    private static final int THREE_LENGTH = 3;

    public static Rocket of(String line) {
        String[] tokens = line.split(",", -1);
        int tokenIdx = 0;
        String id = tokens[tokenIdx++];
        String name = tokens[tokenIdx++];
        String wikiStr = tokens[tokenIdx++];
        String heightStr = tokens[tokenIdx].replaceAll(" m$", "");
        Optional<String> wiki = buildWiki(wikiStr);
        Optional<Double> height = buildHeight(heightStr);
        return new Rocket(id, name, wiki, height);
    }

    private static Optional<String> buildWiki(String str) {
        if (str.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(str);
    }

    private static Optional<Double> buildHeight(String str) {
        if (str.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(Double.parseDouble(str));
    }
}
