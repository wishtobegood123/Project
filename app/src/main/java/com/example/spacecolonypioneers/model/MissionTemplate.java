package com.example.spacecolonypioneers.model;

import com.example.spacecolonypioneers.model.enums.MissionType;

import java.util.Arrays;
import java.util.List;

public class MissionTemplate {
    private String namePrefix;
    private String nameSuffix;
    private MissionType type;
    private int minDifficulty;
    private int maxDifficulty;
    private int baseXp;
    private int baseFragments;
    private int baseProgress;
    private List<String> enemyTypes;

    public MissionTemplate() {
    }

    public MissionTemplate(String namePrefix, String nameSuffix, MissionType type, int minDifficulty,
                           int maxDifficulty, int baseXp, int baseFragments, int baseProgress,
                           List<String> enemyTypes) {
        this.namePrefix = namePrefix;
        this.nameSuffix = nameSuffix;
        this.type = type;
        this.minDifficulty = minDifficulty;
        this.maxDifficulty = maxDifficulty;
        this.baseXp = baseXp;
        this.baseFragments = baseFragments;
        this.baseProgress = baseProgress;
        this.enemyTypes = enemyTypes;
    }

    public static List<MissionTemplate> getTemplates() {
        return Arrays.asList(
                new MissionTemplate("Alien", "Recon", MissionType.EXPLORATION, 1, 3, 50, 10, 5, Arrays.asList("Scout", "Skirmisher")),
                new MissionTemplate("Resource", "Gathering", MissionType.RESEARCH, 2, 4, 80, 20, 10, Arrays.asList("Guard", "Mech")),
                new MissionTemplate("Enemy Camp", "Raid", MissionType.COMBAT, 3, 5, 120, 30, 15, Arrays.asList("Warrior", "Elite")),
                new MissionTemplate("Survivor", "Rescue", MissionType.RESCUE, 2, 4, 100, 25, 12, Arrays.asList("Predator", "Hunter")),
                new MissionTemplate("Outpost", "Construction", MissionType.CONSTRUCTION, 1, 3, 60, 15, 8, Arrays.asList("Wanderer", "Swarm"))
        );
    }

    public String getNamePrefix() { return namePrefix; }
    public String getNameSuffix() { return nameSuffix; }
    public MissionType getType() { return type; }
    public int getMinDifficulty() { return minDifficulty; }
    public int getMaxDifficulty() { return maxDifficulty; }
    public int getBaseXp() { return baseXp; }
    public int getBaseFragments() { return baseFragments; }
    public int getBaseProgress() { return baseProgress; }
    public List<String> getEnemyTypes() { return enemyTypes; }
}
