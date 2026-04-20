package com.example.spacecolonypioneers.model;

public class CrewStatistics {
    private int crewId;
    private String crewName;
    private int missionsCompleted;
    private int totalDamageDealt;
    private int totalDamageTaken;
    private int totalHealingDone;

    public CrewStatistics() {
    }

    public CrewStatistics(int crewId, String crewName) {
        this.crewId = crewId;
        this.crewName = crewName;
    }

    public void incrementMissionsCompleted() { missionsCompleted++; }
    public void addDamageDealt(int damage) { totalDamageDealt += Math.max(0, damage); }
    public void addDamageTaken(int damage) { totalDamageTaken += Math.max(0, damage); }
    public void addHealingDone(int healing) { totalHealingDone += Math.max(0, healing); }

    public String getCrewName() { return crewName; }
    public int getMissionsCompleted() { return missionsCompleted; }
    public int getTotalDamageDealt() { return totalDamageDealt; }
    public int getTotalDamageTaken() { return totalDamageTaken; }
    public int getTotalHealingDone() { return totalHealingDone; }
}
