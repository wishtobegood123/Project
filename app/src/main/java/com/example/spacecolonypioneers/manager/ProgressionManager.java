package com.example.spacecolonypioneers.manager;

import com.example.spacecolonypioneers.model.CrewMember;
import com.example.spacecolonypioneers.model.GameState;
import com.example.spacecolonypioneers.model.enums.Assignment;
import com.example.spacecolonypioneers.model.enums.Profession;

import java.util.List;

public class ProgressionManager {
    public static void processProgression() {
        GameState state = GameState.getInstance();
        if (state == null) return;
        List<CrewMember> crewList = state.getCrewList();
        if (crewList == null) return;

        CrewMember medic = null;
        for (CrewMember crew : crewList) {
            if (crew != null && crew.getProfession() == Profession.MEDIC
                    && crew.getAssignment() == Assignment.QUARTERS && !crew.isInjured()) {
                medic = crew;
                break;
            }
        }

        for (CrewMember crew : crewList) {
            if (crew == null) continue;
            crew.advanceDay();
            
            if (crew.getAssignment() == Assignment.QUARTERS) {
                crew.setHp(crew.getHp() + 40);
                crew.setEnergy(crew.getEnergy() + 25);
                if (medic != null && medic != crew) {
                    crew.setHp(crew.getHp() + 25);
                }
                if (crew.isInjured() && crew.getHp() >= crew.getMaxHp() * 0.6f) {
                    crew.setInjured(false);
                }
            } else if (crew.getAssignment() == Assignment.SIMULATOR) {
                crew.setXp(crew.getXp() + 20);
                crew.setEnergy(crew.getEnergy() - 10);
                CrewManager.checkLevelUp(crew);
            }
            if (crew.getEnergy() < crew.getMaxEnergy()) {
                crew.setEnergy(crew.getEnergy() + 5);
            }
        }
        
        state.setDay(state.getDay() + 1);
    }
}
