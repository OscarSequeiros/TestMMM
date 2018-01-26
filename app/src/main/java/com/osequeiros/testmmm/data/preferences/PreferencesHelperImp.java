package com.osequeiros.testmmm.data.preferences;

import android.content.SharedPreferences;

/**
 * Created by osequeiros on 1/25/18.
 */

public class PreferencesHelperImp implements PreferencesHelper {

    public static String MY_SHARED_PREFERENCES = "com.osequeiros.testmmm";
    private final String KEY_LATITUD = MY_SHARED_PREFERENCES + ".latitud";
    private final String KEY_LONGITUD = MY_SHARED_PREFERENCES + ".longitud";

    private SharedPreferences sharedPreferences;


    public PreferencesHelperImp(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    private SharedPreferences.Editor editor() {
        return sharedPreferences.edit();
    }

    @Override
    public void saveLatLon(String lat, String lon) {
        SharedPreferences.Editor editor = editor();
        editor.putString(KEY_LATITUD, lat);
        editor.putString(KEY_LONGITUD, lon);
        editor.apply();
    }

    @Override
    public String getLatLon() {
        String lat = sharedPreferences.getString(KEY_LATITUD, "");
        String lon = sharedPreferences.getString(KEY_LONGITUD, "");
        return lat + " , " + lon;
    }
}
