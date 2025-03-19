package com.example.bookingapp;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class BookingFragment extends Fragment {
    private static final String TAG = "BookingFragment";
    public BookingFragment() {
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_booking, container, false);


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
            Log.d(TAG, "Selected Date "+ selectedDate);



            db.collection("appointments")
                    .document(selectedDate.strip()).get()
                    .addOnSuccessListener(
                            queryDocumentSnapshots -> {
                                Log.d(TAG,"The data from document"+ queryDocumentSnapshots.get("availableTimes"));
                            // Clear old data before adding new time slots
                            timeSlots.clear();

  //                        reference for below Stack Overflow (2018) How to get an array from Firestore? Available at: https://stackoverflow.com/questions/50233281/how-to-get-an-array-from-firestore (Accessed: 19 March 2025).
                                List<String> group = (List<String>) queryDocumentSnapshots.get("availableTimes");

                                if (group != null && !group.isEmpty()) {
                                    // Add each available time slot (using document ID as time slot)
                                    for (String document : group) {
                                        timeSlots.add(document);
                                    }
                                }else {
                                    Toast.makeText(requireContext(), "No available times for this date.", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "No available times for this date.");
                    }
                            // Notify the adapter to update the ListView
                            adapter.notifyDataSetChanged();
                            }
                    );

        });
        return view;
    }


    private void bookSlot(){

    }

}