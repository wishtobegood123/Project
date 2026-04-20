package com.example.spacecolonypioneers.model;

import com.example.spacecolonypioneers.model.enums.Phase;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private static GameState instance;

    private Phase currentPhase;
    private List<CrewMember> crewList;
    private List<Mission> missionList;
    private int selectedCrewId;
    private int selectedMissionId;
    private List<Integer> currentSquadIds;
    private GameStatistics statistics;
    private int totalProgress;
    private int totalFragments;
    private int day;
    private int resources;

    private transient CrewMember selectedCrew;
    private transient Mission selectedMission;
    private transient List<CrewMember> currentSquad;
    private transient CombatState combatState;

    private GameState() {
        currentPhase = Phase.SCHEDULING;
        crewList = new ArrayList<CrewMember>();
        missionList = new ArrayList<Mission>();
        currentSquadIds = new ArrayList<Integer>();
        statistics = new GameStatistics();
        totalProgress = 0;
        totalFragments = 0;
        day = 1;
        resources = 200;
        selectedCrewId = -1;
        selectedMissionId = -1;
    }

    public static synchronized GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public static void setInstance(GameState state) {
        instance = state;
        if (instance != null) {
            instance.restoreTransientFields();
        }
    }

    public void restoreTransientFields() {
        if (selectedCrewId >= 0 && crewList != null) {
            for (CrewMember crew : crewList) {
                if (crew != null && crew.getId() == selectedCrewId) {
                    selectedCrew = crew;
                    break;
                }
            }
        }
        if (selectedMissionId >= 0 && missionList != null) {
            for (Mission mission : missionList) {
                if (mission != null && mission.getId() == selectedMissionId) {
                    selectedMission = mission;
                    break;
                }
            }
        }
        currentSquad = new ArrayList<CrewMember>();
        if (currentSquadIds != null && crewList != null) {
            for (Integer id : currentSquadIds) {
                for (CrewMember crew : crewList) {
                    if (crew != null && crew.getId() == id) {
                        currentSquad.add(crew);
                        break;
                    }
                }
            }
        }
    }

    public void updatePersistentFields() {
        selectedCrewId = selectedCrew != null ? selectedCrew.getId() : -1;
        selectedMissionId = selectedMission != null ? selectedMission.getId() : -1;
        currentSquadIds = new ArrayList<Integer>();
        if (currentSquad != null) {
            for (CrewMember crew : currentSquad) {
                if (crew != null) {
                    currentSquadIds.add(crew.getId());
                }
            }
        }
    }

    public Phase getCurrentPhase() { return currentPhase; }
    public void setCurrentPhase(Phase currentPhase) { this.currentPhase = currentPhase; }
    public List<CrewMember> getCrewList() { return crewList; }
    public void setCrewList(List<CrewMember> crewList) { this.crewList = crewList; }
    public List<Mission> getMissionList() { return missionList; }
    public CrewMember getSelectedCrew() { return selectedCrew; }
    public void setSelectedCrew(CrewMember selectedCrew) { this.selectedCrew = selectedCrew; if (selectedCrew != null) selectedCrewId = selectedCrew.getId(); }
    public Mission getSelectedMission() { return selectedMission; }
    public void setSelectedMission(Mission selectedMission) { this.selectedMission = selectedMission; if (selectedMission != null) selectedMissionId = selectedMission.getId(); }
    public List<CrewMember> getCurrentSquad() { if (currentSquad == null) currentSquad = new ArrayList<CrewMember>(); return currentSquad; }
    public GameStatistics getStatistics() { return statistics; }
    public void setStatistics(GameStatistics statistics) { this.statistics = statistics; }
    public int getTotalProgress() { return totalProgress; }
    public void setTotalProgress(int totalProgress) { this.totalProgress = totalProgress; }
    public int getTotalFragments() { return totalFragments; }
    public void setTotalFragments(int totalFragments) { this.totalFragments = totalFragments; }
    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }
    public int getResources() { return resources; }
    public void setResources(int resources) { this.resources = resources; }
    public CombatState getCombatState() { return combatState; }
    public void setCombatState(CombatState combatState) { this.combatState = combatState; }
}
