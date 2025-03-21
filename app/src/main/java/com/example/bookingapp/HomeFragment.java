package com.example.bookingapp;
import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // welcomes user by their name
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            db.collection("users").document(userEmail).get()
                    //  Finds all bookings for the user thats logged in via email
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("First name");
                            welcomeTextHomePage.setText("Welcome, " + firstName + "!");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("PersonFragment", "Error fetching user data", e));
        }
    }
// total number of apointments booked
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
//Stores the booked appointments from Firestore
    private List<QueryDocumentSnapshot> booked_appointments = new ArrayList<QueryDocumentSnapshot>();

//Loads all booked appointments and displays them in ListView
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
                            //containing all appointment details
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
    private void delete(int appointmentId) {
        String appointment_id = booked_appointments.get(appointmentId).getId();
        db.collection("Booked").document(appointment_id).delete();
    }

// function gets the details from firebase in fields of the selected appointment then pops up an alert to ask if to cancel.
    // generated with help of AI function CancelAppointmentAlert then edited to fit for my app
    private void CancelAppointmentAlert(){
        appointmentsListView
                .setOnItemClickListener((parent, view2, position, id) -> {
                    QueryDocumentSnapshot selectedAppointment = booked_appointments.get(position);
                    String selectedDate = selectedAppointment.getString("Date");
                    String selectedTimeSlot = selectedAppointment.getString("Time");

     Log.d(TAG, "Clicked time slot: " + selectedAppointment);
     AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle);
     builder.setTitle("Cancel Appointment");
     builder.setMessage("Are you sure you want to cancel your appointment on " + selectedDate + " at " + selectedTimeSlot + "?");
     builder.setPositiveButton("Cancel Appointment", (dialog, which) -> {
         delete(position);
         // cancel appointments functions
         Toast.makeText(requireContext(), "Appointment cancelled successfully!", Toast.LENGTH_LONG).show();
     });
     builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
     // alert is to be shown here
     AlertDialog alertDialog = builder.create();
     alertDialog.show();
     fetchAppointments(selectedDate,selectedTimeSlot);
 });
}
// Updates available appointment slots in Firestore
private void fetchAppointments(String selectedDate, String returningTime) {
        List<String> times_for_date = new ArrayList<>();// times_fot_date stores available appointments
    db.collection("appointments")
            .document(selectedDate.strip()).get()
            .addOnSuccessListener(
                    queryDocumentSnapshots -> {
                        Log.d(TAG, "The data from document" + queryDocumentSnapshots.get("availableTimes"));


                        //reference for below Stack Overflow (2018) How to get an array from Firestore? Available at: https://stackoverflow.com/questions/50233281/how-to-get-an-array-from-firestore (Accessed: 19 March 2025).
                        List<String> group = (List<String>) queryDocumentSnapshots.get("availableTimes");
                        //Gets available time slots from Firestore

                        if (group != null && !group.isEmpty()) {
                            // Add each available time slot (using ID as time slot)
                            for (String document : group) {
                                // adds everything in a list
                                times_for_date.add(document);
                            }
                            Log.d(TAG, "Data fetched from FB: " + group.toString());
                        } else {
                            Toast.makeText(requireContext(), "No available times for this date.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "No available times for this date.");
                        }
                        times_for_date.add(returningTime);// adds cancelled appoints to the list again
                        Collections.sort(times_for_date);
                        Map<String, Object> booked = new HashMap<>();
                        booked.put("availableTimes", times_for_date);
                        db.collection("appointments")// new availability updated
                                .document(selectedDate).set(booked).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    }
            );
    }
    }