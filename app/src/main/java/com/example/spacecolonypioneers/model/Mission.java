package com.example.spacecolonypioneers.model;

import com.example.spacecolonypioneers.model.enums.MissionModifier;
import com.example.spacecolonypioneers.model.enums.MissionType;

import java.util.ArrayList;
import java.util.List;

public class Mission {
    private int id;
    private String name;
    private MissionType type;
    private int difficulty;
    private int threatLevel;
    private int rewardXp;
    private int rewardFragments;
    private int rewardProgress;
    private MissionModifier modifier;
    private List<String> enemyTypes;
    private boolean completed;
    private boolean selected;

    public Mission() {
    }

    public Mission(int id, String name, MissionType type, int difficulty, int rewardXp,
                   int rewardFragments, int rewardProgress, MissionModifier modifier,
                   List<String> enemyTypes) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.difficulty = difficulty;
        this.modifier = modifier != null ? modifier : MissionModifier.NONE;
        this.threatLevel = (int) (difficulty * this.modifier.getDifficultyMultiplier());
        this.rewardXp = (int) (rewardXp * this.modifier.getXpMultiplier());
        this.rewardFragments = rewardFragments;
        this.rewardProgress = rewardProgress;
        this.enemyTypes = enemyTypes != null ? new ArrayList<>(enemyTypes) : new ArrayList<String>();
        this.completed = false;
        this.selected = false;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public MissionType getType() { return type; }
    public int getDifficulty() { return difficulty; }
    public int getThreatLevel() { return threatLevel; }
    public int getRewardXp() { return rewardXp; }
    public int getRewardFragments() { return rewardFragments; }
    public int getRewardProgress() { return rewardProgress; }
    public MissionModifier getModifier() { return modifier; }
    public List<String> getEnemyTypes() { return enemyTypes; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}
