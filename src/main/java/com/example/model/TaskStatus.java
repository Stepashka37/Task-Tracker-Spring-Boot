package com.example.model;

public enum TaskStatus {
    NEW("NEW"),
    IN_PROGRESS("IN PROGRESS"),
    DONE("DONE");

    private String displayStatus;

    TaskStatus(String displayStatus){
        this.displayStatus =displayStatus;
    }

    public String getDisplayStatus(){
        return displayStatus;
    }

    @Override
    public String toString() {
        return displayStatus;
    }

    public static TaskStatus convert(String name){
        switch (name){
            case "NEW": return NEW;
            case "DONE": return DONE;
            case "IN PROGRESS": return IN_PROGRESS;
        }
        return NEW;
    }
}

