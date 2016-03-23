package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.RouteStopActivity;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.RouteStopActivityType;
import com.foodpanda.urbanninja.model.enums.RouteStopTaskStatus;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.widget.ExpandableLayout;
import com.foodpanda.urbanninja.utils.FormatUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class RouteStopActionAdapter extends SimpleBaseAdapter<RouteStopActivity, SimpleBaseAdapter.BaseViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final StorageManager storageManager;
    private NestedFragmentCallback nestedFragmentCallback;
    private LinkedHashMap<RouteStopActivity, Boolean> checkedActionsHashMap = new LinkedHashMap<>();
    private Stop stop;

    public RouteStopActionAdapter(Stop stop, Context context, NestedFragmentCallback nestedFragmentCallback) {
        super(stop.getActivities(), context);
        //TODO should be removed
        //blocked by https://foodpanda.atlassian.net/browse/LOGI-324
        objects = removePayActivity(stop.getActivities());
        this.nestedFragmentCallback = nestedFragmentCallback;
        this.stop = stop;
        for (RouteStopActivity routeStopActivity : stop.getActivities()) {
            checkedActionsHashMap.put(routeStopActivity, false);
        }
        storageManager = App.STORAGE_MANAGER;
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
     * and set action or header value to the ViewMainContentHolder
     *
     * @param holder   SimpleBaseAdapter.BaseViewHolder holder instance
     * @param position in a list on content
     */
    @Override
    public void onBindViewHolder(SimpleBaseAdapter.BaseViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final RouteStopActivity routeStopActivity = getItem(position);
            viewHolder.expandableLayout.setTag(holder);
            viewHolder.checkBoxDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //Check if all task are done and if it's true enable bottom main action button
                    checkedActionsHashMap.put(routeStopActivity, isChecked);
                    if (stop != null) {
                        nestedFragmentCallback.enableActionButton(isAllChecked(),
                            stop.getTask() == RouteStopTaskStatus.PICKUP ? R.string.action_at_picked_up : R.string.action_at_delivered);
                    }
                }
            });

            switch (routeStopActivity.getType()) {
                case PICKUP:
                    viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_pick_up));
                    viewHolder.imageSelected.setImageResource(R.drawable.ico_collect);
                    viewHolder.txtPrice.setText(context.getResources().getString(R.string.route_action_items, routeStopActivity.getValue()));
                    break;
                case DELIVER:
                    viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_deliver));
                    viewHolder.imageSelected.setImageResource(R.drawable.ico_collect);
                    viewHolder.txtPrice.setText(context.getResources().getString(R.string.route_action_items, routeStopActivity.getValue()));
                    break;
                case PAY_RESTAURANT:
                    viewHolder.imageSelected.setImageResource(R.drawable.ico_pay);
                    viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_pay));
                    viewHolder.txtPrice.setText(getFormattedPrice(routeStopActivity));
                    break;
                case PREPARE_CHANGE:
                    viewHolder.imageSelected.setImageResource(R.drawable.ico_pay);
                    viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_change));
                    viewHolder.txtPrice.setText(getFormattedPrice(routeStopActivity));
                    break;
                case COLLECT:
                    viewHolder.imageSelected.setImageResource(R.drawable.ico_pay);
                    viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_collect));
                    viewHolder.txtPrice.setText(getFormattedPrice(routeStopActivity));
                    break;

            }
            viewHolder.txtDescription.setText(routeStopActivity.getDescription());

        } else if (holder instanceof ViewHolderHeader && !TextUtils.isEmpty(stop.getOrderCode())) {
            ViewHolderHeader viewHolder = (ViewHolderHeader) holder;
            int textResourceLink = 0;
            int imageResourceLink = 0;
            switch (stop.getTask()) {
                case PICKUP:
                    textResourceLink = R.string.route_action_header_pick_up;
                    imageResourceLink = R.drawable.ico_restaurant;
                    break;
                case DELIVER:
                    textResourceLink = R.string.route_action_header_deliver;
                    imageResourceLink = R.drawable.ico_user;
                    break;
            }

            viewHolder.txtName.setText(
                Html.fromHtml(
                    context.getResources().getString(textResourceLink,
                        "<br> <b>" + stop.getName() + "</b>")));
            viewHolder.imageOrderType.setImageDrawable(ContextCompat.getDrawable(context, imageResourceLink));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return isPositionHeader(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    private String getFormattedPrice(RouteStopActivity routeStopActivity) {
        return FormatUtil.getValueWithCurrencySymbol(storageManager.getCountry(), routeStopActivity.getValue());
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private class ViewHolderHeader extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtName;
        public ImageView imageOrderType;

        public ViewHolderHeader(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txt_order_name);
            imageOrderType = (ImageView) view.findViewById(R.id.image_order_type);
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
        for (RouteStopActivity routeStopActivity : objects) {
            if (!checkedActionsHashMap.get(routeStopActivity)) {

                return false;
            }
        }

        return true;
    }

    //TODO should be removed after testing in a Honk-Kong
    // blocked by https://foodpanda.atlassian.net/browse/LOGI-324
    private List<RouteStopActivity> removePayActivity(List<RouteStopActivity> list) {
        for (Iterator<RouteStopActivity> iterator = list.iterator(); iterator.hasNext(); ) {
            RouteStopActivity routeStopActivity = iterator.next();
            if (routeStopActivity.getType() == RouteStopActivityType.PAY_RESTAURANT) {
                iterator.remove();
            }
        }

        return list;
    }

    @Override
    public int getItemCount() {
        return objects.size() + 1;
    }

    @Override
    public RouteStopActivity getItem(int position) {
        return objects.get(position - 1);
    }
}
