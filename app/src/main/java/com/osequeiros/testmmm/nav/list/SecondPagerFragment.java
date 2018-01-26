package com.osequeiros.testmmm.nav.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osequeiros.testmmm.R;


/**
 * Created by osequeiros on 1/25/18.
 */

public class SecondPagerFragment extends Fragment {

    public SecondPagerFragment newInstance() {
        return new SecondPagerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_second, container, false);

        return view;
    }
}
