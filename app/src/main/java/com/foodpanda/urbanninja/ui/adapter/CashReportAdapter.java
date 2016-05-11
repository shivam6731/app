package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.OrderReport;
import com.foodpanda.urbanninja.model.OrderStop;
import com.foodpanda.urbanninja.model.WorkingDay;
import com.foodpanda.urbanninja.model.enums.Status;
import com.foodpanda.urbanninja.utils.DateUtil;
import com.foodpanda.urbanninja.utils.FormatUtil;

import java.util.List;

public class CashReportAdapter extends ExpandableRecyclerAdapter<CashReportAdapter.ViewHolderHeader, CashReportAdapter.ViewHolderItem> {

    private final StorageManager storageManager;
    private Context context;


    public CashReportAdapter(List<WorkingDay> items, Context context) {
        super(items);
        this.context = context;
        storageManager = App.STORAGE_MANAGER;
    }

    @Override
    public ViewHolderHeader onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View view = LayoutInflater.from(parentViewGroup.getContext()).inflate(R.layout.list_item_cash_report_header, parentViewGroup, false);

        return new ViewHolderHeader(view);
    }

    @Override
    public ViewHolderItem onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view = LayoutInflater.from(childViewGroup.getContext()).inflate(R.layout.list_item_cash_report, childViewGroup, false);

        return new ViewHolderItem(view);
    }

    @Override
    public void onBindParentViewHolder(ViewHolderHeader parentViewHolder, int position, ParentListItem parentListItem) {
        WorkingDay workingDay = (WorkingDay) parentListItem;
        parentViewHolder.txtDate.setText(DateUtil.formatTimeDayMonthYear(workingDay.getDate()));
        parentViewHolder.txtTotal.setText(context.getString(R.string.cash_report_total,
            FormatUtil.getValueWithCurrencySymbolFromNumber(
                storageManager.getCountry(), workingDay.getTotal())));
        parentViewHolder.txtTotal.setTextColor(
            ContextCompat.getColor(context, workingDay.getTotal() >= 0 ? R.color.green_text_color : R.color.warnining_text_color))
        ;
    }

    @Override
    public void onBindChildViewHolder(ViewHolderItem childViewHolder, int position, Object childListItem) {
        OrderReport orderReport = (OrderReport) childListItem;

        childViewHolder.txtCode.setText(orderReport.getCode());
        childViewHolder.reportStepsLayout.removeAllViews();

        //each order step should be set in separate layout to be able
        //to the information for orders with more then two steps
        for (OrderStop orderStop : orderReport.getOrderStops()) {
            drawOrderSteps(childViewHolder.reportStepsLayout, orderStop);
        }
    }

    /**
     * set order step details information to the separate layout
     * with order type is image
     * order name and value of money that was earned or paid
     *
     * @param reportStepsLayout root layout where data would be drawn
     * @param orderStop         step object that should be set
     */
    private void drawOrderSteps(LinearLayout reportStepsLayout, OrderStop orderStop) {
        View view = View.inflate(context, R.layout.list_item_cash_report_step, null);

        TextView txtName = (TextView) view.findViewById(R.id.txt_cash_report_step_name);
        TextView txtValue = (TextView) view.findViewById(R.id.txt_cash_report_step_value);

        if (!TextUtils.isEmpty(orderStop.getName())) {
            txtName.setText(orderStop.getName());
        }

        if (orderStop.getStatus() == Status.CANCELED) {
            txtName.setText(context.getString(R.string.cash_report_cancelled));
            txtName.setTextColor(ContextCompat.getColor(context, R.color.warnining_text_color));
        }

        setImageForOrderType(orderStop, txtName);
        setValueForOrderStep(orderStop.getValue(), txtValue);

        reportStepsLayout.addView(view);
    }

    /**
     * set image marker for order step
     *
     * @param orderStop order step
     * @param txtName   textView where drawable left would be set
     */
    private void setImageForOrderType(OrderStop orderStop, TextView txtName) {
        int drawableResource = 0;
        switch (orderStop.getTask()) {
            case PICKUP:
                drawableResource = R.drawable.icon_pickup_small_black;
                break;
            case DELIVER:
                drawableResource = R.drawable.icon_deliver_small_black;
                break;
        }
        if (orderStop.getStatus() == Status.CANCELED) {
            drawableResource = R.drawable.icon_cancelled_small_red;
        }

        txtName.setCompoundDrawablesWithIntrinsicBounds(drawableResource, 0, 0, 0);
    }

    /**
     * set value of money to the textView with currency symbol
     * based on rider's country
     *
     * @param orderReportStepValue value of money that should be set
     * @param txtValue             textView for money value
     */
    private void setValueForOrderStep(double orderReportStepValue, TextView txtValue) {
        String valueWithCurrencySymbol = FormatUtil.getValueWithCurrencySymbolFromNumber(
            storageManager.getCountry(), orderReportStepValue);

        txtValue.setText(valueWithCurrencySymbol);
    }

    /**
     * View holder for each order
     */
    class ViewHolderItem extends ChildViewHolder {
        public TextView txtCode;
        public LinearLayout reportStepsLayout;

        public ViewHolderItem(View itemView) {
            super(itemView);
            txtCode = (TextView) itemView.findViewById(R.id.txt_cash_report_code);
            reportStepsLayout = (LinearLayout) itemView.findViewById(R.id.cash_report_steps_layout);
        }
    }

    /**
     * View holder for header item in the list
     */
    class ViewHolderHeader extends ParentViewHolder {
        private static final float INITIAL_POSITION = 0.0f;
        private static final float ROTATED_POSITION = 180f;
        private static final int ANIMATION_DURATION_MILLISECONDS = 200;

        public TextView txtTotal;
        public TextView txtDate;
        private ImageView imageView;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.txt_header_date);
            txtTotal = (TextView) itemView.findViewById(R.id.txt_header_total);
            imageView = (ImageView) itemView.findViewById(R.id.image_cash_report_arrow);
        }

        /**
         * set default value for the arrow image
         *
         * @param expanded is this section is expanded
         */
        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            imageView.setRotation(expanded ? ROTATED_POSITION : INITIAL_POSITION);
        }

        /**
         * animation for the arrow to let user know that we have content inside the section
         * or this section was closed
         *
         * @param expanded is this section is expanded
         */
        @Override
        public void onExpansionToggled(boolean expanded) {
            super.onExpansionToggled(expanded);

            RotateAnimation rotateAnimation = new RotateAnimation(ROTATED_POSITION * (expanded ? 1 : -1),
                INITIAL_POSITION,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

            rotateAnimation.setDuration(ANIMATION_DURATION_MILLISECONDS);
            rotateAnimation.setFillAfter(true);
            imageView.startAnimation(rotateAnimation);
        }

    }

}
