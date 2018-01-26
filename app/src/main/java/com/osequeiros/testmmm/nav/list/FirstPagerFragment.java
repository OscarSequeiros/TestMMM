package com.osequeiros.testmmm.nav.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osequeiros.testmmm.R;
import com.osequeiros.testmmm.data.entities.Item;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by osequeiros on 1/25/18.
 */

public class FirstPagerFragment extends Fragment {

    @BindView(R.id.recycler_view) RecyclerView mRecycler;
    private ItemsAdapter mAdapter;
    private List<Item> itemList = new ArrayList<>();

    public FirstPagerFragment newInstance() {
        return new FirstPagerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_first, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new ItemsAdapter(itemList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(
                getActivity().getApplicationContext());
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setAdapter(mAdapter);

        prepareData();

        return view;
    }

    private void prepareData() {
        for (int i = 0; i < 10; i++) {
            Item item = new Item("Descripción nro " + (i + 1));
            itemList.add(item);
        }
        mAdapter.notifyDataSetChanged();
    }
}
