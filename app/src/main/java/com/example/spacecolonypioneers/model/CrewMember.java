package com.example.spacecolonypioneers.model;

import com.example.spacecolonypioneers.model.enums.Assignment;
import com.example.spacecolonypioneers.model.enums.Profession;

public class CrewMember {
    private int id;
    private String name;
    private Profession profession;
    private int hp;
    private int maxHp;
    private int shield;
    private int maxShield;
    private int energy;
    private int maxEnergy;
    private int xp;
    private int level;
    private Assignment assignment;
    private boolean injured;
    private int daysSinceLastCombat;
    private boolean recruited;

    public CrewMember() {
    }

    public CrewMember(int id, String name, Profession profession) {
        this.id = id;
        this.name = name;
        this.profession = profession;
        ProfessionConfig config = ProfessionConfig.getConfig(profession);
        this.maxHp = config.getBaseMaxHp();
        this.hp = maxHp;
        this.maxShield = 50;
        this.shield = 0;
        this.maxEnergy = 60;
        this.energy = maxEnergy;
        this.xp = 0;
        this.level = 1;
        this.assignment = Assignment.UNASSIGNED;
        this.injured = false;
        this.daysSinceLastCombat = 2;
        this.recruited = true;
    }

    public void recalculateStats() {
        ProfessionConfig config = ProfessionConfig.getConfig(profession);
        if (config == null) return;
        this.maxHp = config.getBaseMaxHp() + (int) (config.getHpGrowth() * (level - 1));
        this.maxEnergy = 60 + (level - 1) * 10;
        this.maxShield = 50 + level * 5;
        if (hp > maxHp) hp = maxHp;
        if (shield > maxShield) shield = maxShield;
        if (energy > maxEnergy) energy = maxEnergy;
    }

    public void takeDamage(int damage) {
        if (shield > 0) {
            if (damage <= shield) {
                shield -= damage;
                return;
            }
            damage -= shield;
            shield = 0;
        }
        hp = Math.max(0, hp - damage);
    }
    public boolean isAvailableForCombat() {
        return daysSinceLastCombat >= 1 && !injured;
    }
    public int getDaysUntilCombatReady() {
        if (daysSinceLastCombat >= 1) return 0;
        return 1 - daysSinceLastCombat;
    }
    public void markAsParticipatedInCombat() {
        this.daysSinceLastCombat = 0;
    }
    public void advanceDay() {
        if (daysSinceLastCombat < 2) {
            daysSinceLastCombat++;
        }
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Profession getProfession() { return profession; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(0, Math.min(hp, maxHp)); }
    public int getMaxHp() { return maxHp; }
    public int getShield() { return shield; }
    public void setShield(int shield) { this.shield = Math.max(0, Math.min(shield, maxShield)); }
    public int getMaxShield() { return maxShield; }
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = Math.max(0, Math.min(energy, maxEnergy)); }
    public int getMaxEnergy() { return maxEnergy; }
    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }
    public boolean isInjured() { return injured; }
    public void setInjured(boolean injured) { this.injured = injured; }
    public int getDaysSinceLastCombat() { return daysSinceLastCombat; }
    public boolean isRecruited() { return recruited; }
    public void setRecruited(boolean recruited) { this.recruited = recruited; }
}
