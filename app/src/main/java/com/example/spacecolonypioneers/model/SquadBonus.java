package com.example.spacecolonypioneers.model;

import com.example.spacecolonypioneers.model.enums.Profession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SquadBonus {
    private String name;
    private String description;
    private double xpBonus;
    private double damageBonus;
    private double damageReduction;

    public SquadBonus() {
        this("Standard Squad", "No special bonus", 1.0, 1.0, 0.0);
    }

    public SquadBonus(String name, String description, double xpBonus, double damageBonus, double damageReduction) {
        this.name = name;
        this.description = description;
        this.xpBonus = xpBonus;
        this.damageBonus = damageBonus;
        this.damageReduction = damageReduction;
    }

    public static SquadBonus calculateSquadBonus(List<CrewMember> squad) {
        if (squad == null || squad.isEmpty()) return new SquadBonus();
        Map<Profession, Integer> professionCount = new HashMap<Profession, Integer>();
        for (CrewMember crew : squad) {
            if (crew != null && crew.getProfession() != null) {
                int count = professionCount.containsKey(crew.getProfession()) ? professionCount.get(crew.getProfession()) : 0;
                professionCount.put(crew.getProfession(), count + 1);
            }
        }

        double totalXpBonus = 1.0;
        double totalDamageBonus = 1.0;
        double totalDamageReduction = 0.0;
        StringBuilder bonusName = new StringBuilder();
        StringBuilder bonusDesc = new StringBuilder();

        if (professionCount.containsKey(Profession.SOLDIER) && professionCount.containsKey(Profession.MEDIC)) {
            totalXpBonus += 0.15;
            totalDamageReduction += 0.1;
            bonusName.append("Field Medics ");
            bonusDesc.append("Soldier + Medic: XP +15%, damage reduction +10%\n");
        }
        if (professionCount.containsKey(Profession.ENGINEER) && professionCount.containsKey(Profession.SCOUT)) {
            totalDamageBonus += 0.2;
            bonusName.append("Tech Vanguard ");
            bonusDesc.append("Engineer + Scout: damage +20%\n");
        }
        if (professionCount.containsKey(Profession.COMMANDER)) {
            int commanderCount = professionCount.get(Profession.COMMANDER);
            totalXpBonus += commanderCount * 0.1;
            totalDamageBonus += commanderCount * 0.05;
            bonusName.append("Command Network ");
            bonusDesc.append("Commander: XP +10%, damage +5%\n");
        }
        if (squad.size() >= 4) {
            totalXpBonus += 0.25;
            bonusName.append("Large-Scale Ops ");
            bonusDesc.append("Squad of 4+: XP +25%\n");
        }
        if (bonusName.length() == 0) return new SquadBonus();
        return new SquadBonus(bonusName.toString().trim(), bonusDesc.toString().trim(), totalXpBonus, totalDamageBonus, totalDamageReduction);
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getXpBonus() { return xpBonus; }
    public double getDamageBonus() { return damageBonus; }
    public double getDamageReduction() { return damageReduction; }
}
