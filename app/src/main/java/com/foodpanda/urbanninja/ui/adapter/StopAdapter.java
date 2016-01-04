package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.ui.widget.ExpandableLayout;

import java.util.List;

public class StopAdapter extends SimpleBaseAdapter<Stop, StopAdapter.ViewHolder> {

    public StopAdapter(List<Stop> objects, Context context) {
        super(objects, context);
    }

    @Override
    public StopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_stop, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Stop stop = getItem(position);

        holder.expandableLayout.setTag(holder);
    }

    public class ViewHolder extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtName;
        public TextView txtPrice;
        public TextView txtDescription;
        public ImageView imageSelected;
        public ExpandableLayout expandableLayout;
        public View viewContent;

        public ViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txt_stop_name);
            txtPrice = (TextView) view.findViewById(R.id.txt_stop_price);
            txtDescription = (TextView) view.findViewById(R.id.txt_stop_description);
            imageSelected = (ImageView) view.findViewById(R.id.image_stop_icon);

            expandableLayout = (ExpandableLayout) view.findViewById(R.id.expand_layout);

            viewContent = view.findViewById(R.id.layout_main_content);
            viewContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLayout.toggleExpansion();
                }
            });
        }
    }

}
