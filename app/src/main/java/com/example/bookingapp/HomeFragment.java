package com.example.bookingapp;
import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
public class HomeFragment extends Fragment {
    public HomeFragment() {
    }
    private TextView welcomeTextHomePage;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView totalappointmentsbooked;
    private ListView appointmentsListView;
    private ArrayList<String> appointmentList= new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        welcomeTextHomePage = view.findViewById(R.id.welcomeTextHomePage);
        totalappointmentsbooked = view.findViewById(R.id.totalappointmentsbooked);
        appointmentsListView = view.findViewById(R.id.appointmentsListView);

        loadUserInfo();
        loadTotalAppointments();
        loadAppointments();
        CancelAppointmentAlert();
        return view;
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            db.collection("users").document(userEmail).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("First name");
                            welcomeTextHomePage.setText("Welcome, " + firstName + "!");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("PersonFragment", "Error fetching user data", e));
        }
    }

    private void loadTotalAppointments() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            db.collection("Booked")
                    .whereEqualTo("Email", userEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        int totalAppointments = queryDocumentSnapshots.size();
                        totalappointmentsbooked.setText("Total Appointments: " + totalAppointments);
                    })
                    .addOnFailureListener(e -> Log.e("HomeFragment", "Error fetching appointments", e));
        }
    }


    private List<QueryDocumentSnapshot> booked_appointments = new ArrayList<QueryDocumentSnapshot>();
    private void loadAppointments() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            db.collection("Booked")
                    .whereEqualTo("Email", userEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        appointmentList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String appointmentDetails = "Date: " + document.getString("Date") +
                                    ", Time: " + document.getString("Time");
                            appointmentList.add(appointmentDetails);
                            booked_appointments.add(document);
                        }
                        //Adapter to display the appointments in the ListView
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_list_item_1, appointmentList);
                        appointmentsListView.setAdapter(adapter);
                    })
                    .addOnFailureListener(e -> Log.e("HomeFragment", "Error fetching appointments", e));
        }
    }

    // deletes the appointment by one that was selected from firebase
    private void delete(String appointmentId) {
        String appointment_id = booked_appointments.get(0).getId();
        db.collection("Booked").document(appointment_id).delete();
    }

// function gets the details from firebase in fields of the selected appointment then pops up an alert to ask if to cancel.
    private void CancelAppointmentAlert(){
        appointmentsListView
                .setOnItemClickListener((parent, view2, position, id) -> {
                    QueryDocumentSnapshot selectedAppointment = booked_appointments.get(position);
                    String selectedDate = selectedAppointment.getString("Date");
                    String selectedTimeSlot = selectedAppointment.getString("Time");
                    String appointmentId = selectedAppointment.getId();
     Log.d(TAG, "Clicked time slot: " + selectedAppointment);
     AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle);
     builder.setTitle("Cancel Appointment");
     builder.setMessage("Are you sure you want to cancel your appointment on " + selectedDate + " at " + selectedTimeSlot + "?");
     builder.setPositiveButton("Cancel Appointment", (dialog, which) -> {
         delete(appointmentId);
         // cancel appointments functions
         Toast.makeText(requireContext(), "Appointment cancelled successfully!", Toast.LENGTH_LONG).show();
     });
     builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
     // alert is to be shown here
     AlertDialog alertDialog = builder.create();
     alertDialog.show();
 });
}



    }