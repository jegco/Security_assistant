package com.example.jorgecaro.info_usuario_jorge_caro.Util;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by jorge caro on 10/22/2017.
 */

public class GeofenceTransitionIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private static final int REQUEST_CODE = 1;
    private static final String[] PERMISOS = {
            Manifest.permission.READ_CONTACTS
    };
    private SimpleGeofence simpleGeofence;
    ArrayList<Geofence> geofences;
    private PendingIntent pendingIntent;
    private Activity activity;
    private Context context;

    public GeofenceTransitionIntentService(Activity activity, Context context) {
        super(GeofenceTransitionIntentService.class.getName());
        this.activity = activity;
        this.context = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        geofences = new ArrayList<>();
        if (!googlePlayServicesAvaiable()){
            Toast.makeText(context, "No esta disponible el servicio de GooglePlay Services", Toast.LENGTH_SHORT).show();
        } else {
            googleApiClient.connect();
            createGeofence();
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.i("geofencingerror", geofencingEvent.getErrorCode() + "");
        } else {
            int transitionType = geofencingEvent.getGeofenceTransition();
            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                googleApiClient.blockingConnect(Constant.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                String geofenceLaunched = geofencingEvent.getTriggeringGeofences().get(0).getRequestId();
                Toast.makeText(getBaseContext(), "Entraste a la zona de peligro con el id: "+geofenceLaunched, Toast.LENGTH_SHORT);
                Log.i("geofence", "entro a la zona de geofence");
            } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
                googleApiClient.blockingConnect(Constant.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                String geofenceLaunched = geofencingEvent.getTriggeringGeofences().get(0).getRequestId();
                Toast.makeText(getBaseContext(), "Salio a la zona de peligro con el id: "+geofenceLaunched, Toast.LENGTH_SHORT);
                Log.i("geofence", "salio a la zona de geofence");
            } else if (Geofence.GEOFENCE_TRANSITION_DWELL == transitionType){
                googleApiClient.blockingConnect(Constant.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                Toast.makeText(getBaseContext(), "Has estado demasiado tiempo en la zona de peligro", Toast.LENGTH_SHORT);
                Log.i("geofence", "Has estado demasiado tiempo en la zona de peligro");
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        pendingIntent = getGeofenceTransitionPendingIntent();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISOS, REQUEST_CODE);
        }
        LocationServices.GeofencingApi.addGeofences(googleApiClient, geofences, pendingIntent);

        Toast.makeText(activity, "Iniciando servicio de Geofence", Toast.LENGTH_SHORT).show();
        Log.i("InicGeofe","Iniciando servicio de geofence");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()){
            try {
                connectionResult.startResolutionForResult(activity, Constant.CONNECTION_FALIURE_RESOLUTION_REQUEST);
            }catch (IntentSender.SendIntentException e){
                Toast.makeText(activity, "Ocurrio un error en el servicio de Google Play Services", Toast.LENGTH_SHORT).show();
                Log.i("Error_services", e.toString());
            }
        }else Toast.makeText(activity, "Ocurrio un error de conexion", Toast.LENGTH_SHORT).show();
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public void createGeofence(){
        simpleGeofence = new SimpleGeofence(
                Constant.ANDROID_ID,
                Constant.ANDROID_LATITUDE,
                Constant.ANDROID_LONGIUDE,
                Constant.ANDROID_RADIUS_METERS,
                Constant.GEOFENCE_EXPIRATION_TIME,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL,
                Constant.ANDROID_LOITERING_DELAY
        );

        geofences.add(simpleGeofence.toGeofence());
        Log.i("CreateGeofence", "entro al createGeofence");
    }

    public boolean googlePlayServicesAvaiable(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == resultCode){
            return true;
        }
        return false;
    }

    public PendingIntent getGeofenceTransitionPendingIntent(){
        Intent i = new Intent(activity, GeofenceTransitionIntentService.class);
        return PendingIntent.getService(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
