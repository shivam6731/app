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

public class StopAdapter extends SimpleBaseAdapter<Stop, SimpleBaseAdapter.BaseViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private MainActivityCallback mainActivityCallback;
    private LinkedHashMap<Stop, Boolean> checkedActionsHashMap = new LinkedHashMap<>();

    public StopAdapter(List<Stop> objects, Context context, MainActivityCallback mainActivityCallback) {
        super(objects, context);
        this.mainActivityCallback = mainActivityCallback;
        for (Stop stop : objects) {
            checkedActionsHashMap.put(stop, false);
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_stop, parent, false);

                return new ViewHolder(view);
            case TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_stop_header, parent, false);

                return new ViewHolderHeader(view);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    /**
     * This method would recognize is this header or regular item
     * and set action or header value to the ViewHolder 
     * @param holder SimpleBaseAdapter.BaseViewHolder holder instance
     * @param position in a list on content
     */
    @Override
    public void onBindViewHolder(SimpleBaseAdapter.BaseViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final Stop stop = getItem(position);
            viewHolder.expandableLayout.setTag(holder);
            viewHolder.checkBoxDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //Check if all task are done and if it's true enable bottom main action button
                    checkedActionsHashMap.put(stop, isChecked);
                    mainActivityCallback.enableActionButton(isAllChecked(), R.string.action_at_picked_up);
                }
            });
        } else if (holder instanceof ViewHolderHeader) {
            ViewHolderHeader viewHolder = (ViewHolderHeader) holder;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return isPositionHeader(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private class ViewHolderHeader extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtName;
        public TextView txtDescription;


        public ViewHolderHeader(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txt_order_name);
            txtDescription = (TextView) view.findViewById(R.id.txt_order_description);
        }
    }


    private class ViewHolder extends SimpleBaseAdapter.BaseViewHolder {
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
            if (!checkedActionsHashMap.get(stop)) {
                return false;
            }
        }

        return true;
    }

}
