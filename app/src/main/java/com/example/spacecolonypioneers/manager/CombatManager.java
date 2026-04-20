package com.example.spacecolonypioneers.manager;

import android.os.Handler;
import android.os.Looper;

import com.example.spacecolonypioneers.model.CombatLogEntry;
import com.example.spacecolonypioneers.model.CombatState;
import com.example.spacecolonypioneers.model.CrewMember;
import com.example.spacecolonypioneers.model.Enemy;
import com.example.spacecolonypioneers.model.GameState;
import com.example.spacecolonypioneers.model.Mission;
import com.example.spacecolonypioneers.model.ProfessionConfig;
import com.example.spacecolonypioneers.model.enums.SkillType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CombatManager {
    private static final Random RANDOM = new Random();
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    public interface OnTurnChangeListener {
        void onTurnChanged(CombatState state);
    }
    
    private static OnTurnChangeListener turnChangeListener;
    
    public static void setOnTurnChangeListener(OnTurnChangeListener listener) {
        turnChangeListener = listener;
    }

    public static CombatState startCombat() {
        CombatState state = new CombatState();
        GameState gameState = GameState.getInstance();
        if (gameState == null) return null;
        List<CrewMember> squad = gameState.getCurrentSquad();
        if (squad == null || squad.isEmpty()) return null;

        for (CrewMember crew : squad) {
            if (crew != null && !crew.isInjured()) {
                crew.setShield(0);
                state.getPlayerTeam().add(crew);
            }
        }
        if (state.getPlayerTeam().isEmpty()) return null;
        Mission selectedMission = gameState.getSelectedMission();
        int missionDifficulty = selectedMission != null ? selectedMission.getDifficulty() : 1;
        int enemyCount = 4;
        int day = gameState.getDay();
        int levelBonus = day / 3;
        int difficultyBonus = missionDifficulty * 2;
        
        for (int i = 0; i < enemyCount; i++) {
            int enemyHp = 60 + (i * 20) + (levelBonus * 10) + (difficultyBonus * 5);
            int enemyAttack = 12 + (i * 3) + (levelBonus * 2) + (difficultyBonus * 3);
            int enemyDefense = 3 + (i * 2) + levelBonus + difficultyBonus;
            
            String enemyName;
            switch (i % 4) {
                case 0:
                    enemyName = "Alien Scout";
                    break;
                case 1:
                    enemyName = "Alien Warrior";
                    break;
                case 2:
                    enemyName = "Alien Elite";
                    enemyHp += 20;
                    enemyAttack += 5;
                    break;
                case 3:
                    enemyName = "Alien Behemoth";
                    enemyHp += 40;
                    enemyAttack += 3;
                    enemyDefense += 3;
                    break;
                default:
                    enemyName = "Alien Creature";
            }
            
            state.getEnemyTeam().add(new Enemy(i + 1, enemyName, enemyHp, enemyAttack, enemyDefense));
        }
        
        state.addLog(new CombatLogEntry("Battle started! Encountered " + enemyCount + " enemies!", CombatLogEntry.LogType.NORMAL));
        state.resetActedCrews();
        
        state.setSelectedCrew(state.getPlayerTeam().get(0));
        state.setSelectedEnemy(state.getEnemyTeam().get(0));
        return state;
    }

    public static void performAttack(CombatState state) {
        if (state == null || !state.isPlayerTurn() || state.isCombatEnded()) return;
        CrewMember attacker = state.getSelectedCrew();
        Enemy target = state.getSelectedEnemy();
        if (attacker == null || target == null || target.getHp() <= 0) return;
        if (state.hasCrewActed(attacker.getId())) {
            state.addLog(new CombatLogEntry(attacker.getName() + " has already acted this turn!", CombatLogEntry.LogType.NORMAL));
            return;
        }
        
        ProfessionConfig config = ProfessionConfig.getConfig(attacker.getProfession());
        if (config == null) return;

        int baseDamage = config.getBaseAttack() + (int) (config.getAttackGrowth() * (attacker.getLevel() - 1));
        baseDamage += state.getTeamAttackBonus();
        int attackBonusPercent = state.getTeamAttackBonusPercent();
        if (attackBonusPercent > 0) {
            baseDamage = (int)(baseDamage * (1 + attackBonusPercent / 100.0));
        }
        int damage = Math.max(1, (int)(baseDamage * 2.0) - target.getDefense() + RANDOM.nextInt(10));
        target.setHp(target.getHp() - damage);
        attacker.setEnergy(attacker.getEnergy() + 10);
        state.markCrewAsActed(attacker.getId());
        
        if (GameState.getInstance().getStatistics().getCrewStats(attacker.getId()) != null) {
            GameState.getInstance().getStatistics().getCrewStats(attacker.getId()).addDamageDealt(damage);
        }
        state.addLog(new CombatLogEntry(attacker.getName() + " attacks " + target.getName() + " and deals " + damage + " damage!", CombatLogEntry.LogType.DAMAGE));
        if (target.getHp() <= 0) {
            selectNextAliveEnemy(state);
        }
        checkCombatEnd(state);
        if (!state.isCombatEnded()) {
            nextPlayer(state);
        }
    }

    public static void useSkill(CombatState state) {
        if (state == null || !state.isPlayerTurn() || state.isCombatEnded()) return;
        CrewMember caster = state.getSelectedCrew();
        if (caster == null) return;
        if (state.hasCrewActed(caster.getId())) {
            state.addLog(new CombatLogEntry(caster.getName() + " has already acted this turn!", CombatLogEntry.LogType.NORMAL));
            return;
        }
        
        ProfessionConfig config = ProfessionConfig.getConfig(caster.getProfession());
        if (config == null || config.getSkill() == null) return;
        SkillType skill = config.getSkill();
        if (caster.getEnergy() < skill.getEnergyCost()) {
            state.addLog(new CombatLogEntry("Not enough energy! Need " + skill.getEnergyCost() + " energy.", CombatLogEntry.LogType.NORMAL));
            return;
        }
        caster.setEnergy(caster.getEnergy() - skill.getEnergyCost());
        state.markCrewAsActed(caster.getId());

        switch (skill) {
            case HEAL:
                CrewMember healTarget = getMostInjuredAlly(state);
                if (healTarget != null) {
                    int healAmount = 30 + caster.getLevel() * 5;
                    healTarget.setHp(healTarget.getHp() + healAmount);
                    if (GameState.getInstance().getStatistics().getCrewStats(caster.getId()) != null) {
                        GameState.getInstance().getStatistics().getCrewStats(caster.getId()).addHealingDone(healAmount);
                    }
                    state.addLog(new CombatLogEntry(caster.getName() + " uses First Aid Kit and heals " + healTarget.getName() + " for " + healAmount + " HP!", CombatLogEntry.LogType.HEAL));
                }
                break;
            case REPAIR:
                int attackBonusPercent = 10 + (caster.getLevel() - 1) * 5;
                state.setTeamAttackBonusPercent(attackBonusPercent);
                state.addLog(new CombatLogEntry(caster.getName() + " uses Tactical Command. Team attack increases by " + attackBonusPercent + "%!", CombatLogEntry.LogType.SKILL));
                break;
            case RAGE_SHOT:
                Enemy rageTarget = state.getSelectedEnemy();
                if (rageTarget != null && rageTarget.getHp() > 0) {
                    int baseDamage = config.getBaseAttack() + (int) (config.getAttackGrowth() * (caster.getLevel() - 1));
                    int currentBonusPercent = state.getTeamAttackBonusPercent();
                    if (currentBonusPercent > 0) {
                        baseDamage = (int)(baseDamage * (1 + currentBonusPercent / 100.0));
                    }
                    int damage = Math.max(1, (int)(baseDamage * 2.5) - rageTarget.getDefense() + RANDOM.nextInt(12));
                    rageTarget.setHp(rageTarget.getHp() - damage);
                    if (GameState.getInstance().getStatistics().getCrewStats(caster.getId()) != null) {
                        GameState.getInstance().getStatistics().getCrewStats(caster.getId()).addDamageDealt(damage);
                    }
                    state.addLog(new CombatLogEntry(caster.getName() + " uses Rage Shot on " + rageTarget.getName() + ", dealing " + damage + " damage!", CombatLogEntry.LogType.SKILL));
                    if (rageTarget.getHp() <= 0) {
                        selectNextAliveEnemy(state);
                    }
                }
                break;
            case SCOUT:
                state.setEnemyAttackDebuff(state.getEnemyAttackDebuff() + 5);
                state.addLog(new CombatLogEntry(caster.getName() + " performs Tactical Recon. Enemy attack is reduced by 5!", CombatLogEntry.LogType.SKILL));
                break;
            case INSPIRE:
                state.setTeamAttackBonus(state.getTeamAttackBonus() + 8);
                state.addLog(new CombatLogEntry(caster.getName() + " uses Battlefield Inspire. Team attack increases by 8!", CombatLogEntry.LogType.SKILL));
                break;
        }

        checkCombatEnd(state);
        if (!state.isCombatEnded()) {
            nextPlayer(state);
        }
    }

    private static CrewMember getMostInjuredAlly(CombatState state) {
        CrewMember target = null;
        double lowestHpPercent = 1.0;
        for (CrewMember crew : state.getPlayerTeam()) {
            if (crew != null && !crew.isInjured() && crew.getMaxHp() > 0) {
                double hpPercent = (double) crew.getHp() / crew.getMaxHp();
                if (hpPercent < lowestHpPercent) {
                    lowestHpPercent = hpPercent;
                    target = crew;
                }
            }
        }
        return target;
    }

    private static void nextPlayer(CombatState state) {
        List<CrewMember> playerTeam = state.getPlayerTeam();
        CrewMember currentCrew = state.getSelectedCrew();
        int currentIndex = currentCrew != null ? playerTeam.indexOf(currentCrew) : -1;
        for (int i = 1; i <= playerTeam.size(); i++) {
            int nextIndex = (currentIndex + i) % playerTeam.size();
            CrewMember next = playerTeam.get(nextIndex);
            if (next != null && !next.isInjured()) {
                state.setSelectedCrew(next);
                return;
            }
        }
        endPlayerTurn(state);
    }

    public static void endPlayerTurn(final CombatState state) {
        if (state == null || state.isCombatEnded()) return;
        state.resetActedCrews();
        
        state.setPlayerTurn(false);
        state.addLog(new CombatLogEntry("--- Enemy Turn ---", CombatLogEntry.LogType.NORMAL));
        generateEnemyActions(state);
        
        HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                performEnemyTurn(state);
            }
        }, 1500);
    }
    private static void generateEnemyActions(CombatState state) {
        state.clearPendingActions();
        GameState gameState = GameState.getInstance();
        Mission selectedMission = gameState != null ? gameState.getSelectedMission() : null;
        int missionDifficulty = selectedMission != null ? selectedMission.getDifficulty() : 1;
        
        for (Enemy enemy : state.getEnemyTeam()) {
            if (enemy == null || enemy.getHp() <= 0) continue;
            List<CrewMember> aliveCrews = new ArrayList<CrewMember>();
            for (CrewMember crew : state.getPlayerTeam()) {
                if (crew != null && !crew.isInjured()) {
                    aliveCrews.add(crew);
                }
            }
            
            if (aliveCrews.isEmpty()) break;
            CrewMember target = aliveCrews.get(RANDOM.nextInt(aliveCrews.size()));
            int attackChance, buffChance, debuffChance;
            String enemyName = enemy.getName();
            
            if (enemyName.contains("Scout")) {
                attackChance = 50;
                buffChance = 10;
                debuffChance = 40;
            } else if (enemyName.contains("Elite") || enemyName.contains("Behemoth")) {
                attackChance = 50;
                buffChance = 35;
                debuffChance = 15;
            } else {
                attackChance = 60;
                buffChance = 20;
                debuffChance = 20;
            }
            
            int actionRoll = RANDOM.nextInt(100);
            CombatState.EnemyAction enemyAction;
            CombatState.EnemyAction.ActionType lastAction = state.getLastAction(enemy.getId());
            
            if (actionRoll < attackChance) {
                ProfessionConfig config = ProfessionConfig.getConfig(target.getProfession());
                int targetDefense = config != null ? config.getBaseDefense() : 0;
                int attackPower = Math.max(1, enemy.getAttack() - state.getEnemyAttackDebuff());
                int damage = Math.max(1, (int)(attackPower * 1.2) - targetDefense + RANDOM.nextInt(3));
                
                enemyAction = new CombatState.EnemyAction(
                    enemy.getId(),
                    CombatState.EnemyAction.ActionType.ATTACK,
                    "Attack " + target.getName(),
                    damage,
                    target.getId()
                );
            } else if (actionRoll < attackChance + buffChance) {
                if (lastAction == CombatState.EnemyAction.ActionType.BUFF_ATTACK) {
                    ProfessionConfig config = ProfessionConfig.getConfig(target.getProfession());
                    int targetDefense = config != null ? config.getBaseDefense() : 0;
                    int attackPower = Math.max(1, enemy.getAttack() - state.getEnemyAttackDebuff());
                    int damage = Math.max(1, (int)(attackPower * 1.2) - targetDefense + RANDOM.nextInt(3));
                    
                    enemyAction = new CombatState.EnemyAction(
                        enemy.getId(),
                        CombatState.EnemyAction.ActionType.ATTACK,
                        "Attack " + target.getName(),
                        damage,
                        target.getId()
                    );
                } else {
                    int buffPercent = 5 + (missionDifficulty - 1) * 2;
                    enemyAction = new CombatState.EnemyAction(
                        enemy.getId(),
                        CombatState.EnemyAction.ActionType.BUFF_ATTACK,
                        "Empower Attack +" + buffPercent + "%",
                        buffPercent,
                        target.getId()
                    );
                }
            } else {
                int debuffPercent = 5 + (missionDifficulty - 1) * 2;
                enemyAction = new CombatState.EnemyAction(
                    enemy.getId(),
                    CombatState.EnemyAction.ActionType.DEBUFF_PLAYER,
                    "Debuff Team -" + debuffPercent + "%",
                    debuffPercent,
                    target.getId()
                );
            }
            
            state.setPendingAction(enemyAction);
            state.setLastAction(enemy.getId(), enemyAction.type);
        }
    }

    private static void performEnemyTurn(CombatState state) {
        
        for (Enemy enemy : state.getEnemyTeam()) {
            if (enemy == null || enemy.getHp() <= 0) continue;
            
            CombatState.EnemyAction action = state.getPendingAction(enemy.getId());
            if (action == null) continue;
            List<CrewMember> aliveCrews = new ArrayList<CrewMember>();
            for (CrewMember crew : state.getPlayerTeam()) {
                if (crew != null && !crew.isInjured()) {
                    aliveCrews.add(crew);
                }
            }
            
            if (aliveCrews.isEmpty()) break;
            CrewMember target = aliveCrews.get(RANDOM.nextInt(aliveCrews.size()));
            if (action.type == CombatState.EnemyAction.ActionType.ATTACK) {
                ProfessionConfig config = ProfessionConfig.getConfig(target.getProfession());
                int targetDefense = config != null ? config.getBaseDefense() : 0;
                int attackPower = Math.max(1, enemy.getAttack() - state.getEnemyAttackDebuff());
                int damage = Math.max(1, (int)(attackPower * 1.2) - targetDefense + RANDOM.nextInt(3));
                
                target.takeDamage(damage);
                if (GameState.getInstance().getStatistics().getCrewStats(target.getId()) != null) {
                    GameState.getInstance().getStatistics().getCrewStats(target.getId()).addDamageTaken(damage);
                }
                state.addLog(new CombatLogEntry(enemy.getName() + " attacks " + target.getName() + " and deals " + damage + " damage!", CombatLogEntry.LogType.DAMAGE));
                if (target.getHp() <= 0) {
                    target.setHp(1);
                    target.setInjured(true);
                    state.addLog(new CombatLogEntry(target.getName() + " is down!", CombatLogEntry.LogType.NORMAL));
                }
            } else if (action.type == CombatState.EnemyAction.ActionType.BUFF_ATTACK) {
                state.addLog(new CombatLogEntry(enemy.getName() + " empowers attack, increasing attack by " + action.value + "%!", CombatLogEntry.LogType.SKILL));
            } else if (action.type == CombatState.EnemyAction.ActionType.DEBUFF_PLAYER) {
                state.setEnemyAttackDebuff(state.getEnemyAttackDebuff() + action.value);
                state.addLog(new CombatLogEntry(enemy.getName() + " casts weakening aura, reducing team attack by " + action.value + "%!", CombatLogEntry.LogType.SKILL));
            }
        }
        
        checkCombatEnd(state);
        if (!state.isCombatEnded()) {
            state.setCurrentTurn(state.getCurrentTurn() + 1);
            state.setPlayerTurn(true);
            state.addLog(new CombatLogEntry("--- Turn " + state.getCurrentTurn() + " ---", CombatLogEntry.LogType.NORMAL));
            for (CrewMember crew : state.getPlayerTeam()) {
                if (crew != null && !crew.isInjured()) {
                    state.setSelectedCrew(crew);
                    break;
                }
            }
            selectNextAliveEnemy(state);
            if (turnChangeListener != null) {
                final CombatState finalState = state;
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        if (turnChangeListener != null) {
                            turnChangeListener.onTurnChanged(finalState);
                        }
                    }
                });
            }
        }
    }

    private static void selectNextAliveEnemy(CombatState state) {
        for (Enemy enemy : state.getEnemyTeam()) {
            if (enemy != null && enemy.getHp() > 0) {
                state.setSelectedEnemy(enemy);
                return;
            }
        }
    }

    private static void checkCombatEnd(CombatState state) {
        boolean allEnemiesDead = true;
        for (Enemy enemy : state.getEnemyTeam()) {
            if (enemy != null && enemy.getHp() > 0) {
                allEnemiesDead = false;
                break;
            }
        }
        if (allEnemiesDead) {
            state.setCombatEnded(true);
            state.setPlayerWon(true);
            state.addLog(new CombatLogEntry("Victory!", CombatLogEntry.LogType.VICTORY));
            for (CrewMember crew : state.getPlayerTeam()) {
                if (crew != null) {
                    crew.markAsParticipatedInCombat();
                    
                    if (!crew.isInjured()) {
                        crew.setXp(crew.getXp() + 50);
                        CrewManager.checkLevelUp(crew);
                    }
                    crew.setShield(0);
                }
            }
            return;
        }

        boolean allPlayersInjured = true;
        for (CrewMember crew : state.getPlayerTeam()) {
            if (crew != null && !crew.isInjured()) {
                allPlayersInjured = false;
                break;
            }
        }
        if (allPlayersInjured) {
            state.setCombatEnded(true);
            state.setPlayerWon(false);
            state.addLog(new CombatLogEntry("Defeat...", CombatLogEntry.LogType.DEFEAT));
            for (CrewMember crew : state.getPlayerTeam()) {
                if (crew != null) {
                    crew.markAsParticipatedInCombat();
                    crew.setShield(0);
                }
            }
        }
    }
}
