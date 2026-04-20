package com.example.spacecolonypioneers.model;

public class CombatLogEntry {
    private String message;
    private LogType type;

    public enum LogType {
        NORMAL, DAMAGE, HEAL, SKILL, VICTORY, DEFEAT
    }

    public CombatLogEntry() {
    }

    public CombatLogEntry(String message, LogType type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() { return message; }
    public LogType getType() { return type; }
}
