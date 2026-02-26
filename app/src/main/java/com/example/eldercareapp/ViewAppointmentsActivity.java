package com.example.eldercareapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eldercareapp.databinding.ActivityViewAppointmentsBinding;

import java.util.ArrayList;

public class ViewAppointmentsActivity extends AppCompatActivity {
    private ActivityViewAppointmentsBinding binding;
    private DataBase db;
    private ArrayList<Appointment> appointments;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAppointmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.setNavigationOnClickListener(v -> finish());
        }

        db = new DataBase(this);
        appointments = db.selectAllAppointments();

        if (appointments.isEmpty()) {
            binding.noAppointmentsText.setVisibility(View.VISIBLE);
        } else {
            binding.noAppointmentsText.setVisibility(View.GONE);
            setupRecyclerView();
        }
    }

    public void backToHome(View view) {
        finish();
    }

    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(appointments);
        binding.appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.appointmentsRecyclerView.setAdapter(adapter);
    }

    private class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
        private ArrayList<Appointment> apptList;

        public AppointmentAdapter(ArrayList<Appointment> list) {
            this.apptList = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_appointment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Appointment appt = apptList.get(position);
            holder.drName.setText("Dr. " + appt.getDoctorName());
            holder.apptDate.setText("Date: " + appt.getDay() + "/" + appt.getMonth() + "/" + appt.getYear());
        }

        @Override
        public int getItemCount() {
            return apptList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView drName, apptDate;

            ViewHolder(View itemView) {
                super(itemView);
                drName = itemView.findViewById(R.id.drName);
                apptDate = itemView.findViewById(R.id.appointmentDate);
            }
        }
    }
}
