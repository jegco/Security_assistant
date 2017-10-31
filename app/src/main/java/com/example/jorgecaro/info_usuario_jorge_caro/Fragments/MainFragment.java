package com.example.jorgecaro.info_usuario_jorge_caro.Fragments;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jorgecaro.info_usuario_jorge_caro.R;
import com.example.jorgecaro.info_usuario_jorge_caro.Util.GeofenceTransitionIntentService;
import com.example.jorgecaro.info_usuario_jorge_caro.Util.SimpleGeofence;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class MainFragment extends Fragment {

    private View view;
    private TextView email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        email = (TextView) view.findViewById(R.id.email);
        email.setText(getArguments().getString("email"));
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactFragment contactFragment = new ContactFragment();
                Bundle arg = new Bundle();
                arg.putString("email", getArguments().getString("email"));
                contactFragment.setArguments(arg);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, contactFragment).commit();
            }
        });
        new GeofenceTransitionIntentService(getActivity(), getContext()).onCreate();
        return view;
    }


}
