package com.example.spacecolonypioneers.model.enums;

public enum Profession {
    MEDIC("Medic"),
    ENGINEER("Engineer"),
    SOLDIER("Soldier"),
    SCOUT("Scout"),
    COMMANDER("Commander");

    private final String displayName;

    Profession(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
