package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.algorithm.SymmetricBlockCipher;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {
    private List<Mission> missions;
    private List<Rocket> rockets;
    private final SecretKey secretKey;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        this.secretKey = secretKey;
        loadMissions(missionsReader);
        loadRockets(rocketsReader);
    }

    private void loadRockets(Reader rocketsReader) {
        try (var bufferedReader = new BufferedReader(rocketsReader)) {
            rockets = bufferedReader.lines()
                .skip(1)
                .map(Rocket::of).toList();
        } catch (IOException e) {
            throw new UncheckedIOException("A problem occurred while reading the rockets from the file", e);
        }
    }

    private void loadMissions(Reader missionsReader) {
        try (var bufferedReader = new BufferedReader(missionsReader)) {
            missions = bufferedReader.lines()
                .skip(1)
                .map(Mission::of).toList();
        } catch (IOException e) {
            throw new UncheckedIOException("A problem occurred while reading the missions from the file", e);
        }
    }

    @Override
    public Collection<Mission> getAllMissions() {
        return Collections.unmodifiableCollection(missions);
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null!");
        }
        return this.missions.stream().filter(m -> m.missionStatus().equals(missionStatus)).toList();
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        validateTimeFrame(from, to);
        var filteredMissions = getSuccessfulMissionsInPeriod(from, to);
        var mappedCompanies = groupCompaniesByCountOfSuccMissions(filteredMissions);
        var resultEntry = mappedCompanies.entrySet().stream()
            .max(Map.Entry.comparingByValue());

        return resultEntry.map(Map.Entry::getKey).orElse("");
    }

    private List<Mission> getSuccessfulMissionsInPeriod(LocalDate from, LocalDate to) {
        return missions.stream()
            .filter(m -> m.date().isAfter(from) && m.date().isBefore(to))
            .filter(m -> m.missionStatus().equals(MissionStatus.SUCCESS))
            .toList();
    }

    private Map<String, Long> groupCompaniesByCountOfSuccMissions(List<Mission> missions) {
        return missions.stream()
            .collect(Collectors.groupingBy(Mission::company, Collectors.counting()));
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        return missions.stream()
            .collect(Collectors.groupingBy(Mission::getCountry, Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException("Cannot select negative or zero count of missions!");
        }
        if (missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("Status cannot be null!");
        }
        return missions.stream()
            .filter(m -> m.missionStatus().equals(missionStatus))
            .filter(m -> m.rocketStatus().equals(rocketStatus))
            .filter(m -> m.cost().isPresent())
            .sorted(Comparator.comparingDouble(m -> m.cost().get()))
            .limit(n)
            .toList();
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        var groupedMissionsByCompanyAndLocation = groupMissionsByCompanyAndLocation();
        return groupedMissionsByCompanyAndLocation.entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> getLocationByMaxValue(entry.getValue())));
    }

    private Map<String, Map<String, Long>> groupMissionsByCompanyAndLocation() {
        return this.missions.stream()
            .collect(Collectors.groupingBy(Mission::company,
                Collectors.groupingBy(Mission::location, Collectors.counting())));
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        validateTimeFrame(from, to);
        var groupedSuccessfulMissions = groupSuccessfulMissionsByCompanyAndLocation();
        return groupedSuccessfulMissions.entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> getLocationByMaxValue(entry.getValue())));
    }

    private Map<String, Map<String, Long>> groupSuccessfulMissionsByCompanyAndLocation() {
        return this.missions.stream()
            .filter(m -> m.missionStatus().equals(MissionStatus.SUCCESS))
            .collect(Collectors.groupingBy(Mission::company,
                Collectors.groupingBy(Mission::location, Collectors.counting())));
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return Collections.unmodifiableCollection(rockets);
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("You cannot select negative count of rockets!");
        }
        return rockets.stream()
            .filter(rocket -> rocket.height().isPresent())
            .sorted((r1, r2) -> r2.height().get().compareTo(r1.height().get()))
            .limit(n)
            .toList();
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return rockets.stream()
            .collect(Collectors.toMap(
                Rocket::name,
                Rocket::wiki
            ));
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        var filteredMissions = getTopNMostExpensiveMissions(n, missionStatus, rocketStatus);
        var rocketNames = filteredMissions.stream()
            .map(m -> m.detail().rocketName())
            .collect(Collectors.toSet());

        List<String> result = new ArrayList<>();
        var rocketNameToWikiMap = getWikiPageForRocket();
        for (var rocketName : rocketNames) {
            if (rocketNameToWikiMap.containsKey(rocketName) && rocketNameToWikiMap.get(rocketName).isPresent()) {
                result.add(rocketNameToWikiMap.get(rocketName).get());
            }
        }
        return result;
    }

    private List<Mission> getTopNMostExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException("You cannot select negative or zero count of wiki pages!");
        }
        if (missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("Status cannot be null!");
        }

        return missions.stream()
            .filter(m -> m.missionStatus().equals(missionStatus))
            .filter(m -> m.rocketStatus().equals(rocketStatus))
            .filter(m -> m.cost().isPresent())
            .sorted((m1, m2) -> m2.cost().get().compareTo(m1.cost().get()))
            .limit(n)
            .toList();
    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException {
        if (outputStream == null) {
            throw new IllegalArgumentException("Output Stream cannot be null!");
        }
        validateTimeFrame(from, to);

        var reliabilityMap = getReliabilityMap(from, to);
        String mostReliableRocketName = getMostReliableRocket(reliabilityMap);

        SymmetricBlockCipher rijndael = new Rijndael(secretKey);
        rijndael.encrypt(new ByteArrayInputStream(mostReliableRocketName.getBytes()), outputStream);
    }

    private Map<String, Double> getReliabilityMap(LocalDate from, LocalDate to) {
        var rocketNameToSuccMissions = getCountOfSuccessfulMissionsPerRocketName(from, to);
        var rocketNameToUnsuccMissions = getCountOfUnsuccessfulMissionsPerRocketName(from, to);
        var rocketNameMissions = getCountOfAllMissionsPerRocketName(from, to);

        Map<String, Double> reliabilityMap = new HashMap<>();
        for (var entry : rocketNameMissions.entrySet()) {
            long succCount = 0;
            long unsuccCount = 0;
            if (rocketNameToSuccMissions.containsKey(entry.getKey())) {
                succCount = rocketNameToSuccMissions.get(entry.getKey());
            }
            if (rocketNameToUnsuccMissions.containsKey(entry.getKey())) {
                unsuccCount = rocketNameToUnsuccMissions.get(entry.getKey());
            }
            double reliability = (2 * succCount + unsuccCount) / (double) (2 * entry.getValue());
            reliabilityMap.put(entry.getKey(), reliability);
        }
        return reliabilityMap;
    }

    private String getMostReliableRocket(Map<String, Double> reliabilityMap) {
        return reliabilityMap.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("");
    }

    private Map<String, Long> getCountOfMissionsPerRocketName(
        LocalDate from, LocalDate to, Predicate<MissionStatus> statusPredicate) {
        return missions.stream()
            .filter(r -> r.date().isAfter(from) && r.date().isBefore(to))
            .filter(r -> statusPredicate.test(r.missionStatus()))
            .collect(Collectors.groupingBy(
                m -> m.detail().rocketName(),
                Collectors.counting()
            ));
    }

    private Map<String, Long> getCountOfSuccessfulMissionsPerRocketName(LocalDate from, LocalDate to) {
        return getCountOfMissionsPerRocketName(from, to, status -> status.equals(MissionStatus.SUCCESS));
    }

    private Map<String, Long> getCountOfUnsuccessfulMissionsPerRocketName(LocalDate from, LocalDate to) {
        return getCountOfMissionsPerRocketName(from, to, status -> !status.equals(MissionStatus.SUCCESS));
    }

    private Map<String, Long> getCountOfAllMissionsPerRocketName(LocalDate from, LocalDate to) {
        return getCountOfMissionsPerRocketName(from, to, status -> true);
    }

    private void validateTimeFrame(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Local Date for time period cannot be null!");
        }
        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("To date should be after the from date!");
        }
    }

    private String getLocationByMaxValue(Map<String, Long> locationCountEntries) {
        Optional<Map.Entry<String, Long>> mostDesiredEntry = locationCountEntries.entrySet().stream()
            .max(Map.Entry.comparingByValue());
        return mostDesiredEntry.map(Map.Entry::getKey).orElse("");
    }
}
