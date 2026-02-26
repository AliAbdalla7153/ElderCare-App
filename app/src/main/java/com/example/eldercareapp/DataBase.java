package com.example.eldercareapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="My_Company";
    private static int DATABASE_VERSION = 1;
    private static final String TABLE_DATA ="info";
    private static final String Doctor_name = "DoctorName";
    private static final String Year = "year";
    private static final String Month = "month";
    private static final String Day = "day";

    public DataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreate = "CREATE TABLE " + TABLE_DATA + " ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Doctor_name + " TEXT, "
                + Year + " INTEGER, "
                + Month + " INTEGER, "
                + Day + " INTEGER)";

        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old table if it exists
        String dropTable = "drop table if exists " + TABLE_DATA;
        db.execSQL( dropTable);
        // Re-create tables
        onCreate( db );
    }
    public void insert(Appointment appointment) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlInsert = "INSERT INTO " + TABLE_DATA +
                " (" + Doctor_name + ", " + Year + ", " + Month + ", " + Day + ") VALUES ('"
                + appointment.getDoctorName() + "', "
                + appointment.getYear() + ", "
                + appointment.getMonth() + ", "
                + appointment.getDay() + ");";
        db.execSQL(sqlInsert);
        db.close();
    }

    public ArrayList<Appointment> selectAllAppointments() {
        SQLiteDatabase db = this.getWritableDatabase(); // same as Candy
        String sqlQuery = "SELECT * FROM " + TABLE_DATA;
        Cursor cursor = db.rawQuery(sqlQuery, null);
        ArrayList<Appointment> appointments = new ArrayList<>();
        while (cursor.moveToNext()) {
            // id is index 0, so we skip it
            String doctorName = cursor.getString(1); // DoctorName
            int year = cursor.getInt(2);
            int month = cursor.getInt(3);
            int day = cursor.getInt(4);

            Appointment appointment = new Appointment(doctorName, year, day, month);
            appointments.add(appointment);
        }
        db.close();
        return appointments;
    }
}
