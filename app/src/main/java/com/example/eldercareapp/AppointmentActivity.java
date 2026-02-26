package com.example.eldercareapp;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.eldercareapp.databinding.ActivityAppointmentBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class AppointmentActivity extends AppCompatActivity {
    private static final String TAG = "AppointmentActivity";
    private int year, month, day;
    private DataBase dbManager;
    private ActivityAppointmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        dbManager = new DataBase(this);
        
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.setNavigationOnClickListener(v -> finish());
        }

        createNotificationChannel();  
        checkTodayAppointments();     

        binding.dateTxt.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(AppointmentActivity.this,
                    (view, y, m, d) -> {
                        year = y;
                        month = m + 1;
                        day = d;
                        binding.dateTxt.setText(day + "/" + month + "/" + year);
                    },
                    currentYear, currentMonth, currentDay);
            dpd.show();
        });
    }

    public void Insert(View view) {
        String name = binding.nameTxt.getText().toString().trim();

        if (name.isEmpty() || binding.dateTxt.getText().toString().equals("Tap to select date")) {
            Toast.makeText(this, "Please enter doctor name and pick a date", Toast.LENGTH_SHORT).show();
            return;
        }

        Appointment appointment = new Appointment(name, year, day, month);
        dbManager.insert(appointment);

        Toast.makeText(this, "Appointment saved successfully!", Toast.LENGTH_SHORT).show();

        // Clear inputs
        binding.nameTxt.setText("");
        binding.dateTxt.setText("Tap to select date");
        
        // Return to main after short delay or let user add more
        // finish(); 
    }

    public void backToMain(View view) {
        this.finish();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "reminder_channel",
                    "Appointment Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for daily doctor appointment reminders");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void checkTodayAppointments() {
        Calendar today = Calendar.getInstance();
        int tYear = today.get(Calendar.YEAR);
        int tMonth = today.get(Calendar.MONTH) + 1;
        int tDay = today.get(Calendar.DAY_OF_MONTH);

        ArrayList<Appointment> allAppointments = dbManager.selectAllAppointments();

        for (Appointment appt : allAppointments) {
            if (appt.getYear() == tYear && appt.getMonth() == tMonth && appt.getDay() == tDay) {
                Toast.makeText(this, "Reminder: Appointment with Dr. " + appt.getDoctorName() + " today!", Toast.LENGTH_LONG).show();

                // Check and request notification permission (Android 13+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                        return; 
                    }
                }

                showNotification(appt);
            }
        }
    }

    private void showNotification(Appointment appt) {
        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(this, "reminder_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Doctor Appointment")
                .setContentText("You have an appointment with Dr. " + appt.getDoctorName() + " today.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        try {
            notificationManager.notify((int) System.currentTimeMillis(), mbuilder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Notification security exception", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkTodayAppointments(); 
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
