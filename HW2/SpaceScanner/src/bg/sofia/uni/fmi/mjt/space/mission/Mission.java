package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public record Mission(String id, String company, String location, LocalDate date, Detail detail,
                      RocketStatus rocketStatus, Optional<Double> cost, MissionStatus missionStatus) {
    private static final String REGEX_SPLITTER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    public static Mission of(String line) {
        String[] tokens = line.split(REGEX_SPLITTER);
        int tokenIdx = 0;
        String id = trimDoubleQuotes(tokens[tokenIdx++]);
        String company = trimDoubleQuotes(tokens[tokenIdx++]);
        String location = trimDoubleQuotes(tokens[tokenIdx++]);
        String dateString = trimDoubleQuotes(tokens[tokenIdx++]);
        String detailString = trimDoubleQuotes(tokens[tokenIdx++]);
        String rocketStatusString = trimDoubleQuotes(tokens[tokenIdx++]);
        String costStr = trimDoubleQuotes(tokens[tokenIdx++]).strip();
        String missionStatusStr = trimDoubleQuotes(tokens[tokenIdx]);
        LocalDate date = buildDate(dateString);
        Detail detail = buildDetail(detailString);
        Optional<Double> cost = buildCost(costStr);
        RocketStatus rocketStatus = buildRocketStatus(rocketStatusString);
        MissionStatus missionStatus = buildMissionStatus(missionStatusStr);
        return new Mission(id, company, location, date, detail, rocketStatus, cost,
            missionStatus);
    }

    public String getCountry() {
        String[] locationTokens = location.split(", ");
        return locationTokens[locationTokens.length - 1];
    }

    private static String trimDoubleQuotes(String input) {
        if (input.startsWith("\"") && input.endsWith("\"")) {
            return input.substring(1, input.length() - 1);
        } else {
            // String does not have surrounding double quotes
            return input;
        }
    }

    private static LocalDate buildDate(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM dd, yyyy", Locale.ENGLISH);
        return LocalDate.parse(str, formatter);
    }

    private static Detail buildDetail(String str) {
        String[] tokens = str.split("\\|");
        return new Detail(tokens[0].strip(), tokens[1].strip());
    }

    private static Optional<Double> buildCost(String str) {
        if (str.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(Double.parseDouble(str));
    }

    private static RocketStatus buildRocketStatus(String str) {
        return switch (str) {
            case "StatusActive" -> RocketStatus.STATUS_ACTIVE;
            default -> RocketStatus.STATUS_RETIRED;
        };
    }

    private static MissionStatus buildMissionStatus(String str) {
        return switch (str) {
            case "Success" -> MissionStatus.SUCCESS;
            case "Failure" -> MissionStatus.FAILURE;
            case "Partial Failure" -> MissionStatus.PARTIAL_FAILURE;
            default -> MissionStatus.PRELAUNCH_FAILURE;
        };
    }
}
