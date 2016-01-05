package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.foodpanda.urbanninja.ui.widget.ExpandableLayout;

import java.util.LinkedHashMap;
import java.util.List;

public class StopAdapter extends SimpleBaseAdapter<Stop, StopAdapter.ViewHolder> {
    private MainActivityCallback mainActivityCallback;
    private LinkedHashMap<Stop, Boolean> linkedHashMap = new LinkedHashMap<>();

    public StopAdapter(List<Stop> objects, Context context, MainActivityCallback mainActivityCallback) {
        super(objects, context);
        this.mainActivityCallback = mainActivityCallback;
        for (Stop stop : objects) {
            linkedHashMap.put(stop, false);
        }
    }

    @Override
    public StopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_stop, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Stop stop = getItem(position);

        holder.expandableLayout.setTag(holder);
        holder.checkBoxDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                linkedHashMap.put(stop, isChecked);
                mainActivityCallback.enableActionButton(isAllChecked(),R.string.action_at_picked_up);
            }
        });
    }

    public class ViewHolder extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtName;
        public TextView txtPrice;
        public TextView txtDescription;
        public ImageView imageSelected;
        public CheckBox checkBoxDone;
        public ExpandableLayout expandableLayout;
        public View viewContent;

        public ViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txt_stop_name);
            txtPrice = (TextView) view.findViewById(R.id.txt_stop_price);
            txtDescription = (TextView) view.findViewById(R.id.txt_stop_description);
            imageSelected = (ImageView) view.findViewById(R.id.image_stop_icon);
            checkBoxDone = (CheckBox) view.findViewById(R.id.checkbox_done);

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

    private boolean isAllChecked() {
        for (Stop stop : objects) {
            if (!linkedHashMap.get(stop)) {
                return false;
            }
        }
        return true;
    }

}
