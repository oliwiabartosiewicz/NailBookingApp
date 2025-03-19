package com.example.bookingapp;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingFragment extends Fragment {
    private static final String TAG = "BookingFragment";

    public BookingFragment() {
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_booking, container, false);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        ListView listView = view.findViewById(R.id.listView);

        // reference for below array https://stackoverflow.com/questions/10972860/classcastexception-arraylist-cannot-be-cast-to-string
        // Initialise the adapter and ArrayList to store time slots
        ArrayList<String> timeSlots = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, timeSlots);
        listView.setAdapter(adapter);


        // CalendarView listener to fetch time slots for selected date
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Log.d(TAG, String.valueOf(dayOfMonth) + "day of the month");
            Log.d(TAG, String.valueOf(month) + " the month");
            Log.d(TAG, String.valueOf(year) + " the year");

            // Format the selected date as dd-MM-yyyy
            String selectedDate = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year);
            Log.d(TAG, "Selected Date " + selectedDate);


            db.collection("appointments")
                    .document(selectedDate.strip()).get()
                    .addOnSuccessListener(
                            queryDocumentSnapshots -> {
                                Log.d(TAG, "The data from document" + queryDocumentSnapshots.get("availableTimes"));
                                // Clear old data before adding new time slots
                                timeSlots.clear();

                                //reference for below Stack Overflow (2018) How to get an array from Firestore? Available at: https://stackoverflow.com/questions/50233281/how-to-get-an-array-from-firestore (Accessed: 19 March 2025).
                                List<String> group = (List<String>) queryDocumentSnapshots.get("availableTimes");

                                if (group != null && !group.isEmpty()) {
                                    // Add each available time slot (using document ID as time slot)
                                    for (String document : group) {
                                        timeSlots.add(document);
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "No available times for this date.", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "No available times for this date.");
                                }
                                // Notify the adapter to update the ListView
                                adapter.notifyDataSetChanged();
                            }
                    );
            // created by CHAT GPT
            listView.setOnItemClickListener((parent, view2, position, id) -> {
                String selectedTimeSlot = timeSlots.get(position);
                Log.d(TAG, "Clicked time slot: " + selectedTimeSlot);
                bookAppointments(selectedDate,selectedTimeSlot);
                Toast.makeText(requireContext(), "Selected: " + selectedTimeSlot, Toast.LENGTH_SHORT).show();
            });


        });
        return view;
    }
    private void bookAppointments(String date, String time) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User not logged in!");
            return;
        }
        String userEmail = currentUser.getEmail();
        db.collection("users").document(userEmail).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        String firstName = documentSnapshot.getString("First name");
                        String lastName = documentSnapshot.getString("Surname");

// ref for below Google (n.d.) Add data to Firestore Available at: https://firebase.google.com/docs/firestore/manage-data/add-data#java_2 (Accessed: 19 March 2025).
                        Map<String, Object> booked = new HashMap<>();
                        booked.put("FirstName",firstName);
                        booked.put("LastName",lastName);
                        booked.put("Date",date);
                        booked.put("Time",time);

                        db.collection("Booked")
                                .document(currentUser.getEmail()).set(booked).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                })
                .addOnFailureListener(e -> Log.e("PersonFragment", "Error fetching user data", e));
    }









    }



