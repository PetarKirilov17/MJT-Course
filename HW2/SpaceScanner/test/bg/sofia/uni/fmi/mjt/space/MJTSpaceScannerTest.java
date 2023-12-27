package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.algorithm.SymmetricBlockCipher;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MJTSpaceScannerTest {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int KEY_SIZE_IN_BITS = 128;

    private static SpaceScannerAPI spaceScanner;
    private static SpaceScannerAPI spaceScannerForTestingReliability;

    private static SecretKey secretKey;
    private static List<Mission> missions;
    private static List<Mission> missionsForTestingReliabilities;
    private static List<Rocket> rockets;
    private static List<Rocket> rocketsForTestingReliabilities;

    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGenerator.init(KEY_SIZE_IN_BITS);
        SecretKey secretKey = keyGenerator.generateKey();

        String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Generated Secret Key (Base64-encoded): " + base64Key);

        return secretKey;

    }
        @BeforeAll
    static void setUp(){
        StringReader missionsReader = new StringReader("""
            Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
            0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
            1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
            2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Success
            3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success
            11,ExPace,"Site 95, Jiuquan Satellite Launch Center, China","Fri Jul 10, 2020","Kuaizhou 11 | Jilin-1 02E, CentiSpace-1 S2",StatusActive,"28.3 ",Failure
            25,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Sat May 30, 2020",Falcon 9 Block 5 | SpaceX Demo-2,StatusActive,"50.0 ",Success
            26,CASC,"Xichang Satellite Launch Center, China","Fri May 29, 2020",Long March 11 | XJS-G and XJS-H,StatusActive,"5.3 ",Success
            27,Virgin Orbit,"Cosmic Girl, Mojave Air and Space Port, California, USA","Mon May 25, 2020",LauncherOne | Demo Flight,StatusActive,"12.0 ",Failure
            34,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Wed Apr 22, 2020",Falcon 9 Block 5 | Starlink V1 L6,StatusActive,"50.0 ",Success
            35,IRGC,"Launch Plateform, Shahrud Missile Test Site","Wed Apr 22, 2020",Qased | Noor 1,StatusActive,,Success
            36,CASC,"LC-2, Xichang Satellite Launch Center, China","Thu Apr 09, 2020",Long March 3B/E | Nusantara Dua,StatusActive,"29.15 ",Failure
            127,Exos,"Vertical Launch Area, Spaceport America, New Mexico","Sat Jun 29, 2019",SARGE | Launch 3,StatusActive,,Partial Failure
            413,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Thu Sep 01, 2016",Falcon 9 Block 3 | AMOS-6,StatusRetired,"62.0 ",Prelaunch Failure""");

        StringReader rocketsReader = new StringReader("""
            "",Name,Wiki,Rocket Height
            0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m
            1,Tsyklon-4M,https://en.wikipedia.org/wiki/Cyclone-4M,38.7 m
            2,Unha-2,https://en.wikipedia.org/wiki/Unha,28.0 m
            3,Unha-3,https://en.wikipedia.org/wiki/Unha,32.0 m
            4,Vanguard,https://en.wikipedia.org/wiki/Vanguard_(rocket),23.0 m
            15,Vostok-2A,https://en.wikipedia.org/wiki/Vostok_(rocket_family),
            16,Vostok-2M,https://en.wikipedia.org/wiki/Vostok-2M,
            17,Vulcan Centaur,https://en.wikipedia.org/wiki/Vulcan_%28rocket%29,58.3 m
            62,Atlas-E/F Burner,,
            169,Falcon 9 Block 5,https://en.wikipedia.org/wiki/Falcon_9,70.0 m
            213,Long March 2D,https://en.wikipedia.org/wiki/Long_March_2D,41.06 m
            294,Proton-M/Briz-M,https://en.wikipedia.org/wiki/Proton-M,58.2 m
            371,Starship Prototype,https://en.wikipedia.org/wiki/SpaceX_Starship,50.0 m""");

        StringReader missionsForTestingRelReader = new StringReader("""
            0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
            1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
            25,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Sat May 30, 2020",Falcon 9 Block 5 | SpaceX Demo-2,StatusActive,"50.0 ",Success
            381,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Wed Dec 28, 2016","Long March 2D | SuperView-1 1, 2 & Bayi Kepu 1",StatusActive,"29.75 ",Partial Failure
            """);

        StringReader rocketsForTestingRelReader = new StringReader("""
            169,Falcon 9 Block 5,https://en.wikipedia.org/wiki/Falcon_9,70.0 m,
            213,Long March 2D,https://en.wikipedia.org/wiki/Long_March_2D,41.06 m
            """);

        try {
            secretKey = generateSecretKey();
            spaceScannerForTestingReliability = new MJTSpaceScanner(missionsForTestingRelReader, rocketsForTestingRelReader, secretKey);
            spaceScanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);
            missions = spaceScanner.getAllMissions().stream().toList();
            rockets = spaceScanner.getAllRockets().stream().toList();
            missionsForTestingReliabilities = spaceScannerForTestingReliability.getAllMissions().stream().toList();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            throw new RuntimeException("Error while loading the secret key!");
        }
    }

    @Test
    void testGetAllMissionsThrowsExcWhenMissionStatusIsNull(){
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getAllMissions(null),
            "Get All Missions by status should throw IllegalArgumentException when status is null");
    }

    @Test
    void testGetAllMissionsByStatusSuccessfully(){
        List<Mission> successfulMissions = List.of(
            missions.get(0),
            missions.get(1),
            missions.get(2),
            missions.get(3),
            missions.get(5),
            missions.get(6),
            missions.get(8),
            missions.get(9)
        );
        List<Mission> failureMissions = List.of(
          missions.get(4),
          missions.get(7),
          missions.get(10)
        );
        List<Mission> partialFailure = List.of(
            missions.get(11)
        );
        List<Mission> prelaunchFailure = List.of(
            missions.get(12)
        );
        assertIterableEquals(successfulMissions, spaceScanner.getAllMissions(MissionStatus.SUCCESS),
            "Get All Missions should return all successful missions");
        assertIterableEquals(failureMissions, spaceScanner.getAllMissions(MissionStatus.FAILURE),
            "Get All Missions should return all failure missions");
        assertIterableEquals(partialFailure, spaceScanner.getAllMissions(MissionStatus.PARTIAL_FAILURE),
            "Get All Missions should return all partial failure missions");
        assertIterableEquals(prelaunchFailure, spaceScanner.getAllMissions(MissionStatus.PRELAUNCH_FAILURE),
            "Get All Missions should return all prelaunch failure missions");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsThrowsIllegalArgExcWhenLocalDateIsNull(){
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getCompanyWithMostSuccessfulMissions(null,
            LocalDate.now()), "Get Company with most successful missions should throw IllegalArgument exception when from date is null!");
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getCompanyWithMostSuccessfulMissions(LocalDate.now(), null),
            "Get Company with most successful missions should throw IllegalArgument exception when to date is null!");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsThrowsTimeFrameMismatchExcWhenToIsBeforeFrom(){
        assertThrows(TimeFrameMismatchException.class, () -> spaceScanner.getCompanyWithMostSuccessfulMissions(
            LocalDate.of(2023, 12, 10), LocalDate.of(2023, 12, 1)),
            "Get Company with most successful missions should throw TimeFrameMismatch exception when to date is before from date!");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsSuccessfully(){
        assertEquals("SpaceX", spaceScanner.getCompanyWithMostSuccessfulMissions(
            LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31)),
            "Get company with most successful missions should return the name of the company that has the biggest number of successful mission in that period!");
    }

    @Test
    void testGetMissionsPerCountry(){
        List<Mission> USAMissions = List.of(
            missions.get(0),
            missions.get(2),
            missions.get(5),
            missions.get(7),
            missions.get(8),
            missions.get(12)
        );
        List<Mission> ChinaMissions = List.of(
            missions.get(1),
            missions.get(4),
            missions.get(6),
            missions.get(10)
        );
        List<Mission> KazakhstanMissions = List.of(missions.get(3));
        List<Mission> ShahrudMissions = List.of(missions.get(9));
        List<Mission> NewMexicoMissions = List.of(missions.get(11));
        Map<String, Collection<Mission>> expectedMap = Map.of(
            "USA", USAMissions,
            "China", ChinaMissions,
            "Kazakhstan", KazakhstanMissions,
            "Shahrud Missile Test Site", ShahrudMissions,
            "New Mexico", NewMexicoMissions
        );

        assertEquals(expectedMap, spaceScanner.getMissionsPerCountry(),
            "Get Missions per country should return a map that shows missions grouped by countries!");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsThrowsIllegalArgExcWhenNIsNotPositive(){
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getTopNLeastExpensiveMissions(-5, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
            "Get top N Least Expensive missions should throw Illegal Argument Exception when number of queried missions is negative!");

        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getTopNLeastExpensiveMissions(0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
            "Get top N Least Expensive missions should throw Illegal Argument Exception when number of queried missions is zero!");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsThrowsIllegalArgExcWhenStatusIsNull(){
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getTopNLeastExpensiveMissions(5, null, RocketStatus.STATUS_ACTIVE),
           "Get top N Least Expensive missions should throw Illegal Argument Exception when mission status is null!" );

        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getTopNLeastExpensiveMissions(5, MissionStatus.SUCCESS,null),
            "Get top N Least Expensive missions should throw Illegal Argument Exception when mission status is null!" );
    }

    @Test
    void testGetTopNLeastExpensiveMissionsSuccessfully(){
        List<Mission> expectedMissions = List.of(
            missions.get(6),
            missions.get(1),
            missions.get(0),
            missions.get(5),
            missions.get(8),
            missions.get(3)
        );
        assertIterableEquals(expectedMissions, spaceScanner.getTopNLeastExpensiveMissions(10, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
            "Get Top N Least Expensive Missions should return the first n missions sorted by cost!");

        List<Mission> shortExpected = List.of(
            missions.get(6),
            missions.get(1)
        );
        assertIterableEquals(shortExpected, spaceScanner.getTopNLeastExpensiveMissions(2, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
            "Get Top N Least Expensive Missions should return the first n missions sorted by cost!");
    }

    @Test
    void testGetMostDesiredLocationForMissionsPerCompany(){
        Map<String, String> expectedMap = Map.of(
        "SpaceX", "LC-39A, Kennedy Space Center, Florida, USA",
        "CASC", "Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China",
        "Roscosmos", "Site 200/39, Baikonur Cosmodrome, Kazakhstan",
        "ExPace", "Site 95, Jiuquan Satellite Launch Center, China",
        "Virgin Orbit", "Cosmic Girl, Mojave Air and Space Port, California, USA",
        "IRGC", "Launch Plateform, Shahrud Missile Test Site",
        "Exos", "Vertical Launch Area, Spaceport America, New Mexico"
        );
        assertEquals(expectedMap, spaceScanner.getMostDesiredLocationForMissionsPerCompany(),
            "Get Most Desired Location For Missions Per Company should return a map with companies and their most desired locations for missions!");
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyThrowsIllegalArgExcWhenDateIsNull(){
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(null, LocalDate.of(2022, 12, 1)),
            "Get Location With Most Successful Missions Per Company should throw Illegal Argument Exception when from date is null!");

        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.of(2022, 12, 1),null),
            "Get Location With Most Successful Missions Per Company should throw Illegal Argument Exception when to date is null!");
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyThrowsTimeFrameMismatchExcWhenToIsBeforeFrom(){
        assertThrows(TimeFrameMismatchException.class, () -> spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.of(2022, 12, 31), LocalDate.of(2022, 12, 1)),
            "Get Location With Most Successful Missions Per Company should throw TimeFrameMismatchException when to date is before from date");
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanySuccessfully(){
        Map<String, String> expectedMap = Map.of(
            "SpaceX", "LC-39A, Kennedy Space Center, Florida, USA",
            "CASC", "Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China",
            "Roscosmos", "Site 200/39, Baikonur Cosmodrome, Kazakhstan",
            "IRGC", "Launch Plateform, Shahrud Missile Test Site"
        );
        assertEquals(expectedMap, spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 12, 31)),
            "Get Location With Most Successful Missions Per Company should return a map that shows most successful location per company!");
    }

    @Test
    void testGetTopNTallestRocketsThrowsIllegalArgExcWhenNumberIsNotPositive(){
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getTopNTallestRockets(-5),
            "Get Top N Tallest Rockets should throw Illegal Argument Exception when number of queried rockets is negative!");

        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getTopNTallestRockets(0),
            "Get Top N Tallest Rockets should throw Illegal Argument Exception when number of queried rockets is zero!");
    }

    @Test
    void testGetTopNTallestRocketsSuccessfully(){
        List<Rocket> expectedRockets = List.of(
            rockets.get(9),
            rockets.get(7),
            rockets.get(11),
            rockets.get(12),
            rockets.get(10),
            rockets.get(0)
        );
        assertIterableEquals(expectedRockets, spaceScanner.getTopNTallestRockets(6),
            "Get Top N Tallest Rockets should return the tallest n rockets");
    }

    @Test
    void testGetWikiPageForRocket(){
        Map<String, Optional<String>> expectedMap = new HashMap<>();
        expectedMap.put("Tsyklon-3", Optional.of("https://en.wikipedia.org/wiki/Tsyklon-3"));
        expectedMap.put("Tsyklon-4M", Optional.of("https://en.wikipedia.org/wiki/Cyclone-4M"));
        expectedMap.put("Unha-2", Optional.of("https://en.wikipedia.org/wiki/Unha"));
        expectedMap.put("Unha-3", Optional.of("https://en.wikipedia.org/wiki/Unha"));
        expectedMap.put("Vanguard", Optional.of("https://en.wikipedia.org/wiki/Vanguard_(rocket)"));
        expectedMap.put("Vostok-2A", Optional.of("https://en.wikipedia.org/wiki/Vostok_(rocket_family)"));
        expectedMap.put("Vostok-2M", Optional.of("https://en.wikipedia.org/wiki/Vostok-2M"));
        expectedMap.put("Vulcan Centaur", Optional.of("https://en.wikipedia.org/wiki/Vulcan_%28rocket%29"));
        expectedMap.put("Atlas-E/F Burner", Optional.empty());
        expectedMap.put("Falcon 9 Block 5", Optional.of("https://en.wikipedia.org/wiki/Falcon_9"));
        expectedMap.put("Long March 2D", Optional.of("https://en.wikipedia.org/wiki/Long_March_2D"));
        expectedMap.put("Starship Prototype", Optional.of("https://en.wikipedia.org/wiki/SpaceX_Starship"));
        expectedMap.put("Proton-M/Briz-M", Optional.of("https://en.wikipedia.org/wiki/Proton-M"));

        assertEquals(expectedMap, spaceScanner.getWikiPageForRocket(),
            "Get Wiki Page For Rocket should return map that shows wiki page for every rocket!");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsThrowsIllegalArgExcWhenNumberIsNotPositive(){
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
            -5, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
            "Get Wiki Pages For Rockets Used In Most Expensive Missions should throw Illegal Argument Exception" +
                " when number is negative!");
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
            "Get Wiki Pages For Rockets Used In Most Expensive Missions should throw Illegal Argument Exception" +
                " when number is zero!");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsThrowsIllegalArgExcWhenStatusIsNull(){
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
            5, null, RocketStatus.STATUS_ACTIVE),
           "Get Wiki Pages For Rockets Used In Most Expensive Missions should throw Illegal Argument Exception" +
               " when mission status is null" );
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
            5, MissionStatus.SUCCESS, null),
            "Get Wiki Pages For Rockets Used In Most Expensive Missions should throw Illegal Argument Exception" +
                " when mission status is null" );
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsSuccessfully(){
        List<String> expectedWikis = List.of(
            "https://en.wikipedia.org/wiki/Proton-M",
            "https://en.wikipedia.org/wiki/Falcon_9"
        );
        var result = spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(2, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);
        assertEquals(expectedWikis.size(), result.size(), "Expected and result lists should have same size!");
        assertTrue(expectedWikis.containsAll(result));
        assertTrue(result.containsAll(expectedWikis));
    }

    @Test
    void testSaveMostReliableRocketThrowsIllegalArgExcWhenOutputStrIsNull(){
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.saveMostReliableRocket(
            null, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 12)),
            "Save Most Reliable Rocket should throw Illegal Argument Exception when OtuputStream is null!");
    }

    @Test
    void testSaveMostReliableRocketThrowsIllegalArgExcWhenDateIsNull(){
        OutputStream outputStream = new ByteArrayOutputStream();
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.saveMostReliableRocket(outputStream, null, LocalDate.of(2020, 1, 1)),
            "Save Most Reliable Rocket should throw Illegal Argument Exception when from date is null");
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.saveMostReliableRocket(outputStream,  LocalDate.of(2020, 1, 1), null),
            "Save Most Reliable Rocket should throw Illegal Argument Exception when to date is null");
    }

    @Test
    void testSaveMostReliableRocketThrowsTimeFrameMismatchExcWhenToIsBeforeFrom(){
        OutputStream outputStream = new ByteArrayOutputStream();
        assertThrows(TimeFrameMismatchException.class, () -> spaceScanner.saveMostReliableRocket(
            outputStream, LocalDate.of(2020, 12, 1), LocalDate.of(2020, 1, 1)),
            "Save Most Reliable Rocket should throw Time Frame Mismatch Exception when to date is before from date");
    }

    @Test
    void testSaveMostReliableRocketSuccessfully() throws CipherException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        spaceScannerForTestingReliability.saveMostReliableRocket(outputStream,
            LocalDate.of(2016, 1, 1), LocalDate.of(2020, 12, 31));
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        SymmetricBlockCipher algo = new Rijndael(secretKey);
        ByteArrayOutputStream res = new ByteArrayOutputStream();
        algo.decrypt(inputStream, res);
        assertEquals("Falcon 9 Block 5", res.toString());
    }
}
