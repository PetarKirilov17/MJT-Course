package bg.sofia.uni.fmi.mjt.football;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FootballPlayerAnalyzerTest {
    private static StringReader reader;

    private static StringReader shortReader;
    private static FootballPlayerAnalyzer analyzer;
    private static FootballPlayerAnalyzer shortAnalyzer;

    @BeforeAll
    static void setUp() {
        reader = new StringReader(
            """
                name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
                L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left
                C. Eriksen;Christian  Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right
                P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right
                V. van Dijk;Virgil van Dijk;7/8/1991;27;193.04;92.1;CB;Netherlands;88;90;59500000;215000;Right
                K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right
                M. Neuer;Manuel Neuer;3/27/1986;32;193.04;92.1;GK;Germany;89;89;38000000;130000;Right"""
        );

        shortReader = new StringReader(
            """
                name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
                L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left
                K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right"""
        );
        analyzer = new FootballPlayerAnalyzer(reader);
        shortAnalyzer = new FootballPlayerAnalyzer(shortReader);
    }

    @Test
    void testGetAllPlayers() {
        List<Player> expectedPlayers = List.of(
            Player.of(
                "L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left"),
            Player.of(
                "C. Eriksen;Christian  Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right"),
            Player.of("P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right"),
            Player.of("V. van Dijk;Virgil van Dijk;7/8/1991;27;193.04;92.1;CB;Netherlands;88;90;59500000;215000;Right"),
            Player.of("K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right"),
            Player.of("M. Neuer;Manuel Neuer;3/27/1986;32;193.04;92.1;GK;Germany;89;89;38000000;130000;Right")
        );
        assertIterableEquals(expectedPlayers, analyzer.getAllPlayers(),
            "Get All Players should return all players from the expected list!");
    }

    @Test
    void testGetAllNationalities() {
        Set<String> expected = Set.of("Argentina", "Denmark", "France", "Netherlands", "Germany");
        var result = analyzer.getAllNationalities();
        assertEquals(expected, result,
            "Test get all nationalities should return the countries int the expected set!");
    }

    @Test
    void testGetHighestPaidPlayerByNationalityThrowsExc() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.getHighestPaidPlayerByNationality(null),
            "GetHighestPaidPlayerByNationality should throw IllegalArgumentException when nationality is null!");
    }

    @Test
    void testGetHighestPaidPlayerByNationalityThrowsNoSuchElementExc() {
        assertThrows(NoSuchElementException.class, () -> analyzer.getHighestPaidPlayerByNationality("USA"),
            "GetHighestPaidPlayerByNationality should throw NoSuchElementException when players from the country do not exist!");
    }

    @Test
    void testGetHighestPaidPlayerByNationalitySuccessfully() {
        Player highestExpected =
            Player.of("P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right");
        assertEquals(highestExpected, analyzer.getHighestPaidPlayerByNationality("France"),
            "GetHighestPaidPlayerByNationality should return the expected player!");
    }

    @Test
    void testGroupByPosition() {
        var stPlayers = Set.of(Player.of(
                "L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left"),
            Player.of("K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right"));

        var rwPlayers = Set.of(Player.of(
                "L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left"),
            Player.of("K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right"));

        var cfPlayers = Set.of(Player.of(
            "L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left"));
        var rmPlayers = Set.of(
            Player.of("K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right"));

        Map<Position, Set<Player>> expected =
            Map.of(Position.ST, stPlayers, Position.RW, rwPlayers, Position.CF, cfPlayers, Position.RM, rmPlayers);
        var result = shortAnalyzer.groupByPosition();

        assertEquals(expected, result, "GroupByPosition should return the map as expected!");
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetThrowsExcWhenPosIsNull() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.getTopProspectPlayerForPositionInBudget(null, 10),
            "GetTopProspectPlayerForPositionInBudget should throw IllegalArgumentException when position is null");
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetThrowsExcWhenBalanceIsNegative() {
        assertThrows(IllegalArgumentException.class,
            () -> analyzer.getTopProspectPlayerForPositionInBudget(Position.CF, -10),
            "GetTopProspectPlayerForPositionInBudget should throw IllegalArgumentException when balance is negative");
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetSuccessfully() {
        Player topProspect =
            Player.of("K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right");
        assertEquals(topProspect, shortAnalyzer.getTopProspectPlayerForPositionInBudget(Position.ST, 1000000000).get(),
            "GetTopProspectPlayerForPositionInBudget should return the expected top prospect player");
    }

    @Test
    void testGetSimilarPlayersThrowsExc() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.getSimilarPlayers(null),
            "GetSimilarPlayers should throw IllegalArgumentException when Player is null!");
    }

    @Test
    void testGetSimilarPlayersSuccessfully() {
        var similar = Set.of(Player.of(
                "C. Eriksen;Christian  Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right"),
            Player.of("P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right"));
        assertEquals(similar, analyzer.getSimilarPlayers(
            Player.of("P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right")));
    }

    @Test
    void testGetPlayersByFullNameKeywordThrowsExc() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.getPlayersByFullNameKeyword(null),
            "getPlayersByFullNameKeyword should throw IllegalArgumentException when keyword is null!");
    }

    @Test
    void testGetPlayersByFullNameSuccessfully() {
        var player = Player.of("L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left");
        var expected = Set.of(player);
        assertEquals(expected, analyzer.getPlayersByFullNameKeyword("Messi"),
            "GetPlayersByFullName should return the expected set of players!");
    }
}
