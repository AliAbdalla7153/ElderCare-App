package com.example.eldercareapp;

public class Medication {
    private int id;
    private String name;
    private String dosage;
    private String time;
    private int taken;

    public Medication(int id, String name, String dosage, String time, int taken) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.time = time;
        this.taken = taken;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDosage() { return dosage; }
    public String getTime() { return time; }
    public int getTaken() { return taken; }

    public void setTaken(int taken) { this.taken = taken; }

    public String toString() {
        return id + " " + name + " " + dosage + " at " + time + (taken == 1 ? " [Taken]" : " [Not taken yet]");
    }
}