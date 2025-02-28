package com.example.bookingapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class About_us_Fragment extends Fragment {


   public About_us_Fragment() {
   }


    //View view;
   // @Override
  //  public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
   //     view= inflater.inflate(R.layout.fragment_about_us_, container, false);
   //     return view;

   //     Intent intent= new Intent(About_us_Fragment.this,MapsActivityclass);
   //     startActivity(intent);
   // }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us_, container, false);

        Intent intent = new Intent(getActivity(), MapsActivity.class);
        startActivity(intent);

        return view;
    }




}



