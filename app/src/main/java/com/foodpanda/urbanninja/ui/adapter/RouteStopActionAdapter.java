package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.manager.MultiPickupManager;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.RouteStopActivity;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.DialogType;
import com.foodpanda.urbanninja.model.enums.RouteStopActivityType;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.interfaces.ShowMapAddressCallback;
import com.foodpanda.urbanninja.ui.widget.ExpandableLayout;
import com.foodpanda.urbanninja.ui.widget.RecyclerViewEmpty;
import com.foodpanda.urbanninja.utils.FormatUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class RouteStopActionAdapter extends SimpleBaseAdapter<RouteStopActivity, SimpleBaseAdapter.BaseViewHolder> {
    private static final int TYPE_HEADER_OR_FOOTER = 0;
    private static final int TYPE_ITEM = 1;

    private final StorageManager storageManager;
    private NestedFragmentCallback nestedFragmentCallback;
    private ShowMapAddressCallback showMapAddressCallback;

    private LinkedHashMap<RouteStopActivity, Boolean> checkedActionsHashMap = new LinkedHashMap<>();
    private Stop stop;

    private RecyclerViewEmpty recyclerView;

    public RouteStopActionAdapter(
        Stop stop,
        Context context,
        NestedFragmentCallback nestedFragmentCallback,
        ShowMapAddressCallback showMapAddressCallback,
        RecyclerViewEmpty recyclerView) {

        super(stop.getActivities(), context);
        setSelectable(false);
        //TODO should be removed
        //blocked by https://foodpanda.atlassian.net/browse/LOGI-324
        objects = removePayActivity(stop.getActivities());
        this.nestedFragmentCallback = nestedFragmentCallback;
        this.showMapAddressCallback = showMapAddressCallback;
        this.stop = stop;
        this.recyclerView = recyclerView;
        for (RouteStopActivity routeStopActivity : stop.getActivities()) {
            checkedActionsHashMap.put(routeStopActivity, false);
        }
        storageManager = App.STORAGE_MANAGER;
    }

    // All both of this ViewHolders extend BaseView Holder
    // So this call is safe
    @SuppressWarnings("unchecked")
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_stop, parent, false);

                return new ViewHolder(view);
            case TYPE_HEADER_OR_FOOTER:
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
     * @param position in the list of content
     */
    @Override
    public void onBindViewHolder(final SimpleBaseAdapter.BaseViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            setRouteStopActionView((ViewHolder) holder);
        } else if (holder instanceof ViewHolderHeaderFooter) {
            setDataForNextLastStep((ViewHolderHeaderFooter) holder);
        }
    }

    /**
     * Set data for each order action
     * add action type name
     * add action type icons
     * add details if they exist
     * add restaurant name for delivery type
     *
     * @param viewHolder action view layout container
     */
    private void setRouteStopActionView(ViewHolder viewHolder) {
        final RouteStopActivity routeStopActivity = getItem(viewHolder.getAdapterPosition());

        viewHolder.checkBoxDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //Check if all task are done and if it's true enable bottom main action button
            checkedActionsHashMap.put(routeStopActivity, isChecked);
            if (stop != null) {
                nestedFragmentCallback.setActionButtonVisible(
                    isAllChecked(),
                    stop.getTask() == RouteStopTask.PICKUP ? R.string.action_at_picked_up : R.string.action_at_delivered
                );
            }
        });


        if (TextUtils.isEmpty(routeStopActivity.getDescription())) {
            viewHolder.layoutDetails.setVisibility(View.GONE);
        } else {
            viewHolder.txtDescription.setText(routeStopActivity.getDescription());
            viewHolder.layoutDetails.setVisibility(View.VISIBLE);
        }
        viewHolder.reportIssueView.setVisibility(View.GONE);

        if (routeStopActivity.getType() != null) {
            switch (routeStopActivity.getType()) {
                case PICKUP:
                    viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_pick_up, routeStopActivity.getValue()));
                    viewHolder.imageSelected.setImageResource(R.drawable.icon_collect_order_dark);
                    setNotAdditionalInfoActionLayout(viewHolder);
                    break;
                case DELIVER:
                    viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_deliver, routeStopActivity.getValue()));
                    viewHolder.imageSelected.setImageResource(R.drawable.icon_deliver_order_dark);
                    setNotAdditionalInfoActionLayout(viewHolder);
                    break;
                case PAY_RESTAURANT:
                    viewHolder.imageSelected.setImageResource(R.drawable.icon_pay_restaurant);
                    viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_pay, getFormattedPrice(routeStopActivity)));
                    setNotAdditionalInfoActionLayout(viewHolder);
                    break;
                case PREPARE_CHANGE:
                    viewHolder.imageSelected.setImageResource(R.drawable.icon_pay_restaurant);
                    viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_change));
                    setPrepareChangeDescription(viewHolder, routeStopActivity);
                    setNotAdditionalInfoActionLayout(viewHolder);
                    break;
                case COLLECT:
                    viewHolder.imageSelected.setImageResource(R.drawable.icon_collect_money_dark);
                    viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_collect, getFormattedPrice(routeStopActivity)));
                    viewHolder.reportIssueView.setVisibility(View.VISIBLE);
                    viewHolder.reportIssueView.setOnClickListener(view -> showCollectionIssueWarningDialog());
                    setNotAdditionalInfoActionLayout(viewHolder);
                    break;
                case HALAL:
                case NON_HALAL:
                case PREORDER:
                    setAdditionalInfoActionLayout(viewHolder, routeStopActivity);
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
    }

    /**
     * show dialog to let rider know that this issue will initiate investigation
     * if he agrees we show collection issue dialog
     */
    private void showCollectionIssueWarningDialog() {
        nestedFragmentCallback.openInformationDialog(
            context.getString(R.string.issue_collection_warning_dialog_title),
            context.getString(R.string.issue_collection_warning_dialog_text),
            context.getString(R.string.issue_collection_warning_dialog_ok), DialogType.ISSUE_COLLECTION_WARNING);
    }

    /**
     * For the PREPARE_CHANGE type of activity we need to add custom description
     * with details about money
     *
     * @param viewHolder        holder for the change value
     * @param routeStopActivity PREPARE_CHANGE activity to get value of
     */
    private void setPrepareChangeDescription(ViewHolder viewHolder, @NonNull RouteStopActivity routeStopActivity) {
        if (!TextUtils.isEmpty(routeStopActivity.getValue())) {
            viewHolder.layoutDetails.setVisibility(View.VISIBLE);
            viewHolder.txtDescription.setText(
                context.getResources().getString(
                    R.string.route_action_change_description,
                    getFormattedPrice(routeStopActivity)));
        }
    }

    /**
     * Set data for next and last step for
     * with arrow, expandable view and labels
     * add preview content to the {@link com.foodpanda.urbanninja.ui.fragments.MapAddressDetailsFragment}
     *
     * @param viewHolder last, next step layout container
     */
    private void setDataForNextLastStep(ViewHolderHeaderFooter viewHolder) {
        //get last of next step depend on position of the view
        Stop stop = isHeaderView(viewHolder) ? this.stop : storageManager.getNextStop();

        viewHolder.txtName.setText(context.getResources().getString(R.string.task_details_go_to, stop.getName()));

        int stringRes = isHeaderView(viewHolder) ? R.string.route_action_last_step : R.string.route_action_up_step;
        viewHolder.txtType.setText(stringRes);

        viewHolder.expandableLayout.setOnClickListener(v ->
            expandLayout(viewHolder.expandableLayout, viewHolder.imageView, viewHolder.getAdapterPosition()));

        //to make pre-view unique and to not replace it with last or next step
        //we need to set custom id for each type of view
        viewHolder.stepFrameLayout.setId(isHeaderView(viewHolder) ? R.id.last_step : R.id.next_step);

        //add fragment with pre-view content
        showMapAddressCallback.showNextPreviousStep(stop, viewHolder.stepFrameLayout.getId());
        //add warning message for multi-pickup
        addMultiPickupWarningLayout(viewHolder);
    }

    /**
     * add layout below header to add warning message with information
     * about multi pickup and order codes
     *
     * @param viewHolder container for warning layout
     */
    private void addMultiPickupWarningLayout(ViewHolderHeaderFooter viewHolder) {
        MultiPickupManager multiPickupManager = new MultiPickupManager(storageManager);
        if (isHeaderView(viewHolder) && multiPickupManager.isNotEmptySamePlacePickUpStops(stop)) {
            View view = View.inflate(context, R.layout.route_stop_additional_details_layout, null);

            TextView txtAdditionalName = (TextView) view.findViewById(R.id.txt_additional_title);
            TextView txtAdditionalDescription = (TextView) view.findViewById(R.id.txt_additional_description);

            txtAdditionalName.setText(R.string.multi_pickup_alert_title);
            txtAdditionalDescription.setText(multiPickupManager.getMultiPickUpDetailsSting(context, stop));

            viewHolder.warningLayout.addView(view, setLayoutParams());
        }
    }

    /**
     * Set margin for warning card
     * We are using the same layout for warning messages here and in MapAddressDetailsFragment
     * and we need margin only here.
     *
     * @return layout param with margin
     */
    private FrameLayout.LayoutParams setLayoutParams() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(
            context.getResources().getDimensionPixelSize(R.dimen.margin_small),
            context.getResources().getDimensionPixelSize(R.dimen.margin_small),
            context.getResources().getDimensionPixelSize(R.dimen.margin_small),
            context.getResources().getDimensionPixelSize(R.dimen.margin_small));

        return layoutParams;
    }

    /**
     * Detect adapter position
     * for the header view return true
     *
     * @param viewHolder view which position we need to check
     * @return true if this is header view last step in our case
     */
    private boolean isHeaderView(ViewHolderHeaderFooter viewHolder) {
        return viewHolder.getAdapterPosition() == 0;
    }

    /**
     * set default layout background color and header view state for all task
     * not related to halal preOrder or big order
     *
     * @param viewHolder container for action
     */
    private void setNotAdditionalInfoActionLayout(ViewHolder viewHolder) {
        viewHolder.halalHeaderView.setVisibility(View.GONE);
        viewHolder.contentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.main_background_color));
    }

    /**
     * set layout background color and header view state for halal and not-halal,
     * preOrder RouteStopActivityType tasks
     * moreover set name for action section
     *
     * @param viewHolder        container for action
     * @param routeStopActivity route stop action related to additional info
     */
    private void setAdditionalInfoActionLayout(ViewHolder viewHolder, RouteStopActivity routeStopActivity) {
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
                viewHolder.imageSelected.setImageResource(R.drawable.icon_alert_orange);
                viewHolder.txtName.setText(context.getResources().getString(R.string.route_action_not_halal));
                viewHolder.txtDescription.setText(context.getResources().getString(R.string.route_action_not_halal_description));
                break;
            case PREORDER:
                viewHolder.contentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.not_halal_background_color));
                viewHolder.halalHeaderView.setBackgroundColor(ContextCompat.getColor(context, R.color.warning_text_color));
                viewHolder.imageSelected.setImageResource(R.drawable.icon_alert_red);
                viewHolder.txtName.setText(FormatUtil.getPreOrderValue(routeStopActivity.getValue(), context));
                viewHolder.txtDescription.setText(context.getResources().getString(R.string.route_action_pre_order_adapter_description));
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
        ImageView imageView,
        int position
    ) {

        expandableLayout.toggleExpansion();
        int imageResource;
        int backgroundResource;
        if (expandableLayout.isExpanded()) {
            imageResource = R.drawable.icon_deliver_down;
            backgroundResource = R.drawable.list_item_stop_header_not_selected;
        } else {
            imageResource = R.drawable.icon_deliver_up;
            backgroundResource = R.drawable.list_item_stop_header_selected;
            scrollToEndOfFooterView(expandableLayout, position);
        }
        imageView.setImageDrawable(ContextCompat.getDrawable(context, imageResource));
        expandableLayout.setBackgroundResource(backgroundResource);
    }

    /**
     * Scroll to the end of the view to show all content
     * Only footer view should be animated
     *
     * @param expandableLayout layout that should be animated
     * @param position         position of item to know is to last one
     */
    private void scrollToEndOfFooterView(ExpandableLayout expandableLayout, int position) {
        if (position == getItemCount() - 1) {
            expandableLayout.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
                @Override
                public void onToggle(ExpandableLayout view, View child, boolean isExpanded) {
                    recyclerView.smoothScrollBy(0, child.getMeasuredHeight());
                }

                @Override
                public void onExpandOffset(ExpandableLayout view, View child, float offset, boolean isExpanding) {

                }
            });
        }
    }

    /**
     * Set type depend on position
     * we have three types of view header, footer and regular item
     *
     * @param position position of the view
     * @return type of view
     */
    @Override
    public int getItemViewType(int position) {
        int type = TYPE_ITEM;
        //if the position is first one or the last one and we have more stops to do we show
        //last or next step layout with expandable map inside
        if (position == 0 || (position == getItemCount() - 1 && storageManager.hasNextStop())) {
            type = TYPE_HEADER_OR_FOOTER;
        }

        return type;
    }

    private String getFormattedPrice(RouteStopActivity routeStopActivity) {
        return FormatUtil.getValueWithCurrencySymbol(storageManager.getCountry(), routeStopActivity.getValue());
    }

    private class ViewHolderHeaderFooter extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtType;
        public TextView txtName;
        public ExpandableLayout expandableLayout;
        public ImageView imageView;
        public FrameLayout stepFrameLayout;
        public FrameLayout warningLayout;

        public ViewHolderHeaderFooter(View view) {
            super(view);
            txtType = (TextView) view.findViewById(R.id.txt_type);
            txtName = (TextView) view.findViewById(R.id.txt_name);
            expandableLayout = (ExpandableLayout) view.findViewById(R.id.expand_layout);
            imageView = (ImageView) view.findViewById(R.id.image_arrow);
            stepFrameLayout = (FrameLayout) view.findViewById(R.id.step_layout);
            warningLayout = (FrameLayout) view.findViewById(R.id.warning_layout);
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
        public View reportIssueView;

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
            reportIssueView = view.findViewById(R.id.txt_issue);
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
     * however in case when we don't have next step we only should add header
     *
     * @return count of ALL items in a recycler view with header and footer
     */
    @Override
    public int getItemCount() {
        return objects.size() + (storageManager.hasNextStop() ? 2 : 1);
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
