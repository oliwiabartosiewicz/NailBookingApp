package com.example.bookingapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PersonFragment extends Fragment {

    public PersonFragment() {
    }

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView welcomeText, firstNameBox, lastNameBox, emailBox;
    private Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        welcomeText = view.findViewById(R.id.welcomeText);
        firstNameBox = view.findViewById(R.id.firstNameBox);
        lastNameBox = view.findViewById(R.id.lastNameBox);
        emailBox = view.findViewById(R.id.emailBox);
        logoutButton = view.findViewById(R.id.logoutButton);

        loadUserInfo();
        logoutButton.setOnClickListener(v -> logoutUser());

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
                            String lastName = documentSnapshot.getString("Surname");
                            String email = documentSnapshot.getString("email");

                            // shows names in each box
                            welcomeText.setText("Welcome, " + firstName + "!");
                            firstNameBox.setText("First Name: " + firstName);
                            lastNameBox.setText("Last Name: " + lastName);
                            emailBox.setText("Email: " + email);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("PersonFragment", "Error fetching user data", e));
        }
    }
// reference for below Google (2025) Firebase Authentication. Available at: https://firebase.google.com/docs/auth/android/manage-users#sign_out (Accessed: 12 March 2025).
// reference for below Stack Overflow. (2016). Properly log out a user from Android app. Available at: https://stackoverflow.com/questions/35541675/properly-log-out-a-user-from-android-app [Accessed 12 Mar. 2025].
    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}