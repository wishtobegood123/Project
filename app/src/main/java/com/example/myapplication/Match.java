package com.example.myapplication;

public class Match implements SoccerEntity {
    private final String id;
    private final String homeTeam;
    private final String awayTeam;
    private final String score;
    private final String league;
    private final String date;
    private final String stadium;

    public Match(String id, String homeTeam, String awayTeam, String score, String league, String date, String stadium) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.score = score;
        this.league = league;
        this.date = date;
        this.stadium = stadium;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getName() { return homeTeam + " vs " + awayTeam; }
    public String getHomeTeam() { return homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public String getScore() { return score; }
    public String getLeague() { return league; }
    public String getDate() { return date; }
    public String getStadium() { return stadium; }
}