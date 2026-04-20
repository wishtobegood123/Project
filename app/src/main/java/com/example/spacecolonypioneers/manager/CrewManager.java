package com.example.spacecolonypioneers.manager;

import com.example.spacecolonypioneers.model.CrewMember;
import com.example.spacecolonypioneers.model.GameState;

public class CrewManager {
    public static void train(CrewMember crew) {
        if (crew == null) return;
        crew.setXp(crew.getXp() + 25);
        crew.setEnergy(crew.getEnergy() - 15);
        GameState.getInstance().getStatistics().incrementTrainingSessions();
        checkLevelUp(crew);
    }

    public static void rest(CrewMember crew) {
        if (crew == null) return;
        crew.setHp(crew.getHp() + 50);
        crew.setEnergy(crew.getEnergy() + 35);
        if (crew.isInjured() && crew.getHp() >= crew.getMaxHp() * 0.5f) {
            crew.setInjured(false);
        }
    }

    public static void checkLevelUp(CrewMember crew) {
        if (crew == null) return;
        int xpNeeded = crew.getLevel() * 50;
        while (crew.getXp() >= xpNeeded && xpNeeded > 0) {
            crew.setXp(crew.getXp() - xpNeeded);
            crew.setLevel(crew.getLevel() + 1);
            crew.recalculateStats();
            crew.setHp(crew.getMaxHp());
            crew.setEnergy(crew.getMaxEnergy());
            crew.setShield(0);
            xpNeeded = crew.getLevel() * 50;
        }
    }
}
