package com.example.spacecolonypioneers.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStatistics {
    private int totalMissions;
    private int successfulMissions;
    private int totalTrainingSessions;
    private int totalXpEarned;
    private int totalFragmentsCollected;
    private Map<Integer, CrewStatistics> crewStatsMap;

    public GameStatistics() {
        crewStatsMap = new HashMap<Integer, CrewStatistics>();
    }

    public void initializeCrewStats(List<CrewMember> crewList) {
        if (crewList == null) return;
        for (CrewMember crew : crewList) {
            if (crew != null && !crewStatsMap.containsKey(crew.getId())) {
                crewStatsMap.put(crew.getId(), new CrewStatistics(crew.getId(), crew.getName()));
            }
        }
    }

    public void incrementTotalMissions() { totalMissions++; }
    public void incrementSuccessfulMissions() { successfulMissions++; }
    public void incrementTrainingSessions() { totalTrainingSessions++; }
    public void addXpEarned(int xp) { totalXpEarned += Math.max(0, xp); }
    public void addFragmentsCollected(int fragments) { totalFragmentsCollected += Math.max(0, fragments); }
    public CrewStatistics getCrewStats(int crewId) { return crewStatsMap.get(crewId); }
    public List<CrewStatistics> getAllCrewStats() { return new ArrayList<CrewStatistics>(crewStatsMap.values()); }
    public int getTotalMissions() { return totalMissions; }
    public int getSuccessfulMissions() { return successfulMissions; }
    public int getTotalTrainingSessions() { return totalTrainingSessions; }
    public int getTotalXpEarned() { return totalXpEarned; }
    public int getTotalFragmentsCollected() { return totalFragmentsCollected; }

    public double getSuccessRate() {
        if (totalMissions == 0) return 0;
        return (double) successfulMissions / totalMissions * 100;
    }
}
