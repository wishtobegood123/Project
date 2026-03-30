package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class DataProvider {
    public static List<Team> createSampleTeams() {
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("1", "FC Barcelona", "Spain", "La Liga", "Camp Nou", 1899));
        teams.add(new Team("2", "Manchester United", "England", "Premier League", "Old Trafford", 1878));
        teams.add(new Team("3", "Bayern Munich", "Germany", "Bundesliga", "Allianz Arena", 1900));
        teams.add(new Team("4", "Juventus", "Italy", "Serie A", "Allianz Stadium", 1897));
        teams.add(new Team("5", "Paris Saint-Germain", "France", "Ligue 1", "Parc des Princes", 1970));
        teams.add(new Team("6", "Ajax Amsterdam", "Netherlands", "Eredivisie", "Johan Cruyff Arena", 1900));
        teams.add(new Team("7", "River Plate", "Argentina", "Primera División", "El Monumental", 1901));
        teams.add(new Team("8", "Flamengo", "Brazil", "Brasileirão", "Maracanã", 1895));
        return teams;
    }

    public static List<Player> createSamplePlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("1", "Lionel Messi", 34, "Argentina", "Forward", "FC Barcelona", 10));
        players.add(new Player("2", "Cristiano Ronaldo", 36, "Portugal", "Forward", "Juventus", 7));
        players.add(new Player("3", "Robert Lewandowski", 32, "Poland", "Forward", "Bayern Munich", 9));
        players.add(new Player("4", "Kevin De Bruyne", 29, "Belgium", "Midfielder", "Manchester City", 17));
        players.add(new Player("5", "Virgil van Dijk", 30, "Netherlands", "Defender", "Liverpool", 4));
        players.add(new Player("6", "Manuel Neuer", 35, "Germany", "Goalkeeper", "Bayern Munich", 1));
        players.add(new Player("7", "Kylian Mbappé", 22, "France", "Forward", "Paris Saint-Germain", 7));
        players.add(new Player("8", "Erling Haaland", 20, "Norway", "Forward", "Borussia Dortmund", 9));
        players.add(new Player("9", "Bruno Fernandes", 26, "Portugal", "Midfielder", "Manchester United", 18));
        players.add(new Player("10", "Joshua Kimmich", 26, "Germany", "Midfielder", "Bayern Munich", 6));
        players.add(new Player("11", "Jan Oblak", 28, "Slovenia", "Goalkeeper", "Atletico Madrid", 13));
        players.add(new Player("12", "Neymar Jr.", 29, "Brazil", "Forward", "Paris Saint-Germain", 10));
        return players;
    }

    public static List<Match> createSampleMatches() {
        List<Match> matches = new ArrayList<>();
        matches.add(new Match("1", "FC Barcelona", "Real Madrid", "2-1", "La Liga", "2023-04-10", "Camp Nou"));
        matches.add(new Match("2", "Manchester United", "Liverpool", "0-3", "Premier League", "2023-03-15", "Old Trafford"));
        matches.add(new Match("3", "Bayern Munich", "Borussia Dortmund", "4-2", "Bundesliga", "2023-04-01", "Allianz Arena"));
        matches.add(new Match("4", "Juventus", "AC Milan", "1-1", "Serie A", "2023-03-20", "Allianz Stadium"));
        matches.add(new Match("5", "Paris Saint-Germain", "Lyon", "3-0", "Ligue 1", "2023-04-05", "Parc des Princes"));
        matches.add(new Match("6", "FC Barcelona", "Bayern Munich", "0-3", "Champions League", "2023-02-28", "Camp Nou"));
        matches.add(new Match("7", "Manchester City", "Paris Saint-Germain", "2-1", "Champions League", "2023-03-08", "Etihad Stadium"));
        matches.add(new Match("8", "Liverpool", "Ajax Amsterdam", "1-0", "Champions League", "2023-03-01", "Anfield"));
        return matches;
    }
}
