package com.example.eldercareapp;

public class Appointment {
    private String doctorName;
    private int year;
    private int month;
    private int day;

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public Appointment(String doctorName, int year, int day, int month) {
        this.doctorName = doctorName;
        this.year = year;
        this.day = day;
        this.month = month;
    }  }