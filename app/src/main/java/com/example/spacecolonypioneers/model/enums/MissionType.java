package com.example.spacecolonypioneers.model.enums;

public enum MissionType {
    EXPLORATION("Exploration Mission"),
    COMBAT("Combat Mission"),
    RESCUE("Rescue Mission"),
    RESEARCH("Research Mission"),
    CONSTRUCTION("Construction Mission");

    private final String displayName;

    MissionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
