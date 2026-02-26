package com.example.eldercareapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eldercareapp.databinding.ActivityDeleteMedBinding;

import java.util.ArrayList;

public class DeleteMedActivity extends AppCompatActivity {
    private DatabaseManager dbManager;
    private ActivityDeleteMedBinding binding;
    private MedicationAdapter adapter;
    private ArrayList<Medication> medications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteMedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        dbManager = new DatabaseManager(this);
        
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.setNavigationOnClickListener(v -> finish());
        }

        setupRecyclerView();
        updateList();
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        medications = new ArrayList<>();
        adapter = new MedicationAdapter();
        binding.recyclerView.setAdapter(adapter);
    }

    private void updateList() {
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("email", "");
        medications = dbManager.selectAllMedications(userEmail);
        if (medications.isEmpty()) {
            binding.instructionText.setText("No medications to delete");
        } else {
            binding.instructionText.setText("Tap on a medication to delete it");
        }
        adapter.notifyDataSetChanged();
    }

    public void back(View view) {
        finish();
    }

    // Inner Adapter Class
    private class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_medication_delete, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Medication med = medications.get(position);
            holder.nameText.setText(med.getName());
            holder.detailsText.setText(med.getDosage() + " at " + med.getTime());
            
            holder.itemView.setOnClickListener(v -> {
                new AlertDialog.Builder(DeleteMedActivity.this)
                        .setTitle("Delete Medication")
                        .setMessage("Are you sure you want to delete " + med.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            dbManager.deleteById(med.getId());
                            Toast.makeText(DeleteMedActivity.this, "Medication deleted", Toast.LENGTH_SHORT).show();
                            updateList();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return medications.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText, detailsText;

            ViewHolder(View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.medName);
                detailsText = itemView.findViewById(R.id.medDetails);
            }
        }
    }
}
