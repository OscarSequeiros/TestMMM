package com.osequeiros.testmmm.nav.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.osequeiros.testmmm.R;
import com.osequeiros.testmmm.data.entities.Item;

import java.util.List;

/**
 * Created by osequeiros on 1/25/18.
 */

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {

    private List<Item> itemsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView description;

        public MyViewHolder(View view) {
            super(view);
            description = view.findViewById(R.id.item_description);
        }
    }


    public ItemsAdapter(List<Item> itemsList) {
        this.itemsList = itemsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item item = itemsList.get(position);
        holder.description.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
}
