package com.example.myapplication;

import java.util.List;
import java.util.function.Predicate;

public class TeamRepository extends Repository<Team> {
    public List<Team> filterByLeague(String league) {
        return filter(new Predicate<Team>() {
            @Override
            public boolean test(Team team) {
                return team.getLeague().equals(league);
            }
        });
    }
}