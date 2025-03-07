package com.example.bookingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class About_us_Fragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

   public About_us_Fragment() {
   }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us_, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // ref for below Android Developers, 2024. Common Intents: Send an email. Available at: https://developer.android.com/guide/components/intents-common#Email [Accessed 6 Mar. 2025].
        TextView directionsText = view.findViewById(R.id.clickHereForDirections_text);
        directionsText.setOnClickListener(v -> {
            //Creates Uniform Resource Identifier
            //this then shows with LiwsNailz label
            Uri gmmIntentUri = Uri.parse("geo:52.94679514301795,-1.1414552874246011?q=52.94679514301795,-1.1414552874246011(LiwsNailz)");
            //opens google maps app
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            //checks if the maps app is installed
            if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        });

        // ref for below Android Developers (2024) Verify intent resolution before launching. Available at: https://developer.android.com/reference/android/content/pm/PackageManager.html (Accessed: 7 March 2025).
        TextView phoneText = view.findViewById(R.id.phone_text);
        phoneText.setOnClickListener(v -> {
            Uri phoneUri = Uri.parse("tel:+44123456789");
            Intent callIntent = new Intent(Intent.ACTION_DIAL, phoneUri);
            startActivity(callIntent);
        });

//ref for below Google (2024) Common intents. Available at: https://developer.android.com/guide/components/intents-common (Accessed: 7 March 2025).
        TextView emailText = view.findViewById(R.id.email_text);
        emailText.setOnClickListener(v -> {
            //sends data as an email
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822"); // MIME type for email messaging
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@liwsnailz.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry from LiwsNailz App");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, I’d like to ask about...");

            if (emailIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(emailIntent);
            } else {
                Toast.makeText(requireContext(), "No email app found", Toast.LENGTH_SHORT).show();
            }
        });
        TextView instagramText = view.findViewById(R.id.instagram_text);
        instagramText.setOnClickListener(v -> {
            String instagramUrl = "https://www.instagram.com/liwsnailz?igsh=MTYxam9kN2lyMTBzYQ==";
            Intent instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl));
            if (instagramIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(instagramIntent);
            } else {
                // If the Instagram app is not installed, open the link in a browser
                instagramIntent.setData(Uri.parse(instagramUrl));
                startActivity(instagramIntent);
            }
        });


        return view;


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
// coordinates for my location
        LatLng location = new LatLng(52.94679514301795, -1.1414552874246011);
        mMap.addMarker(new MarkerOptions().position(location).title("Marker in Business Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }

}



