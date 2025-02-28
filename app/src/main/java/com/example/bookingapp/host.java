package com.example.bookingapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class host extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_host);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });

        BottomNavigationView bnv = findViewById(R.id.bottomNavigationView);
        bnv.setOnItemSelectedListener(this);
        bnv.setSelectedItemId(R.id.home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.person) {
            replaceFragment(new PersonFragment());
            return true;
        } else if(item.getItemId() == R.id.home) {
            replaceFragment(new HomeFragment());
            return true;

        } else if (item.getItemId() == R.id.gallery){
            replaceFragment(new GalleryFragment());
            return true;

        }else if (item.getItemId() == R.id.about_us){
            replaceFragment(new About_us_Fragment());
            return true;
        }else if (item.getItemId() == R.id.booking){
            replaceFragment(new BookingFragment());
            return true;
        }
        return false;
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true).
                replace(R.id.maFragment, fragment).commit();
    }
}