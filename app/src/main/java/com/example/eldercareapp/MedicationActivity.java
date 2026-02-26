package com.example.eldercareapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eldercareapp.databinding.ActivityMedicationBinding;

import java.util.ArrayList;

public class MedicationActivity extends AppCompatActivity {
    private DatabaseManager db;
    private ArrayList<Medication> meds;
    private int currentIndex = 0;
    private ActivityMedicationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.setNavigationOnClickListener(v -> finish());
        }

        db = new DatabaseManager(this);
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("email", "");
        meds = db.selectAllMedications(userEmail);

        if (meds.isEmpty()) {
            binding.medNameText.setText("No medications found");
            binding.medDosageText.setVisibility(View.GONE);
            binding.medTimeText.setVisibility(View.GONE);
            binding.statusLabel.setVisibility(View.GONE);
            binding.takenBtn.setEnabled(false);
            binding.nextBtn.setEnabled(false);
        } else {
            showMedication(currentIndex);
        }
    }

    public void markTaken(View view) {
        if (meds.isEmpty()) return;
        
        Medication med = meds.get(currentIndex);
        if (med.getTaken() == 0) {
            db.updateTakenStatus(med.getId(), 1);
            med.setTaken(1);
            showMedication(currentIndex);
            Toast.makeText(this, med.getName() + " marked as taken", Toast.LENGTH_SHORT).show();
        }
    }

    public void nextMed(View view) {
        if (meds.isEmpty()) return;
        
        if (currentIndex < meds.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        showMedication(currentIndex);
    }

    public void back(View view) {
        finish();
    }

    private void showMedication(int index) {
        Medication med = meds.get(index);
        
        binding.medNameText.setText(med.getName());
        binding.medDosageText.setText("Dosage: " + med.getDosage());
        binding.medTimeText.setText("Time: " + med.getTime());
        
        binding.medDosageText.setVisibility(View.VISIBLE);
        binding.medTimeText.setVisibility(View.VISIBLE);
        binding.statusLabel.setVisibility(View.VISIBLE);

        if(med.getTaken() == 1) {
            binding.statusLabel.setText("TAKEN");
            binding.statusLabel.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            binding.statusLabel.setBackgroundColor(0xFFE8F5E9); // Light green bg
            binding.takenBtn.setEnabled(false);
            binding.takenBtn.setText("ALREADY TAKEN");
            binding.takenBtn.setAlpha(0.6f);
        } else {
            binding.statusLabel.setText("NOT TAKEN");
            binding.statusLabel.setTextColor(getResources().getColor(R.color.sos_red));
            binding.statusLabel.setBackgroundColor(0xFFFFEBEE); // Light red bg
            binding.takenBtn.setEnabled(true);
            binding.takenBtn.setText("MARK AS TAKEN");
            binding.takenBtn.setAlpha(1.0f);
        }
        
        binding.info.setText("Medication " + (index + 1) + " of " + meds.size());
    }
}
