import bg.sofia.uni.fmi.mjt.football.FootballPlayerAnalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args){
        try(var reader = new BufferedReader(new FileReader("fifa_players_clean.csv"))){
            FootballPlayerAnalyzer analyzer = new FootballPlayerAnalyzer(reader);
        }
        catch (IOException e){

        }

    }
}
