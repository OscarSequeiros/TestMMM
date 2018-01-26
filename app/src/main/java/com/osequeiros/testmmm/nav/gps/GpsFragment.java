package com.osequeiros.testmmm.nav.gps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osequeiros.testmmm.R;
import com.osequeiros.testmmm.background.ServiceLocation;

import butterknife.OnClick;

/**
 * Created by osequeiros on 1/25/18.
 */

public class GpsFragment extends Fragment {

    public static GpsFragment newInstance() {
        return new GpsFragment();
    }

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

    @OnClick(R.id.btn_start_service)
    public void startService() {
        Intent serviceIntent = new Intent(getActivity().getApplicationContext(), ServiceLocation.class);
        getActivity().getApplicationContext().startService(serviceIntent);
    }
}
