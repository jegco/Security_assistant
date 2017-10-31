package com.example.jorgecaro.info_usuario_jorge_caro.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jorgecaro.info_usuario_jorge_caro.MainActivity;
import com.example.jorgecaro.info_usuario_jorge_caro.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginFragment extends Fragment implements FacebookCallback<LoginResult>{

    private View view;
    private GoogleApiClient googleApiClient;
    private static final int GOOGLE_REQUEST_CODE = 1;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        com.google.android.gms.common.SignInButton googleSignInButton = (com.google.android.gms.common.SignInButton) view.findViewById(R.id.signin_google);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin_google();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        ((LoginButton)view.findViewById(R.id.signin_facebook)).setFragment(this);
        ((LoginButton)view.findViewById(R.id.signin_facebook)).registerCallback(callbackManager, this);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null){
                    Log.i("currentProfile", "entro a currentProfile");
                    MainFragment mainFragment = new MainFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("email", currentProfile.getName());
                    mainFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();
                }

            }
        };
        profileTracker.startTracking();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), ((MainActivity)getActivity()))
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        return view;
    }

    private void signin_google() {
        Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(i, GOOGLE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_REQUEST_CODE){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSingInResult(googleSignInResult);
        }else{
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void handleSingInResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()){
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
            if (googleSignInAccount != null){
                MainFragment mainFragment = new MainFragment();
                Bundle bundle = new Bundle();
                bundle.putString("email", googleSignInAccount.getEmail());
                mainFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();
            }
        }
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.i("onSuccess", "si salio"+loginResult.toString());
    }

    @Override
    public void onCancel() {
        Log.i("onCancel", "se cancelo el inicio de sesion con facebook");
    }

    @Override
    public void onError(FacebookException error) {
        Log.i("onError", "este es el error de facebook"+error.toString());
    }
}
