package com.example.myapplication;

public class Team implements SoccerEntity {
    private final String id;
    private final String name;
    private final String country;
    private final String league;
    private final String stadium;
    private final int foundedYear;

    public Team(String id, String name, String country, String league, String stadium, int foundedYear) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.league = league;
        this.stadium = stadium;
        this.foundedYear = foundedYear;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getName() { return name; }
    public String getCountry() { return country; }
    public String getLeague() { return league; }
    public String getStadium() { return stadium; }
    public int getFoundedYear() { return foundedYear; }
}
