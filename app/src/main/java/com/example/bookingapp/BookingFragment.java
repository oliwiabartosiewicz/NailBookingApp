package com.example.bookingapp;
import android.app.AlertDialog;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingFragment extends Fragment {
    private static final String TAG = "BookingFragment";
    ArrayList<String> timeSlots = new ArrayList<>();// available appoints
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
        // adapter and ArrayList to store time slots
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, timeSlots);
        listView.setAdapter(adapter);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Log.d(TAG, String.valueOf(dayOfMonth) + "day of the month");
            Log.d(TAG, String.valueOf(month) + " the month");
            Log.d(TAG, String.valueOf(year) + " the year");
// ref for date input OpenAI. (2025) Why use String.format in Java for date formatting?. Available at: https://chat.openai.com/ (Accessed:18 March 2025).
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
                                    // Add each available time slot (using ID as time slot)
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
            // created by CHAT GPT below 3 lines, then edited myself
            listView.setOnItemClickListener((parent, view2, position, id) -> {
                String selectedTimeSlot = timeSlots.get(position);
                Log.d(TAG, "Clicked time slot: " + selectedTimeSlot);

                //Ref for below Stack Overflow (2021) Creating dialogue box pop up Android Studio Available at: https://stackoverflow.com/questions/66297577/creating-dialogue-box-pop-up-android-studio (Accessed: 19 March 2025).
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle);
                builder.setTitle("Confirm Appointment");
                builder.setMessage("Please confirm the appointment "+" on "+selectedDate+" at "+selectedTimeSlot);

                builder.setPositiveButton("Confirm", (dialog, which) -> {
                    bookAppointments(selectedDate, selectedTimeSlot);
                    Toast.makeText(requireContext(), "Appointment booked successfully!", Toast.LENGTH_LONG).show();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                // alert
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
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
                        Log.d(TAG, "User email: " + userEmail);

// ref for below Google (n.d.) Add data to Firestore Available at: https://firebase.google.com/docs/firestore/manage-data/add-data#java_2 (Accessed: 19 March 2025).
                        Map<String, Object> booked = new HashMap<>();
                        booked.put("FirstName", firstName);
                        booked.put("LastName", lastName);
                        booked.put("Email",userEmail);
                        booked.put("Date", date);
                        booked.put("Time", time);
                        Log.d(TAG, "User email: " + userEmail);

                        db.collection("Booked").add(booked)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!"+ documentReference.getId());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    }
                    removeBookingSlot(date,time);
                })
                .addOnFailureListener(e -> Log.e("PersonFragment", "Error fetching user data", e));


    }

    private void removeBookingSlot(String date, String time){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> booked = new HashMap<>();
// creating an array puts all the appointments back in other than the one selected by user
        List<String> existing_appoints = new ArrayList<>();
        for(String ts : timeSlots) {
            if(!ts.equalsIgnoreCase(time)) {
                existing_appoints.add(ts);
            }
        }

        booked.put("availableTimes", existing_appoints);//Put the updated list of available times into the map

        db.collection("appointments")
                .document(date).set(booked).addOnSuccessListener(new OnSuccessListener<Void>() {
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


    }



