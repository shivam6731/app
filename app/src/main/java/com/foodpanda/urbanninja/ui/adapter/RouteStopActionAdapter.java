package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.RouteStopActivity;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.RouteStopActivityType;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.interfaces.ShowMapAddressCallback;
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
    private ShowMapAddressCallback showMapAddressCallback;

    private LinkedHashMap<RouteStopActivity, Boolean> checkedActionsHashMap = new LinkedHashMap<>();
    private Stop stop;

    public RouteStopActionAdapter(
        Stop stop,
        Context context,
        NestedFragmentCallback nestedFragmentCallback,
        ShowMapAddressCallback showMapAddressCallback) {

        super(stop.getActivities(), context);
        setSelectable(false);
        //TODO should be removed
        //blocked by https://foodpanda.atlassian.net/browse/LOGI-324
        objects = removePayActivity(stop.getActivities());
        this.nestedFragmentCallback = nestedFragmentCallback;
        this.showMapAddressCallback = showMapAddressCallback;
        this.stop = stop;
        for (RouteStopActivity routeStopActivity : stop.getActivities()) {
            checkedActionsHashMap.put(routeStopActivity, false);
        }
        storageManager = App.STORAGE_MANAGER;
    }

    @Override
    public SimpleBaseAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_stop, parent, false);

                return new ViewHolder(view);
            case TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_stop_header, parent, false);

                return new ViewHolderHeaderFooter(view);
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
    public void onBindViewHolder(final SimpleBaseAdapter.BaseViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final RouteStopActivity routeStopActivity = getItem(holder.getAdapterPosition());
            viewHolder.checkBoxDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                //Check if all task are done and if it's true enable bottom main action button
                checkedActionsHashMap.put(routeStopActivity, isChecked);
                if (stop != null) {
                    nestedFragmentCallback.setActionButtonVisible(isAllChecked(),
                        stop.getTask() == RouteStopTask.PICKUP ? R.string.action_at_picked_up : R.string.action_at_delivered);
                }
            });
            if (TextUtils.isEmpty(routeStopActivity.getDescription())) {
                viewHolder.layoutDetails.setVisibility(View.GONE);
            } else {
                viewHolder.txtDescription.setText(routeStopActivity.getDescription());
                viewHolder.layoutDetails.setVisibility(View.VISIBLE);
            }

            if (routeStopActivity.getType() != null) {
                switch (routeStopActivity.getType()) {
                    case PICKUP:
                        viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_pick_up, routeStopActivity.getValue()));
                        viewHolder.imageSelected.setImageResource(R.drawable.icon_collect_order_dark);
                        setNotRelatedToHalalActionLayout(viewHolder);
                        break;
                    case DELIVER:
                        viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_deliver, routeStopActivity.getValue()));
                        viewHolder.imageSelected.setImageResource(R.drawable.icon_deliver_order_dark);
                        setNotRelatedToHalalActionLayout(viewHolder);
                        break;
                    case PAY_RESTAURANT:
                        viewHolder.imageSelected.setImageResource(R.drawable.icon_pay_restaurant);
                        viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_pay, getFormattedPrice(routeStopActivity)));
                        setNotRelatedToHalalActionLayout(viewHolder);
                        break;
                    case PREPARE_CHANGE:
                        viewHolder.imageSelected.setImageResource(R.drawable.icon_pay_restaurant);
                        viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_change, getFormattedPrice(routeStopActivity)));
                        setNotRelatedToHalalActionLayout(viewHolder);
                        break;
                    case COLLECT:
                        viewHolder.imageSelected.setImageResource(R.drawable.icon_collect_money_dark);
                        viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_collect, getFormattedPrice(routeStopActivity)));
                        setNotRelatedToHalalActionLayout(viewHolder);
                        break;
                    case HALAL:
                    case NON_HALAL:
                        setRelatedToHalalActionLayout(viewHolder, routeStopActivity);
                        break;
                }
            }


            // only for delivery type of item we need to show restaurant name to
            // let rider know from witch restaurant  this delivery order is
            if (routeStopActivity.getType() != RouteStopActivityType.DELIVER ||
                TextUtils.isEmpty(stop.getVendorName())) {
                viewHolder.layoutVendorName.setVisibility(View.GONE);
            } else {
                viewHolder.txtVendorName.setText(stop.getVendorName());
                viewHolder.layoutVendorName.setVisibility(View.VISIBLE);
            }

        } else if (holder instanceof ViewHolderHeaderFooter && !TextUtils.isEmpty(stop.getOrderCode())) {
            final ViewHolderHeaderFooter viewHolder = (ViewHolderHeaderFooter) holder;
            viewHolder.txtName.setText(context.getResources().getString(R.string.task_details_go_to, stop.getName()));
            int stringRes = holder.getAdapterPosition() == 0 ? R.string.route_action_last_step : R.string.route_action_up_step;
            viewHolder.txtType.setText(stringRes);

            viewHolder.expandableLayout.setOnClickListener(v -> expandLayout(viewHolder.expandableLayout, viewHolder.imageView));
            showMapAddressCallback.showNextPreviousStep(stop, R.id.step_layout);
        }
    }

    /**
     * set default layout background color and header view state for all task not related to halal
     *
     * @param viewHolder container for action
     */
    private void setNotRelatedToHalalActionLayout(ViewHolder viewHolder) {
        viewHolder.halalHeaderView.setVisibility(View.GONE);
        viewHolder.contentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.main_background_color));
    }

    /**
     * set layout background color and header view state for halal and not-halal tasks
     * moreover set name for action section
     *
     * @param viewHolder        container for action
     * @param routeStopActivity route stop action related to halal
     */
    private void setRelatedToHalalActionLayout(ViewHolder viewHolder, RouteStopActivity routeStopActivity) {
        viewHolder.halalHeaderView.setVisibility(View.VISIBLE);
        viewHolder.layoutDetails.setVisibility(View.VISIBLE);

        switch (routeStopActivity.getType()) {
            case HALAL:
                viewHolder.contentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.halal_background_color));
                viewHolder.halalHeaderView.setBackgroundColor(ContextCompat.getColor(context, R.color.green_text_color));
                viewHolder.imageSelected.setImageResource(R.drawable.icon_alert_green);
                viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_halal));
                viewHolder.txtDescription.setText(context.getResources().getString(R.string.route_action_halal_description));
                break;
            case NON_HALAL:
                viewHolder.contentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.not_halal_background_color));
                viewHolder.halalHeaderView.setBackgroundColor(ContextCompat.getColor(context, R.color.toolbar_color));
                viewHolder.imageSelected.setImageResource(R.drawable.icon_alert_red);
                viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_not_halal));
                viewHolder.txtDescription.setText(context.getResources().getString(R.string.route_action_not_halal_description));
                break;
        }
    }

    /**
     * Change background of the view and image mark
     * Expand layout to show previous or next task that rider should do
     * it would be fragment with map address and comments inside
     *
     * @param expandableLayout expand layout that hide or show detail of the step
     * @param imageView        image view with arrow to show close or open mark
     */
    private void expandLayout(
        ExpandableLayout expandableLayout,
        ImageView imageView) {

        expandableLayout.toggleExpansion();
        int imageResource;
        int backgroundResource;
        if (expandableLayout.isExpanded()) {
            imageResource = R.drawable.icon_deliver_down;
            backgroundResource = R.drawable.list_item_stop_header_not_selected;
        } else {
            imageResource = R.drawable.icon_deliver_up;
            backgroundResource = R.drawable.list_item_stop_header_selected;
        }
        imageView.setImageDrawable(ContextCompat.getDrawable(context, imageResource));
        expandableLayout.setBackgroundResource(backgroundResource);
    }


    /**
     * Set type depend on position
     * we have two types of view header and regular item
     *
     * @param position position of the view
     * @return type of view
     */
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    private String getFormattedPrice(RouteStopActivity routeStopActivity) {
        return FormatUtil.getValueWithCurrencySymbol(storageManager.getCountry(), routeStopActivity.getValue());
    }

    private class ViewHolderHeaderFooter extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtType;
        public TextView txtName;
        public ExpandableLayout expandableLayout;
        public ImageView imageView;

        public ViewHolderHeaderFooter(View view) {
            super(view);
            txtType = (TextView) view.findViewById(R.id.txt_type);
            txtName = (TextView) view.findViewById(R.id.txt_name);
            expandableLayout = (ExpandableLayout) view.findViewById(R.id.expand_layout);
            imageView = (ImageView) view.findViewById(R.id.image_arrow);
        }
    }

    private class ViewHolder extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtName;
        public TextView txtDescription;
        public ImageView imageSelected;
        public CheckBox checkBoxDone;
        public LinearLayout layoutDetails;
        public View halalHeaderView;
        public LinearLayout contentLayout;

        public TextView txtVendorName;
        public LinearLayout layoutVendorName;

        public ViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txt_stop_name);
            txtDescription = (TextView) view.findViewById(R.id.txt_stop_description);
            imageSelected = (ImageView) view.findViewById(R.id.image_stop_icon);
            checkBoxDone = (CheckBox) view.findViewById(R.id.checkbox_done);
            layoutDetails = (LinearLayout) view.findViewById(R.id.layout_details);

            layoutVendorName = (LinearLayout) view.findViewById(R.id.layout_vendor_name);
            txtVendorName = (TextView) view.findViewById(R.id.txt_stop_vendor_name);

            halalHeaderView = view.findViewById(R.id.halal_header_layout);
            contentLayout = (LinearLayout) view.findViewById(R.id.main_content_layout);
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

    /**
     * Here we set the number of not only content items count
     * but also include footer and header view
     * so we have +2 here because of header and footer
     *
     * @return count of ALL items in a recycler view with header and footer
     */
    @Override
    public int getItemCount() {
        return objects.size() + 1;
    }

    /**
     * In case of header with last step we have one more item in adapter
     * and to get real data without header we use position -1
     *
     * @param position position of view in the list included header
     * @return content date
     */
    @Override
    public RouteStopActivity getItem(int position) {
        return objects.get(position - 1);
    }

}
