package com.example.eldercareapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eldercareapp.databinding.ActivityInsertMedBinding;

public class InsertMedActivity extends AppCompatActivity {
    private DatabaseManager db;
    private ActivityInsertMedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsertMedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.setNavigationOnClickListener(v -> finish());
        }
        
        db = new DatabaseManager(this);
    }
    
    public void addMed(View view) {
        String nameStr = binding.medName.getText().toString().trim();
        String dosageStr = binding.dosage.getText().toString().trim();
        String timeStr = binding.time.getText().toString().trim();
        
        if (nameStr.isEmpty() || dosageStr.isEmpty() || timeStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("email", "");
        
        Medication med = new Medication(0, nameStr, dosageStr, timeStr, 0);
        db.insertMedication(med, userEmail);
        
        Toast.makeText(this, nameStr + " added successfully", Toast.LENGTH_SHORT).show();
        
        // Clear inputs
        binding.medName.setText("");
        binding.dosage.setText("");
        binding.time.setText("");
        
        // finish(); // Optional: close activity after adding
    }
    
    public void back(View view) {
        finish();
    }
}
