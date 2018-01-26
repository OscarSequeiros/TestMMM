package com.osequeiros.testmmm.background;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.osequeiros.testmmm.data.preferences.PreferencesHelperImp;

/**
 * Created by osequeiros on 1/25/18.
 */

public class ServiceLocation extends Service {

    /** Intervalo deseado para la toma de actualizaciones de ubicación : */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5 * 60 * 1000;
    
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSetttingsRequest;
    private GoogleApiClient mGoogleApiClient;

    /** Callback para los eventos de ubicación : */
    private LocationCallback mLocationCallback;

    /** Actual ubicación geográfica : */
    private Location mCurrentLocation;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSettingsClient = LocationServices.getSettingsClient(this);

        mGoogleApiClient = new GoogleApiClient.Builder(ServiceLocation.this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.i("Info", "On connect googleApiClient");
                        createLocationCallback();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.i("Info", "On connectSuspend googleApiClient");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.i("Info", "On connect failed googleApiClient");
                    }
                })
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        startLocationsUpdate();

        return START_STICKY;
    }

    /** Módulo de creación del callback para recibir cada ubicación : */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                Log.i("Info", "latlon : " + mCurrentLocation.getLatitude() + "," +
                        mCurrentLocation.getLongitude());
                // Alamcenamos en un shared preferences :
                SharedPreferences preferences = getSharedPreferences(
                        PreferencesHelperImp.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                PreferencesHelperImp preferencesHelper = new PreferencesHelperImp(preferences);
                preferencesHelper.saveLatLon(String.valueOf(mCurrentLocation.getLatitude()),
                        String.valueOf(mCurrentLocation.getLongitude()));
            }
        };
    }

    /** Creamos la solicitud de ubicación */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /** Usamos un Builder para crear el request que se usa para verificar si el dispositivo
     * posee la configuración de ubicación necesaria : */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSetttingsRequest = builder.build();
    }

    /** Iniciamos el proceso */
    private void startLocationsUpdate() {
        mSettingsClient.checkLocationSettings(mLocationSetttingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("Info", "All location settings are satisfied.");
                        if (mGoogleApiClient.isConnected()) {
                            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                                    PackageManager.PERMISSION_GRANTED) {
                                LocationServices.FusedLocationApi.requestLocationUpdates(
                                        mGoogleApiClient, mLocationRequest, mLocationCallback,
                                        Looper.myLooper());
                            }
                        } else {
                            Log.i("Info", "GoogleApiClient doesn't connected.");
                        }
                        //mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        //mLocationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED :
                                Log.i("Info", "Location settings are not " +
                                        "satisfied. Attempting to upgrade location settings ");
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and " +
                                        "cannot be fixed here. Fix in Settings.";
                                Log.e("Error", errorMessage);
                        }
                    }
                });
    }
}
