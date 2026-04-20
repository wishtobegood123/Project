package com.example.spacecolonypioneers.manager;

import com.example.spacecolonypioneers.model.CrewMember;
import com.example.spacecolonypioneers.model.GameState;
import com.example.spacecolonypioneers.model.GameStatistics;
import com.example.spacecolonypioneers.model.Mission;
import com.example.spacecolonypioneers.model.MissionTemplate;
import com.example.spacecolonypioneers.model.SquadBonus;
import com.example.spacecolonypioneers.model.enums.MissionModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MissionManager {
    private static final Random RANDOM = new Random();

    public static void generateDailyMissions() {
        GameState state = GameState.getInstance();
        if (state == null) return;
        List<Mission> missions = new ArrayList<Mission>();
        List<MissionTemplate> templates = MissionTemplate.getTemplates();
        int missionCount = 3 + RANDOM.nextInt(2);
        boolean hasLevel1Mission = false;
        
        for (int i = 0; i < missionCount; i++) {
            MissionTemplate template = templates.get(RANDOM.nextInt(templates.size()));
            Mission mission = createMissionFromTemplate(i + 1, template, i == 0);
            if (mission != null) {
                missions.add(mission);
                if (mission.getDifficulty() == 1) {
                    hasLevel1Mission = true;
                }
            }
        }
        if (!hasLevel1Mission && !missions.isEmpty()) {
            MissionTemplate easyTemplate = templates.get(RANDOM.nextInt(templates.size()));
            Mission level1Mission = createLevel1Mission(missions.size(), easyTemplate);
            if (level1Mission != null) {
                missions.set(0, level1Mission);
            }
        }
        
        state.getMissionList().clear();
        state.getMissionList().addAll(missions);
    }

    private static Mission createMissionFromTemplate(int id, MissionTemplate template, boolean forceEasy) {
        if (template == null) return null;
        
        int difficulty;
        if (forceEasy) {
            difficulty = 1;
        } else {
            difficulty = template.getMinDifficulty() + RANDOM.nextInt(template.getMaxDifficulty() - template.getMinDifficulty() + 1);
        }
        
        MissionModifier modifier = getRandomModifier();
        String name = template.getNamePrefix() + template.getNameSuffix();
        int xp = template.getBaseXp() * difficulty;
        int fragments = template.getBaseFragments() * difficulty;
        int progress = template.getBaseProgress() * difficulty;
        return new Mission(id, name, template.getType(), difficulty, xp, fragments, progress, modifier, template.getEnemyTypes());
    }
    private static Mission createLevel1Mission(int id, MissionTemplate template) {
        if (template == null) return null;
        int difficulty = 1;
        MissionModifier modifier = MissionModifier.NONE;
        String name = template.getNamePrefix() + template.getNameSuffix();
        int xp = template.getBaseXp() * difficulty;
        int fragments = template.getBaseFragments() * difficulty;
        int progress = template.getBaseProgress() * difficulty;
        return new Mission(id, name, template.getType(), difficulty, xp, fragments, progress, modifier, template.getEnemyTypes());
    }

    private static MissionModifier getRandomModifier() {
        int roll = RANDOM.nextInt(100);
        if (roll < 40) return MissionModifier.NONE;
        if (roll < 55) return MissionModifier.DOUBLE_XP;
        if (roll < 70) return MissionModifier.DOUBLE_FRAGMENTS;
        if (roll < 82) return MissionModifier.TOUGH_ENEMIES;
        if (roll < 92) return MissionModifier.FAST_MISSION;
        return MissionModifier.ELITE_TEAM;
    }

    public static void completeMission(Mission mission, SquadBonus bonus) {
        GameState state = GameState.getInstance();
        if (state == null || mission == null || bonus == null) return;
        GameStatistics stats = state.getStatistics();
        if (stats == null) return;

        int finalXp = (int) (mission.getRewardXp() * bonus.getXpBonus());
        if (finalXp < 100) finalXp = 100;
        
        int finalFragments = mission.getRewardFragments();
        int rewardResources = Math.max(100, finalFragments / 2);
        
        state.setTotalProgress(state.getTotalProgress() + mission.getRewardProgress());
        state.setTotalFragments(state.getTotalFragments() + finalFragments);
        state.setResources(state.getResources() + rewardResources);
        stats.addXpEarned(finalXp);
        stats.addFragmentsCollected(finalFragments);
        stats.incrementSuccessfulMissions();

        List<CrewMember> squad = state.getCurrentSquad();
        if (squad != null && !squad.isEmpty()) {
            for (CrewMember crew : squad) {
                if (crew != null && !crew.isInjured()) {
                    int crewXp = finalXp / squad.size();
                    crew.setXp(crew.getXp() + crewXp);
                    CrewManager.checkLevelUp(crew);
                }
                if (crew != null && stats.getCrewStats(crew.getId()) != null) {
                    stats.getCrewStats(crew.getId()).incrementMissionsCompleted();
                }
            }
        }
        mission.setCompleted(true);
    }
}
