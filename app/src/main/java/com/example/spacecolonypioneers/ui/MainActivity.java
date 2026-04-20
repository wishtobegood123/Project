package com.example.spacecolonypioneers.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ViewFlipper;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolonypioneers.R;
import com.example.spacecolonypioneers.manager.CombatManager;
import com.example.spacecolonypioneers.manager.CrewManager;
import com.example.spacecolonypioneers.manager.MissionManager;
import com.example.spacecolonypioneers.manager.ProgressionManager;
import com.example.spacecolonypioneers.manager.StorageManager;
import com.example.spacecolonypioneers.model.CombatState;
import com.example.spacecolonypioneers.model.CrewMember;
import com.example.spacecolonypioneers.model.GameState;
import com.example.spacecolonypioneers.model.Mission;
import com.example.spacecolonypioneers.model.ProfessionConfig;
import com.example.spacecolonypioneers.model.SquadBonus;
import com.example.spacecolonypioneers.model.enums.Assignment;
import com.example.spacecolonypioneers.model.enums.Phase;
import com.example.spacecolonypioneers.model.enums.Profession;
import com.example.spacecolonypioneers.model.enums.SkillType;
import com.example.spacecolonypioneers.ui.adapter.CombatLogAdapter;
import com.example.spacecolonypioneers.ui.adapter.CrewAdapter;
import com.example.spacecolonypioneers.ui.adapter.MissionAdapter;
import com.example.spacecolonypioneers.ui.adapter.SquadAdapter;
import com.example.spacecolonypioneers.ui.view.CombatView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GameState gameState;
    private CrewAdapter quartersAdapter, simulatorAdapter, missionControlAdapter, unassignedAdapter;
    private MissionAdapter missionAdapter;
    private SquadAdapter availableCrewAdapter, squadAdapter;
    private CombatLogAdapter combatLogAdapter;
    private ViewFlipper viewFlipper;
    private CombatView combatView;
    private TextView tvPhase, tvResources, tvDetailName, tvDetailProfession, tvDetailHpText, tvDetailEnergyText, tvDetailShieldText, tvDetailDesc, tvSquadBonus, tvMissionWarning;
    private ProgressBar pbDetailHp, pbDetailEnergy, pbDetailShield;
    private LinearLayout llCrewDetail, llCombatControls;
    private Button btnStartMission, btnAttack, btnSkill, btnEndTurn, btnExitCombat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!TutorialActivity.hasSeenTutorial(this)) {
            showTutorial();
            return;
        }
        
        gameState = GameState.getInstance();
        initViews();
        initAdapters();
        initCrewData();
        if (gameState.getStatistics() != null) {
            gameState.getStatistics().initializeCrewStats(gameState.getCrewList());
        }
        if (gameState.getMissionList() == null || gameState.getMissionList().isEmpty()) {
            MissionManager.generateDailyMissions();
        }
        CombatManager.setOnTurnChangeListener(new CombatManager.OnTurnChangeListener() {
            @Override
            public void onTurnChanged(CombatState state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateCombatUI();
                    }
                });
            }
        });
        
        updateUI();
    }

    private void initViews() {
        viewFlipper = findViewById(R.id.viewFlipper);
        combatView = findViewById(R.id.combatView);
        tvPhase = findViewById(R.id.tvPhase);
        tvResources = findViewById(R.id.tvResources);
        llCrewDetail = findViewById(R.id.llCrewDetail);
        llCombatControls = findViewById(R.id.llCombatControls);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailProfession = findViewById(R.id.tvDetailProfession);
        tvDetailHpText = findViewById(R.id.tvDetailHpText);
        tvDetailEnergyText = findViewById(R.id.tvDetailEnergyText);
        tvDetailShieldText = findViewById(R.id.tvDetailShieldText);
        tvDetailDesc = findViewById(R.id.tvDetailDesc);
        tvSquadBonus = findViewById(R.id.tvSquadBonus);
        tvMissionWarning = findViewById(R.id.tvMissionWarning);
        pbDetailHp = findViewById(R.id.pbDetailHp);
        pbDetailEnergy = findViewById(R.id.pbDetailEnergy);
        pbDetailShield = findViewById(R.id.pbDetailShield);
        btnStartMission = findViewById(R.id.btnStartMission);
        btnAttack = findViewById(R.id.btnAttack);
        btnSkill = findViewById(R.id.btnSkill);
        btnEndTurn = findViewById(R.id.btnEndTurn);
        btnExitCombat = findViewById(R.id.btnExitCombat);

        Button btnSave = findViewById(R.id.btnSave);
        Button btnLoad = findViewById(R.id.btnLoad);
        Button btnNextPhase = findViewById(R.id.btnNextPhase);
        Button btnStatistics = findViewById(R.id.btnStatistics);
        Button btnTutorial = findViewById(R.id.btnTutorial);
        Button btnAssignQuarters = findViewById(R.id.btnAssignQuarters);
        Button btnAssignSimulator = findViewById(R.id.btnAssignSimulator);
        Button btnAssignMission = findViewById(R.id.btnAssignMission);
        Button btnRecruitCrew = findViewById(R.id.btnRecruitCrew);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageManager.saveGame(MainActivity.this, gameState);
            }
        });
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameState loaded = StorageManager.loadGame(MainActivity.this);
                if (loaded != null) {
                    GameState.setInstance(loaded);
                    gameState = GameState.getInstance();
                    if (gameState.getStatistics() != null && gameState.getCrewList() != null) {
                        gameState.getStatistics().initializeCrewStats(gameState.getCrewList());
                    }
                    updateUI();
                }
            }
        });
        btnNextPhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPhase();
            }
        });
        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
            }
        });
        btnTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTutorial();
            }
        });
        btnTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTutorial();
            }
        });
        
        btnAssignQuarters.setOnClickListener(assignListener(Assignment.QUARTERS));
        btnAssignSimulator.setOnClickListener(assignListener(Assignment.SIMULATOR));
        btnAssignMission.setOnClickListener(assignListener(Assignment.MISSION_CONTROL));
        btnRecruitCrew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recruitNewCrew();
            }
        });
        btnStartMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMission();
            }
        });
        btnAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CombatManager.performAttack(gameState.getCombatState());
                updateCombatUI();
            }
        });
        btnSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CombatManager.useSkill(gameState.getCombatState());
                updateCombatUI();
            }
        });
        btnEndTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CombatManager.endPlayerTurn(gameState.getCombatState());
                updateCombatUI();
            }
        });
        btnExitCombat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitCombat();
            }
        });
    }

    private View.OnClickListener assignListener(final Assignment assignment) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameState.getSelectedCrew() != null) {
                    CrewMember selected = gameState.getSelectedCrew();
                    if (selected.getAssignment() != Assignment.UNASSIGNED) {
                        android.widget.Toast.makeText(MainActivity.this, 
                            selected.getName() + " is already assigned to " + getAssignmentName(selected.getAssignment()) + ", cannot be assigned again!", 
                            android.widget.Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selected.setAssignment(assignment);
                    String locationName = getAssignmentName(assignment);
                    android.widget.Toast.makeText(MainActivity.this, 
                        selected.getName() + " assigned to " + locationName, 
                        android.widget.Toast.LENGTH_LONG).show();
                    selectNextUnassignedCrew();
                    
                    updateUI();
                } else {
                    android.widget.Toast.makeText(MainActivity.this, 
                        "Please select a crew member from the standby list first", 
                        android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
    private String getAssignmentName(Assignment assignment) {
        switch (assignment) {
            case QUARTERS: return "Quarters";
            case SIMULATOR: return "Training Simulator";
            case MISSION_CONTROL: return "Mission Control";
            default: return "Standby";
        }
    }

    private void initAdapters() {
        RecyclerView rvQuarters = findViewById(R.id.rvQuarters);
        RecyclerView rvSimulator = findViewById(R.id.rvSimulator);
        RecyclerView rvMissionControl = findViewById(R.id.rvMissionControl);
        RecyclerView rvUnassigned = findViewById(R.id.rvUnassigned);
        RecyclerView rvMissions = findViewById(R.id.rvMissions);
        RecyclerView rvAvailableCrew = findViewById(R.id.rvAvailableCrew);
        RecyclerView rvSquad = findViewById(R.id.rvSquad);
        RecyclerView rvCombatLog = findViewById(R.id.rvCombatLog);

        rvQuarters.setLayoutManager(new LinearLayoutManager(this));
        rvSimulator.setLayoutManager(new LinearLayoutManager(this));
        rvMissionControl.setLayoutManager(new LinearLayoutManager(this));
        rvUnassigned.setLayoutManager(new LinearLayoutManager(this));
        rvMissions.setLayoutManager(new LinearLayoutManager(this));
        rvAvailableCrew.setLayoutManager(new LinearLayoutManager(this));
        rvSquad.setLayoutManager(new LinearLayoutManager(this));
        rvCombatLog.setLayoutManager(new LinearLayoutManager(this));

        CrewAdapter.OnCrewClickListener crewListener = new CrewAdapter.OnCrewClickListener() {
            @Override
            public void onCrewClick(CrewMember crew) {
                gameState.setSelectedCrew(crew);
                updateUI();
            }
        };
        MissionAdapter.OnMissionClickListener missionListener = new MissionAdapter.OnMissionClickListener() {
            @Override
            public void onMissionClick(Mission mission) {
                for (Mission item : gameState.getMissionList()) {
                    if (item != null) item.setSelected(false);
                }
                mission.setSelected(true);
                gameState.setSelectedMission(mission);
                updateUI();
            }
        };
        SquadAdapter.OnSquadMemberClickListener addListener = new SquadAdapter.OnSquadMemberClickListener() {
            @Override
            public void onSquadMemberClick(CrewMember crew) {
                if (!gameState.getCurrentSquad().contains(crew) && gameState.getCurrentSquad().size() < 5) {
                    gameState.getCurrentSquad().add(crew);
                    updateUI();
                }
            }
        };
        SquadAdapter.OnSquadMemberClickListener removeListener = new SquadAdapter.OnSquadMemberClickListener() {
            @Override
            public void onSquadMemberClick(CrewMember crew) {
                gameState.getCurrentSquad().remove(crew);
                updateUI();
            }
        };

        quartersAdapter = new CrewAdapter(new ArrayList<CrewMember>(), crewListener);
        simulatorAdapter = new CrewAdapter(new ArrayList<CrewMember>(), crewListener);
        missionControlAdapter = new CrewAdapter(new ArrayList<CrewMember>(), crewListener);
        unassignedAdapter = new CrewAdapter(new ArrayList<CrewMember>(), crewListener);
        missionAdapter = new MissionAdapter(new ArrayList<Mission>(), missionListener);
        availableCrewAdapter = new SquadAdapter(new ArrayList<CrewMember>(), addListener);
        squadAdapter = new SquadAdapter(new ArrayList<CrewMember>(), removeListener);
        combatLogAdapter = new CombatLogAdapter(new ArrayList<com.example.spacecolonypioneers.model.CombatLogEntry>());

        rvQuarters.setAdapter(quartersAdapter);
        rvSimulator.setAdapter(simulatorAdapter);
        rvMissionControl.setAdapter(missionControlAdapter);
        rvUnassigned.setAdapter(unassignedAdapter);
        rvMissions.setAdapter(missionAdapter);
        rvAvailableCrew.setAdapter(availableCrewAdapter);
        rvSquad.setAdapter(squadAdapter);
        rvCombatLog.setAdapter(combatLogAdapter);
    }

    private void initCrewData() {
        if (gameState.getCrewList() == null || gameState.getCrewList().isEmpty()) {
            List<CrewMember> crewList = new ArrayList<CrewMember>();
            crewList.add(new CrewMember(1, "Anna", Profession.MEDIC));
            crewList.add(new CrewMember(2, "Bob", Profession.ENGINEER));
            crewList.add(new CrewMember(3, "Chris", Profession.SOLDIER));
            crewList.add(new CrewMember(4, "Diana", Profession.SCOUT));
            crewList.add(new CrewMember(5, "Edward", Profession.COMMANDER));
            gameState.setCrewList(crewList);
        }
    }

    private void nextPhase() {
        Phase current = gameState.getCurrentPhase();
        if (current == null) current = Phase.SCHEDULING;
        switch (current) {
            case SCHEDULING:
                gameState.setCurrentPhase(Phase.PROGRESSION);
                ProgressionManager.processProgression();
                MissionManager.generateDailyMissions();
                gameState.setCurrentPhase(Phase.MISSION_SELECTION);
                gameState.getCurrentSquad().clear();
                gameState.setSelectedMission(null);
                viewFlipper.setDisplayedChild(1);
                break;
            case PROGRESSION:
                gameState.setCurrentPhase(Phase.MISSION_SELECTION);
                gameState.setSelectedMission(null);
                viewFlipper.setDisplayedChild(1);
                break;
            case MISSION_SELECTION:
                if (gameState.getSelectedMission() == null) {
                    new android.app.AlertDialog.Builder(this)
                        .setTitle("⚠️ No Mission Selected")
                        .setMessage("Please select a mission before going to the next phase!")
                        .setPositiveButton("OK", null)
                        .show();
                    return;
                }
                if (gameState.getSelectedMission() != null && !gameState.getSelectedMission().isCompleted()) {
                    new android.app.AlertDialog.Builder(this)
                        .setTitle("⚠️ Skip Mission?")
                        .setMessage("A mission is selected but not started. Skip this mission phase?")
                        .setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                proceedToScheduling();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                } else {
                    proceedToScheduling();
                }
                break;
            default:
                break;
        }
        updateUI();
    }
    private void proceedToScheduling() {
        gameState.setCurrentPhase(Phase.SCHEDULING);
        for (CrewMember crew : gameState.getCrewList()) {
            if (crew != null) {
                crew.setAssignment(Assignment.UNASSIGNED);
            }
        }
        viewFlipper.setDisplayedChild(0);
        updateUI();
    }

    private void startMission() {
        if (gameState.getSelectedMission() == null || gameState.getCurrentSquad().isEmpty()) return;
        for (CrewMember crew : gameState.getCurrentSquad()) {
            if (!crew.isAvailableForCombat()) {
                android.widget.Toast.makeText(this, crew.getName() + " is still on cooldown and cannot join combat!", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        if (gameState.getStatistics() != null) gameState.getStatistics().incrementTotalMissions();
        CombatState combatState = CombatManager.startCombat();
        if (combatState != null) {
            gameState.setCombatState(combatState);
            gameState.setCurrentPhase(Phase.COMBAT);
            viewFlipper.setDisplayedChild(2);
            updateCombatUI();
        }
    }

    private void exitCombat() {
        CombatState combatState = gameState.getCombatState();
        if (combatState != null && combatState.isPlayerWon() && gameState.getSelectedMission() != null) {
            SquadBonus bonus = SquadBonus.calculateSquadBonus(gameState.getCurrentSquad());
            MissionManager.completeMission(gameState.getSelectedMission(), bonus);
        }
        gameState.getCurrentSquad().clear();
        gameState.setCombatState(null);
        gameState.setCurrentPhase(Phase.MISSION_SELECTION);
        viewFlipper.setDisplayedChild(1);
        updateUI();
    }

    private void updateUI() {
        Phase phase = gameState.getCurrentPhase() != null ? gameState.getCurrentPhase() : Phase.SCHEDULING;
        tvPhase.setText("Current Phase: " + phase.getDisplayName() + " | Day " + gameState.getDay());
        tvResources.setText("Progress: " + gameState.getTotalProgress() + " | Fragments: " + gameState.getTotalFragments() + " | Resources: " + gameState.getResources());
        if (phase == Phase.COMBAT) {
            viewFlipper.setDisplayedChild(2);
            updateCombatUI();
        } else if (phase == Phase.MISSION_SELECTION) {
            viewFlipper.setDisplayedChild(1);
            updateMissionUI();
        } else {
            viewFlipper.setDisplayedChild(0);
            updateCrewLists();
            if (phase == Phase.SCHEDULING && gameState.getSelectedCrew() == null) {
                List<CrewMember> unassigned = new ArrayList<CrewMember>();
                for (CrewMember crew : gameState.getCrewList()) {
                    if (crew != null && crew.getAssignment() == Assignment.UNASSIGNED) {
                        unassigned.add(crew);
                    }
                }
                if (!unassigned.isEmpty()) {
                    gameState.setSelectedCrew(unassigned.get(0));
                }
            }
            
            updateCrewDetail();
        }
    }

    private void updateCrewLists() {
        List<CrewMember> quarters = new ArrayList<CrewMember>();
        List<CrewMember> simulator = new ArrayList<CrewMember>();
        List<CrewMember> missionControl = new ArrayList<CrewMember>();
        List<CrewMember> unassigned = new ArrayList<CrewMember>();
        for (CrewMember crew : gameState.getCrewList()) {
            if (crew.getAssignment() == Assignment.QUARTERS) {
                quarters.add(crew);
            } else if (crew.getAssignment() == Assignment.SIMULATOR) {
                simulator.add(crew);
            } else if (crew.getAssignment() == Assignment.MISSION_CONTROL) {
                missionControl.add(crew);
            } else {
                unassigned.add(crew);
            }
        }
        quartersAdapter.updateList(quarters);
        simulatorAdapter.updateList(simulator);
        missionControlAdapter.updateList(missionControl);
        unassignedAdapter.updateList(unassigned);
    }

    private void updateMissionUI() {
        if (gameState.getMissionList() != null) {
            missionAdapter.updateList(gameState.getMissionList());
        }
        List<CrewMember> available = new ArrayList<CrewMember>();
        if (gameState.getCrewList() != null) {
            for (CrewMember crew : gameState.getCrewList()) {
                if (crew != null && 
                    crew.getAssignment() == Assignment.MISSION_CONTROL &&
                    !crew.isInjured() && 
                    crew.isAvailableForCombat() && 
                    !gameState.getCurrentSquad().contains(crew)) {
                    available.add(crew);
                }
            }
        }
        availableCrewAdapter.updateList(available);
        squadAdapter.updateList(new ArrayList<CrewMember>(gameState.getCurrentSquad()));
        SquadBonus bonus = SquadBonus.calculateSquadBonus(gameState.getCurrentSquad());
        if (tvSquadBonus != null && bonus != null) {
            tvSquadBonus.setText("Squad Bonus: " + bonus.getName() + "\n" + bonus.getDescription());
        }
        updateMissionWarning();
        
        if (btnStartMission != null) {
            btnStartMission.setEnabled(gameState.getSelectedMission() != null && !gameState.getCurrentSquad().isEmpty());
        }
    }

    private void updateCrewDetail() {
        CrewMember selected = gameState.getSelectedCrew();
        if (selected == null) {
            llCrewDetail.setVisibility(View.GONE);
            return;
        }
        llCrewDetail.setVisibility(View.VISIBLE);
        ProfessionConfig config = ProfessionConfig.getConfig(selected.getProfession());
        tvDetailName.setText(selected.getName() + (selected.isInjured() ? " (Injured)" : ""));
        String cooldownText = "";
        int daysUntilReady = selected.getDaysUntilCombatReady();
        if (daysUntilReady > 0) {
            cooldownText = " | ⏰ Rest for " + daysUntilReady + " more day(s) before combat";
        } else if (!selected.isInjured()) {
            cooldownText = " | ✅ Ready for combat";
        }
        
        tvDetailProfession.setText(selected.getProfession().getDisplayName() + " | Lv " + selected.getLevel() + " | XP: " + selected.getXp() + "/" + (selected.getLevel() * 50) + cooldownText);
        SkillType skill = config != null ? config.getSkill() : null;
        String skillText = "";
        if (skill != null) {
            skillText = "Skill: " + skill.getDisplayName() + " (Cost: " + skill.getEnergyCost() + " energy)\n";
            skillText += skill.getDescription() + "\n";
            if (skill == SkillType.REPAIR) {
                int currentBonus = 10 + (selected.getLevel() - 1) * 5;
                int nextBonus = 10 + selected.getLevel() * 5;
                skillText += "Current Effect: Attack +" + currentBonus + "%";
                if (selected.getLevel() < 20) {
                    skillText += " | Next Lv: +" + nextBonus + "%";
                }
            } else if (skill == SkillType.RAGE_SHOT) {
                ProfessionConfig cfg = ProfessionConfig.getConfig(selected.getProfession());
                if (cfg != null) {
                    int currentBaseDamage = cfg.getBaseAttack() + (int)(cfg.getAttackGrowth() * (selected.getLevel() - 1));
                    int nextBaseDamage = cfg.getBaseAttack() + (int)(cfg.getAttackGrowth() * selected.getLevel());
                    int currentDamage = (int)(currentBaseDamage * 2.5);
                    int nextDamage = (int)(nextBaseDamage * 2.5);
                    skillText += "Current Damage: " + currentDamage;
                    if (selected.getLevel() < 20) {
                        skillText += " | Next Lv: " + nextDamage;
                    }
                }
            } else if (skill == SkillType.HEAL) {
                int currentHeal = 30 + selected.getLevel() * 5;
                int nextHeal = 30 + (selected.getLevel() + 1) * 5;
                skillText += "Current Heal: " + currentHeal;
                if (selected.getLevel() < 20) {
                    skillText += " | Next Lv: " + nextHeal;
                }
            }
        }
        tvDetailDesc.setText((config != null ? config.getDescription() : "") + "\n\n" + skillText);
        pbDetailHp.setMax(selected.getMaxHp());
        pbDetailHp.setProgress(selected.getHp());
        tvDetailHpText.setText(selected.getHp() + "/" + selected.getMaxHp());
        pbDetailShield.setMax(selected.getMaxShield());
        pbDetailShield.setProgress(selected.getShield());
        tvDetailShieldText.setText(selected.getShield() + "/" + selected.getMaxShield());
        pbDetailEnergy.setMax(selected.getMaxEnergy());
        pbDetailEnergy.setProgress(selected.getEnergy());
        tvDetailEnergyText.setText(selected.getEnergy() + "/" + selected.getMaxEnergy());
    }

    private void updateCombatUI() {
        CombatState state = gameState.getCombatState();
        if (state == null) return;
        combatView.setCombatState(state);
        combatLogAdapter.updateList(state.getLog());
        RecyclerView rvCombatLog = findViewById(R.id.rvCombatLog);
        if (rvCombatLog != null && rvCombatLog.getAdapter() != null) {
            int itemCount = rvCombatLog.getAdapter().getItemCount();
            if (itemCount > 0) {
                rvCombatLog.smoothScrollToPosition(itemCount - 1);
            }
        }
        
        boolean playerTurn = state.isPlayerTurn() && !state.isCombatEnded();
        llCombatControls.setVisibility(playerTurn ? View.VISIBLE : View.GONE);
        btnExitCombat.setVisibility(state.isCombatEnded() ? View.VISIBLE : View.GONE);
        btnAttack.setEnabled(!state.isCombatEnded());
        btnSkill.setEnabled(!state.isCombatEnded());
        btnEndTurn.setEnabled(!state.isCombatEnded());
        CrewMember selected = state.getSelectedCrew();
        if (selected != null) {
            ProfessionConfig config = ProfessionConfig.getConfig(selected.getProfession());
            if (config != null && config.getSkill() != null) {
                btnSkill.setText("Skill: " + config.getSkill().getDisplayName() + " (" + config.getSkill().getEnergyCost() + ")");
            }
        }
        btnExitCombat.setText(state.isPlayerWon() ? "Claim Reward" : "Retreat");
    }
    private void recruitNewCrew() {
        if (gameState == null) return;
        int recruitCost = 100;
        if (gameState.getResources() < recruitCost) {
            android.widget.Toast.makeText(this, "Not enough resources! Need " + recruitCost + " resources", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        Profession[] professions = Profession.values();
        Profession randomProfession = professions[(int)(Math.random() * professions.length)];
        String[] firstNames = {"Alex", "Sam", "Jordan", "Taylor", "Casey", "Riley", "Morgan", "Jamie", "Cameron", "Avery", "Logan", "Parker", "Drew", "Reese", "Blake", "Hayden", "Quinn", "Rowan", "Kai", "Eden"};
        String[] lastNames = {"Stone", "Reed", "Brooks", "Hayes", "Shaw", "Lane", "Cole", "Grant", "Mills", "Wells", "Perry", "Cross", "Frost", "Blair", "Price", "Flynn", "Bennett", "Grey", "North", "West"};
        String newName = firstNames[(int)(Math.random() * firstNames.length)] + lastNames[(int)(Math.random() * lastNames.length)];
        int newId = gameState.getCrewList().size() + 1;
        CrewMember newCrew = new CrewMember(newId, newName, randomProfession);
        newCrew.setAssignment(Assignment.UNASSIGNED);
        gameState.getCrewList().add(newCrew);
        gameState.setResources(gameState.getResources() - recruitCost);
        android.widget.Toast.makeText(this, "Successfully recruited " + newName + " (" + randomProfession.getDisplayName() + ")!", android.widget.Toast.LENGTH_LONG).show();
        
        updateUI();
    }
    private void updateMissionWarning() {
        Mission selectedMission = gameState.getSelectedMission();
        List<CrewMember> squad = gameState.getCurrentSquad();
        
        if (selectedMission == null || squad == null || squad.isEmpty()) {
            tvMissionWarning.setVisibility(View.GONE);
            return;
        }
        
        int missionDifficulty = selectedMission.getDifficulty();
        int squadSize = squad.size();
        int totalLevel = 0;
        for (CrewMember crew : squad) {
            if (crew != null) {
                totalLevel += crew.getLevel();
            }
        }
        int avgLevel = squadSize > 0 ? totalLevel / squadSize : 0;
        List<String> warnings = new ArrayList<String>();
        if (squadSize < 3 && missionDifficulty >= 3) {
            warnings.add("⚠️ High-difficulty missions recommend at least 3 crew members");
        }
        if (avgLevel < missionDifficulty) {
            warnings.add("⚠️ Average squad level (" + avgLevel + ") is below mission difficulty (" + missionDifficulty + ")");
        }
        if (avgLevel < missionDifficulty - 1) {
            warnings.add("❗ Level gap is too high. Consider leveling up first.");
        }
        if (warnings.isEmpty()) {
            tvMissionWarning.setVisibility(View.GONE);
        } else {
            tvMissionWarning.setVisibility(View.VISIBLE);
            StringBuilder warningText = new StringBuilder("⚠️ Warning:\n");
            for (String warning : warnings) {
                warningText.append(warning).append("\n");
            }
            tvMissionWarning.setText(warningText.toString());
        }
    }
    private void selectNextUnassignedCrew() {
        List<CrewMember> unassigned = new ArrayList<CrewMember>();
        for (CrewMember crew : gameState.getCrewList()) {
            if (crew != null && crew.getAssignment() == Assignment.UNASSIGNED) {
                unassigned.add(crew);
            }
        }
        
        if (!unassigned.isEmpty()) {
            gameState.setSelectedCrew(unassigned.get(0));
        } else {
            gameState.setSelectedCrew(null);
        }
    }
    private void showTutorial() {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }
}
