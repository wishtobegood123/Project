package com.example.spacecolonypioneers.model.enums;

public enum Phase {
    SCHEDULING("Crew Scheduling"),
    PROGRESSION("Progress Processing"),
    MISSION_SELECTION("Mission Selection"),
    COMBAT("Combat"),
    RESULTS("Results");

    private final String displayName;

    Phase(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
