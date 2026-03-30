package com.example.myapplication;

import java.util.List;
import java.util.function.Predicate;

public class MatchRepository extends Repository<Match> {
    public List<Match> filterByTeam(String team) {
        return filter(new Predicate<Match>() {
            @Override
            public boolean test(Match match) {
                return match.getHomeTeam().equals(team) || match.getAwayTeam().equals(team);
            }
        });
    }
}
