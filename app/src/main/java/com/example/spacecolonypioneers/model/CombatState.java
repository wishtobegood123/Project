package com.example.spacecolonypioneers.model;

import java.util.ArrayList;
import java.util.List;

public class CombatState {
    private List<CrewMember> playerTeam;
    private List<Enemy> enemyTeam;
    private int currentTurn;
    private boolean isPlayerTurn;
    private CrewMember selectedCrew;
    private Enemy selectedEnemy;
    private List<CombatLogEntry> log;
    private boolean combatEnded;
    private boolean playerWon;
    private int teamAttackBonus;
    private int enemyAttackDebuff;
    private int teamAttackBonusPercent;
    private List<Integer> actedCrewIds;
    public static class EnemyAction {
        public int enemyId;
        public ActionType type;
        public String description;
        public int value;
        public int targetCrewId;
        
        public enum ActionType {
            ATTACK("Attack"),
            BUFF_ATTACK("Empower Attack"),
            DEBUFF_PLAYER("Debuff Team");
            
            private final String displayName;
            ActionType(String displayName) { this.displayName = displayName; }
            public String getDisplayName() { return displayName; }
        }
        
        public EnemyAction(int enemyId, ActionType type, String description, int value, int targetCrewId) {
            this.enemyId = enemyId;
            this.type = type;
            this.description = description;
            this.value = value;
            this.targetCrewId = targetCrewId;
        }
    }
    
    private java.util.Map<Integer, EnemyAction> pendingEnemyActions;
    private java.util.Map<Integer, EnemyAction.ActionType> lastEnemyActions;

    public CombatState() {
        playerTeam = new ArrayList<CrewMember>();
        enemyTeam = new ArrayList<Enemy>();
        currentTurn = 1;
        isPlayerTurn = true;
        log = new ArrayList<CombatLogEntry>();
        combatEnded = false;
        playerWon = false;
        teamAttackBonus = 0;
        enemyAttackDebuff = 0;
        teamAttackBonusPercent = 0;
        actedCrewIds = new ArrayList<Integer>();
        pendingEnemyActions = new java.util.HashMap<Integer, EnemyAction>();
        lastEnemyActions = new java.util.HashMap<Integer, EnemyAction.ActionType>();
    }

    public List<CrewMember> getPlayerTeam() { return playerTeam; }
    public List<Enemy> getEnemyTeam() { return enemyTeam; }
    public int getCurrentTurn() { return currentTurn; }
    public void setCurrentTurn(int currentTurn) { this.currentTurn = currentTurn; }
    public boolean isPlayerTurn() { return isPlayerTurn; }
    public void setPlayerTurn(boolean playerTurn) { isPlayerTurn = playerTurn; }
    public CrewMember getSelectedCrew() { return selectedCrew; }
    public void setSelectedCrew(CrewMember selectedCrew) { this.selectedCrew = selectedCrew; }
    public Enemy getSelectedEnemy() { return selectedEnemy; }
    public void setSelectedEnemy(Enemy selectedEnemy) { this.selectedEnemy = selectedEnemy; }
    public List<CombatLogEntry> getLog() { return log; }
    public void addLog(CombatLogEntry entry) { if (entry != null) log.add(entry); }
    public boolean isCombatEnded() { return combatEnded; }
    public void setCombatEnded(boolean combatEnded) { this.combatEnded = combatEnded; }
    public boolean isPlayerWon() { return playerWon; }
    public void setPlayerWon(boolean playerWon) { this.playerWon = playerWon; }
    public int getTeamAttackBonus() { return teamAttackBonus; }
    public void setTeamAttackBonus(int teamAttackBonus) { this.teamAttackBonus = teamAttackBonus; }
    public int getEnemyAttackDebuff() { return enemyAttackDebuff; }
    public void setEnemyAttackDebuff(int enemyAttackDebuff) { this.enemyAttackDebuff = enemyAttackDebuff; }
    public int getTeamAttackBonusPercent() { return teamAttackBonusPercent; }
    public void setTeamAttackBonusPercent(int percent) { this.teamAttackBonusPercent = percent; }
    public List<Integer> getActedCrewIds() { return actedCrewIds; }
    public void resetActedCrews() { actedCrewIds.clear(); }
    public boolean hasCrewActed(int crewId) { return actedCrewIds.contains(crewId); }
    public void markCrewAsActed(int crewId) { 
        if (!actedCrewIds.contains(crewId)) {
            actedCrewIds.add(crewId);
        }
    }
    public java.util.Map<Integer, EnemyAction> getPendingEnemyActions() { return pendingEnemyActions; }
    public void clearPendingActions() { pendingEnemyActions.clear(); }
    public void setPendingAction(EnemyAction action) { 
        if (action != null) {
            pendingEnemyActions.put(action.enemyId, action);
        }
    }
    public EnemyAction getPendingAction(int enemyId) {
        return pendingEnemyActions.get(enemyId);
    }
    public EnemyAction.ActionType getLastAction(int enemyId) {
        return lastEnemyActions.get(enemyId);
    }
    public void setLastAction(int enemyId, EnemyAction.ActionType type) {
        lastEnemyActions.put(enemyId, type);
    }
    public void clearLastActions() {
        lastEnemyActions.clear();
    }
}
