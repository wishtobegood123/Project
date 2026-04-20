package com.example.spacecolonypioneers.model.enums;

public enum MissionModifier {
    NONE("None", 1.0, 1.0),
    DOUBLE_XP("Double XP", 2.0, 1.0),
    DOUBLE_FRAGMENTS("Double Fragments", 1.0, 2.0),
    TOUGH_ENEMIES("Tough Enemies", 1.2, 1.5),
    FAST_MISSION("Quick Strike", 0.8, 0.8),
    ELITE_TEAM("Elite Team", 1.5, 1.5);

    private final String displayName;
    private final double xpMultiplier;
    private final double difficultyMultiplier;

    MissionModifier(String displayName, double xpMultiplier, double difficultyMultiplier) {
        this.displayName = displayName;
        this.xpMultiplier = xpMultiplier;
        this.difficultyMultiplier = difficultyMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }

    public double getDifficultyMultiplier() {
        return difficultyMultiplier;
    }
}
