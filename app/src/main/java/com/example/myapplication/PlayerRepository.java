package com.example.myapplication;

import java.util.List;

public class PlayerRepository extends Repository<Player> {
    public List<Player> filterByTeam(String team) {
        return filter(player -> player.getTeamName().equals(team));
    }
}
