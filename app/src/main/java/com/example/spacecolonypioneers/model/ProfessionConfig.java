package com.example.spacecolonypioneers.model;

import com.example.spacecolonypioneers.model.enums.Profession;
import com.example.spacecolonypioneers.model.enums.SkillType;

public class ProfessionConfig {
    private Profession profession;
    private String description;
    private int baseMaxHp;
    private int baseMaxEnergy;
    private int baseAttack;
    private int baseDefense;
    private SkillType skill;
    private double hpGrowth;
    private double attackGrowth;

    public ProfessionConfig() {
    }

    public ProfessionConfig(Profession profession, String description, int baseMaxHp, int baseMaxEnergy,
                            int baseAttack, int baseDefense, SkillType skill, double hpGrowth,
                            double attackGrowth) {
        this.profession = profession;
        this.description = description;
        this.baseMaxHp = baseMaxHp;
        this.baseMaxEnergy = baseMaxEnergy;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.skill = skill;
        this.hpGrowth = hpGrowth;
        this.attackGrowth = attackGrowth;
    }

    public static ProfessionConfig getConfig(Profession profession) {
        if (profession == null) return null;
        switch (profession) {
            case MEDIC:
                return new ProfessionConfig(profession, "Medic: excels at healing teammates. When assigned to quarters, boosts recovery for all crew there.", 80, 120, 10, 5, SkillType.HEAL, 5.0, 1.5);
            case ENGINEER:
                return new ProfessionConfig(profession, "Engineer: generates energy shields to protect teammates and offers strong defense.", 90, 100, 12, 10, SkillType.REPAIR, 6.0, 2.0);
            case SOLDIER:
                return new ProfessionConfig(profession, "Soldier: frontline combat specialist with the highest attack power.", 120, 80, 20, 8, SkillType.RAGE_SHOT, 8.0, 3.0);
            case SCOUT:
                return new ProfessionConfig(profession, "Scout: agile unit that can recon enemies and weaken them.", 70, 100, 14, 6, SkillType.SCOUT, 4.0, 2.5);
            case COMMANDER:
                return new ProfessionConfig(profession, "Commander: team core that boosts morale and squad combat power.", 100, 110, 15, 9, SkillType.INSPIRE, 7.0, 2.0);
            default:
                return null;
        }
    }

    public Profession getProfession() { return profession; }
    public String getDescription() { return description; }
    public int getBaseMaxHp() { return baseMaxHp; }
    public int getBaseMaxEnergy() { return baseMaxEnergy; }
    public int getBaseAttack() { return baseAttack; }
    public int getBaseDefense() { return baseDefense; }
    public SkillType getSkill() { return skill; }
    public double getHpGrowth() { return hpGrowth; }
    public double getAttackGrowth() { return attackGrowth; }
}
