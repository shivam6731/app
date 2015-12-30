package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Stop;

import java.util.List;

public class StopAdapter extends SimpleBaseAdapter<Stop, StopAdapter.ViewHolder> {

    public StopAdapter(List<Stop> objects, Context context) {
        super(objects, context);
    }

    @Override
    public StopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_country, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Stop stop = getItem(position);

    }

    public class ViewHolder extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtName;
        public ImageView imageSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_country_name);
            imageSelected = (ImageView) itemView.findViewById(R.id.image_selected_country);
        }
    }

}

