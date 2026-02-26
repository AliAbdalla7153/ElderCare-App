package com.example.eldercareapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.eldercareapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DatabaseManager db;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check login status
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("email", null);
        String firstName = prefs.getString("firstName", "Friend");
        
        if (userEmail == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Use View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Setup Toolbar
        setSupportActionBar(binding.toolbar);

        db = new DatabaseManager(this);
        
        // Setup Bottom Navigation
        setupBottomNavigation();

        // Date handling
        String dbToday = getDbDate(0);
        String dbYesterday = getDbDate(-1);
        
        // Display user-friendly date and personalized greeting
        String displayDate = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(new Date());
        binding.todayDate.setText("Hi, " + firstName + "!\n" + displayDate);

        // Check-in logic
        setupCheckInLogic(dbToday, dbYesterday, userEmail);

        // Fetch and display the prayer times
        PrayerTimeFetcher.getPrayerTimes(
                this,
                binding.fajrTextView,
                binding.dhuhrTextView,
                binding.asrTextView,
                binding.maghribTextView,
                binding.ishaTextView);
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.nav_home) {
                // Already on home
                return true;
            } else if (id == R.id.nav_appointments) {
                startActivity(new Intent(this, ViewAppointmentsActivity.class));
                return true;
            } else if (id == R.id.nav_medication) {
                showMedicationOptions();
                return true;
            } else if (id == R.id.nav_logout) {
                logout();
                return true;
            }
            return false;
        });
        
        // Keep "Home" selected by default
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
    }

    private void showMedicationOptions() {
        String[] options = {"Medication Reminder", "Add Medication", "Remove Medication"};
        
        new AlertDialog.Builder(this)
                .setTitle("Medication Management")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Reminder
                            startActivity(new Intent(this, MedicationActivity.class));
                            break;
                        case 1: // Add
                            startActivity(new Intent(this, InsertMedActivity.class));
                            break;
                        case 2: // Remove
                            startActivity(new Intent(this, DeleteMedActivity.class));
                            break;
                    }
                })
                .show();
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    prefs.edit().clear().apply();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
                })
                .show();
    }

    private void setupCheckInLogic(String today, String yesterday, String userEmail) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        
        if (db.checkInExists(today, userEmail)) {
            binding.checkInStatus.setText("You have already checked in today");
            binding.checkInBtn.setEnabled(false);
            binding.checkInBtn.setText("CHECKED IN");
            binding.checkInBtn.setAlpha(0.6f);
        } else {
            binding.checkInBtn.setOnClickListener(v -> {
                db.insertCheckIn(today, "OK", userEmail);
                binding.checkInStatus.setText("You have checked in today");
                Toast.makeText(MainActivity.this, "Check-in successful!", Toast.LENGTH_SHORT).show();
                binding.checkInBtn.setEnabled(false);
                binding.checkInBtn.setText("CHECKED IN");
                binding.checkInBtn.setAlpha(0.6f);
                
                new JavaMailAPI(userEmail, 
                    "Elder Care Daily Check up completed!", 
                    "The Daily check for today was marked!!").start();
                Toast.makeText(MainActivity.this, "Confirmation email sent", Toast.LENGTH_SHORT).show();
            });
        }

        String lastAlertedDate = prefs.getString("lastAlertedDate_" + userEmail, "");
        if (!db.checkInExists(yesterday, userEmail) && !lastAlertedDate.equals(yesterday)) {
            Log.w(TAG, "Yesterday's check-in was missed. Sending alert.");
            new JavaMailAPI(userEmail, 
                "Elder Care Daily Check up missed!", 
                "The Daily check for yesterday (" + yesterday + ") was not marked!!\nPlease get in contact as soon as possible").start();
            prefs.edit().putString("lastAlertedDate_" + userEmail, yesterday).apply();
        }
    }

    private String getDbDate(int daysOffset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, daysOffset);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return day + "/" + month + "/" + year;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure Home is selected when returning to this screen
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
    }

    public void emergencycall(View view) {
        Log.i(TAG, "Emergency call initiated");
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("email", "aliabdallah25577@gmail.com");
        
        new JavaMailAPI(userEmail, 
            "Elder Care Emergency Call initiated!", 
            "The app user initiated an emergency call. Please get in immediate contact!").start();
        
        Toast.makeText(MainActivity.this, "Emergency alert sent to family", Toast.LENGTH_LONG).show();

        String number = "998";
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
    }

    public void AddAppointment(View view) {
        startActivity(new Intent(this, AppointmentActivity.class));
    }
}
