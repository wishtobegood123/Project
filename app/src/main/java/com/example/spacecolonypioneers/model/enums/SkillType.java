package com.example.spacecolonypioneers.model.enums;

public enum SkillType {
    HEAL("First Aid Kit", "Restore target HP", 25),
    REPAIR("Energy Shield", "Generate a shield for the team to absorb damage", 30),
    RAGE_SHOT("Rage Shot", "Deal double damage", 35),
    SCOUT("Tactical Recon", "Reduce enemy attack", 20),
    INSPIRE("Battlefield Inspire", "Increase team attack", 40);

    private final String displayName;
    private final String description;
    private final int energyCost;

    SkillType(String displayName, String description, int energyCost) {
        this.displayName = displayName;
        this.description = description;
        this.energyCost = energyCost;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getEnergyCost() {
        return energyCost;
    }
}
