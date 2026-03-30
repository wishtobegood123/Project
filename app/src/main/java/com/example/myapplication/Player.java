package com.example.myapplication;

public class Player implements SoccerEntity {
    private final String id;
    private final String name;
    private final int age;
    private final String country;
    private final String position;
    private final String teamName;
    private final int number;

    public Player(String id, String name, int age, String country, String position, String teamName, int number) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.country = country;
        this.position = position;
        this.teamName = teamName;
        this.number = number;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCountry() { return country; }
    public String getPosition() { return position; }
    public String getTeamName() { return teamName; }
    public int getNumber() { return number; }
}
