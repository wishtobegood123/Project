package com.example.spacecolonypioneers.model;

public class Enemy {
    private int id;
    private String name;
    private int hp;
    private int maxHp;
    private int attack;
    private int defense;

    public Enemy() {
    }

    public Enemy(int id, String name, int maxHp, int attack, int defense) {
        this.id = id;
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attack = attack;
        this.defense = defense;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(0, Math.min(hp, maxHp)); }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
}
