package com.example.bookingapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng location = new LatLng(52.94679514301795, -1.1414552874246011);
        mMap.addMarker(new MarkerOptions().position(location).title("Marker in Business Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        // Set a custom location
       // LatLng location = new LatLng(52.94679514301795, -1.1414552874246011);
       // mMap.addMarker(new MarkerOptions().position(location).title("Marker in Business Location"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }




}



