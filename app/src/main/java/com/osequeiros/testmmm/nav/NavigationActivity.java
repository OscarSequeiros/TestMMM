package com.osequeiros.testmmm.nav;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.osequeiros.testmmm.R;
import com.osequeiros.testmmm.background.ServiceLocation;
import com.osequeiros.testmmm.nav.gps.GpsFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by osequeiros on 1/25/18.
 */

public class NavigationActivity extends AppCompatActivity {

    @BindView(R.id.navigation) BottomNavigationView mNavigation;
    private GoogleApiClient googleApiClient;

    private final int PERMISSION_REQUEST_LOCATION = 0x321;
    private int REQUIRED_GPS = 0x246;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        ButterKnife.bind(this);

        checkPermissions();

        loadFragment(com.osequeiros.testmmm.nav.list.ListFragment.newInstance());
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_list:
                    fragment = com.osequeiros.testmmm.nav.list.ListFragment.newInstance();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_gps:
                    fragment = GpsFragment.newInstance();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_latlon:

                    return true;
            }
            return false;
        }
    };

    /** Módulo para cambiar de fragment */
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /** Módulo para solicitar los permisos en tiempo de ejecución */
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Permiso denegado anteriormente, explicar razones para asignar permiso:
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar dialog
                new AlertDialog.Builder(this)
                        .setTitle("Acceso a ubicación necesaria")
                        .setMessage("Es necesario que brinde permisos de ubicación.")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(NavigationActivity.this,
                                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                        PERMISSION_REQUEST_LOCATION);
            }
        } else {
            checkLocationGPS();
        }
    }

    /** Módulo para validar  habilitación GPS */
    private void checkLocationGPS() {
        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager != null) {
            if (hasGPSDevice(this)) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // Habilitación de GPS
                    if (googleApiClient == null) {
                        googleApiClient = new GoogleApiClient.Builder(this)
                                .addApi(LocationServices.API)
                                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                    @Override
                                    public void onConnected(Bundle bundle) { }

                                    @Override
                                    public void onConnectionSuspended(int i) { }
                                })
                                .addOnConnectionFailedListener(
                                        new GoogleApiClient.OnConnectionFailedListener() {
                                            @Override
                                            public void onConnectionFailed(
                                                    @NonNull ConnectionResult connectionResult) {
                                                Log.e("Erorr", "Location error " +
                                                        connectionResult.getErrorMessage());
                                            }
                                        }).build();

                        googleApiClient.connect();

                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationRequest.setInterval(ServiceLocation.UPDATE_INTERVAL_IN_MILLISECONDS);
                        locationRequest.setFastestInterval(ServiceLocation.UPDATE_INTERVAL_IN_MILLISECONDS);
                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest
                                .Builder().addLocationRequest(locationRequest);

                        //La apliación requiere del GPS
                        builder.setAlwaysShow(true);

                        Task<LocationSettingsResponse> result = LocationServices
                                .getSettingsClient(getApplicationContext())
                                .checkLocationSettings(builder.build());

                        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                                try {
                                    LocationSettingsResponse response = task
                                            .getResult(ApiException.class);
                                    Log.i("Info", "Location settings are satisfied");
                                } catch (ApiException exception) {
                                    switch (exception.getStatusCode()) {
                                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED :
                                            // Los ajustes de ubicación no se cumplen,
                                            // mostramos dialog :
                                            try {
                                                ResolvableApiException resolvable =
                                                        (ResolvableApiException) exception;
                                                resolvable.startResolutionForResult(
                                                        NavigationActivity.this,
                                                        REQUIRED_GPS);
                                            } catch (IntentSender.SendIntentException e) {
                                                Log.e("Error", e.getMessage());
                                            } catch (ClassCastException e) {
                                                e.printStackTrace();
                                                Log.e("Error", e.getMessage());
                                            }
                                            break;
                                    }
                                }
                            }
                        });
                    }
                } else {
                    Log.i("Info", "GPS ya habilitado");
                    if (googleApiClient == null) {
                        googleApiClient = new GoogleApiClient.Builder(this)
                                .addApi(LocationServices.API)
                                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                    @Override
                                    public void onConnected(Bundle bundle) { }

                                    @Override
                                    public void onConnectionSuspended(int i) { }
                                })
                                .addOnConnectionFailedListener(
                                        new GoogleApiClient.OnConnectionFailedListener() {
                                            @Override
                                            public void onConnectionFailed(
                                                    @NonNull ConnectionResult connectionResult) {
                                                Log.e("Error", "Location error " +
                                                        connectionResult.getErrorCode());
                                            }
                                        }).build();
                        googleApiClient.connect();
                    }
                }
            }
        }
    }

    /** Modulo para validar la existencia de GPS en el dispositivo */
    private boolean hasGPSDevice(Context context) {
        LocationManager mngr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mngr == null) {
            return false;
        } else {
            List<String> providers = mngr.getAllProviders();
            return (providers != null && providers.contains(LocationManager.GPS_PROVIDER));
        }
    }

    /** Callback de los permisos asignados */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationGPS();
                } else {
                    // Permiso denegado, mostramos dialog de advertencia
                    new AlertDialog.Builder(this)
                            .setTitle("Funcionamiento incorrecto")
                            .setMessage("Es posible que alguna de las funciones de la aplicación " +
                                    "dejen de funcionar.")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            }
        }
    }
}
