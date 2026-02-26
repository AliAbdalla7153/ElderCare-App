package com.example.eldercareapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EldercareDB";
    private static int DATABASE_VERSION = 3;
    
    // Medications table
    private static final String TABLE_MEDICATION = "medications";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DOSAGE = "dosage";
    private static final String TIME = "time";
    private static final String TAKEN = "taken";
    private static final String MED_USER_EMAIL = "user_email";

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String USER_EMAIL = "email";
    private static final String USER_PASSWORD = "password";
    private static final String USER_FIRST_NAME = "first_name";
    private static final String USER_LAST_NAME = "last_name";
    
    // Check-ins table
    private static final String TABLE_CHECKIN = "checkins";
    private static final String CHECKIN_DATE = "date";
    private static final String CHECKIN_STATUS = "status";
    private static final String CHECKIN_USER_EMAIL = "user_email";

    public DatabaseManager(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateMed = "create table " + TABLE_MEDICATION + "(" +
                ID + " integer primary key autoincrement, " +
                NAME + " text, " +
                DOSAGE + " text, " +
                TIME + " text, " +
                TAKEN + " integer default 0, " +
                MED_USER_EMAIL + " text)";
        db.execSQL(sqlCreateMed);

        String sqlCreateCheckin = "create table " + TABLE_CHECKIN + " (" +
                CHECKIN_DATE + " text, " +
                CHECKIN_STATUS + " text, " +
                CHECKIN_USER_EMAIL + " text, " +
                "PRIMARY KEY (" + CHECKIN_DATE + ", " + CHECKIN_USER_EMAIL + "))";
        db.execSQL(sqlCreateCheckin);
        
        String sqlCreateUsers = "create table " + TABLE_USERS + "(" +
                USER_EMAIL + " text primary key, " +
                USER_PASSWORD + " text, " +
                USER_FIRST_NAME + " text, " +
                USER_LAST_NAME + " text)";
        db.execSQL(sqlCreateUsers);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_MEDICATION);
        db.execSQL("drop table if exists " + TABLE_CHECKIN);
        db.execSQL("drop table if exists " + TABLE_USERS);
        onCreate(db);
    }
    
    public boolean registerUser(String email, String password, String firstName, String lastName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_USERS + " where " + USER_EMAIL + " = ?", new String[]{email});
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return false; 
        }
        cursor.close();
        
        String sqlInsert = "insert into " + TABLE_USERS + " values(?, ?, ?, ?)";
        db.execSQL(sqlInsert, new Object[]{email, password, firstName, lastName});
        db.close();
        return true;
    }
    
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_USERS + " where " + USER_EMAIL + " = ? and " + USER_PASSWORD + " = ?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
    
    public String getUserFirstName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + USER_FIRST_NAME + " from " + TABLE_USERS + " where " + USER_EMAIL + " = ?", new String[]{email});
        String firstName = "";
        if (cursor.moveToFirst()) {
            firstName = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return firstName;
    }

    public void insertMedication(Medication med, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlInsert = "insert into " + TABLE_MEDICATION + " (" + NAME + ", " + DOSAGE + ", " + TIME + ", " + TAKEN + ", " + MED_USER_EMAIL + ") " +
                "values(?, ?, ?, 0, ?)";
        db.execSQL(sqlInsert, new Object[]{med.getName(), med.getDosage(), med.getTime(), userEmail});
        db.close();
    }

    public void deleteById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlDelete = "delete from " + TABLE_MEDICATION + " where " + ID + " = " + id;
        db.execSQL(sqlDelete);
        db.close();
    }

    public void updateTakenStatus(int id, int taken) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "update " + TABLE_MEDICATION + " set " + TAKEN + " = " + taken + " where " + ID + " = " + id;
        db.execSQL(sql);
        db.close();
    }

    public ArrayList<Medication> selectAllMedications(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "select * from " + TABLE_MEDICATION + " where " + MED_USER_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{userEmail});

        ArrayList<Medication> medications = new ArrayList<>();
        while (cursor.moveToNext()) {
            Medication med = new Medication(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4)
            );
            medications.add(med);
        }
        cursor.close();
        db.close();
        return medications;
    }

    public void insertCheckIn(String date, String status, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        String insert = "insert into " + TABLE_CHECKIN + " (" + CHECKIN_DATE + ", " + CHECKIN_STATUS + ", " + CHECKIN_USER_EMAIL + ") values(?, ?, ?)";
        db.execSQL(insert, new Object[]{date, status, userEmail});
        db.close();
    }

    public boolean checkInExists(String date, String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CHECKIN + " where " + CHECKIN_DATE + " = ? and " + CHECKIN_USER_EMAIL + " = ?", 
                new String[]{date, userEmail});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }
}
