package com.osequeiros.testmmm.nav.latlon;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.osequeiros.testmmm.R;
import com.osequeiros.testmmm.data.preferences.PreferencesHelper;
import com.osequeiros.testmmm.data.preferences.PreferencesHelperImp;

import butterknife.BindView;

/**
 * Created by osequeiros on 1/25/18.
 */

public class LatlonFragment extends Fragment {

    public static LatlonFragment newInstance() {
        return new LatlonFragment();
    }

    @BindView(R.id.text_latlon)
    TextView mTextLatLon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gps, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences preferences = getActivity().getSharedPreferences(
                PreferencesHelperImp.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        PreferencesHelper preferencesHelper = new PreferencesHelperImp(preferences);

        mTextLatLon.setText(preferencesHelper.getLatLon());
    }
}
