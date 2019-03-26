package com.br.mytasksapp.model;

public class Setting {
    private String action;
    private boolean isActive;

    public Setting(String action, boolean isActive) {
        this.action = action;
        this.isActive = isActive;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}