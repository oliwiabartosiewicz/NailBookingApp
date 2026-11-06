package com.example.bookingapp;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    Button signupclicked;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        signupclicked = findViewById(R.id.buttonSignupSubmit);
        signupclicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupButtonClicked(v);
            }
        });

}

    public void signupButtonClicked(View view){
        EditText email = findViewById(R.id.editTextSignupEmail);
        EditText password = findViewById(R.id.editTextSignupPassword);
        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();
        signup(sEmail, sPassword);
    }

    private void signup(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("SignupActivity", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignupActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();
                            userInfo();
                            Intent intent = new Intent(SignupActivity.this, host.class);
                            startActivity(intent);

                        } else {
                            Log.w("SignupActivity", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Incorrect details.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void userInfo () {

        Map<String, Object> user = new HashMap<>();
        EditText firstName = findViewById(R.id.editTextSignupFirstName);
        EditText lastName = findViewById(R.id.editTextSignupLastName);
        String sfirstName = firstName.getText().toString();
        String slastName = lastName.getText().toString();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String semail= currentUser.getEmail();

        user.put("First name",sfirstName);
        user.put("Surname",slastName);
        user.put("email", semail);

        // saves everything in firestore
        db.collection("users").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("SignupActivity", "DocumentSnapshot successfully written!"+ documentReference.getId()); }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("SignupActivity", "Error writing document", e);
                        }
                    });
    }
}