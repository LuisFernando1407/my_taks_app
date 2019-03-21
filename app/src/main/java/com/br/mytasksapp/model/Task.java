package com.br.mytasksapp.model;

public class Task {
    private String id;
    private String userId;
    private String name;
    private String description;
    private String date;
    private boolean isNotified;


    public Task(String id, String userId, String name, String description, String date, boolean isNotified){
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.date = date;
        this.isNotified = isNotified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified(boolean notified) {
        isNotified = notified;
    }
}