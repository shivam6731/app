package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.CashReport;
import com.foodpanda.urbanninja.utils.DateUtil;
import com.foodpanda.urbanninja.utils.FormatUtil;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

public class CashReportAdapter extends SimpleBaseAdapter<CashReport, SimpleBaseAdapter.BaseViewHolder>
    implements StickyRecyclerHeadersAdapter<CashReportAdapter.ViewHeaderHolder> {
    /**
     * Item type for footer
     * should be different from 0 (default value for {@link RecyclerView.Adapter})
     */
    private static final int FOOTER_VIEW_TYPE = 1;
    private final StorageManager storageManager;

    public CashReportAdapter(List<CashReport> items, Context context) {
        super(items, context);
        storageManager = App.STORAGE_MANAGER;
    }

    @Override
    public SimpleBaseAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        SimpleBaseAdapter.BaseViewHolder baseViewHolder;
        switch (viewType) {
            case FOOTER_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cash_report_footer, parent, false);
                baseViewHolder = new ViewFooterHolder(view);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cash_report, parent, false);
                baseViewHolder = new ViewMainContentHolder(view);
                break;
        }

        return baseViewHolder;
    }

    @Override
    public ViewHeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cash_report_header, parent, false);

        return new ViewHeaderHolder(view);
    }

    @Override
    public long getHeaderId(int position) {
        if (!objects.isEmpty() && position < objects.size()) {
            return getItem(position).getDateTime().withTimeAtStartOfDay().getMillis();
        } else {
            return -1;
        }
    }

    /**
     * Set type of view that should be shown
     * for the last item it would be {@value #FOOTER_VIEW_TYPE}
     * for all others it would be default value
     *
     * @param position position of view in the list
     * @return type
     */
    @Override
    public int getItemViewType(int position) {
        if (position == objects.size()) {
            return FOOTER_VIEW_TYPE;
        }

        return super.getItemViewType(position);
    }

    /**
     * Here we have two types of view holders
     * on for regular content another for footer
     *
     * @param holder   base holder with both types
     * @param position position of view in the list
     */
    @Override
    public void onBindViewHolder(SimpleBaseAdapter.BaseViewHolder holder, int position) {
        //if type is footer we set the footer value
        if (holder instanceof ViewFooterHolder) {
            ViewFooterHolder viewHeaderHolder = (ViewFooterHolder) holder;

            viewHeaderHolder.txtTotal.setText(setBrushedTotalValue(objects.size() - 1));
            //if type is main content we set the order value, such as code, name and value
        } else if (holder instanceof ViewMainContentHolder) {
            ViewMainContentHolder viewMainContentHolder = (ViewMainContentHolder) holder;

            CashReport cashReport = getItem(position);
            if (cashReport.isCanceled()) {
                viewMainContentHolder.txtName.setText(context.getString(R.string.cash_report_cancelled));
                viewMainContentHolder.txtName.setTextColor(
                    ContextCompat.getColor(context, R.color.snackbar_background));
            } else {
                viewMainContentHolder.txtName.setText(cashReport.getName());
                viewMainContentHolder.txtName.setTextColor(
                    ContextCompat.getColor(context, R.color.primary_text_color));
            }
            viewMainContentHolder.txtCode.setText(cashReport.getCode());
            viewMainContentHolder.txtValue.setText(
                FormatUtil.getValueWithCurrencySymbolFromNumber(
                    storageManager.getCountry(), cashReport.getValue()));
            viewMainContentHolder.viewDivider.setVisibility(
                cashReport.isLastAction() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onBindHeaderViewHolder(ViewHeaderHolder holder, int position) {
        CashReport cashReport = getItem(position);

        if (cashReport.getDateTime() != null) {
            holder.txtDate.setText(
                DateUtil.formatTimeWeekDayDateMonthYear(cashReport.getDateTime().withTimeAtStartOfDay()));
        }
        if (position != 0) {
            holder.txtTotal.setText(setBrushedTotalValue(position - 1));
            holder.layoutTotal.setVisibility(View.VISIBLE);
        } else {
            holder.layoutTotal.setVisibility(View.GONE);
        }
    }

    /**
     * add one more item in a list for footer view
     * if only this list is not empty
     * otherwise empty list would be shown
     *
     * @return count of items + one more item for footer
     */
    @Override
    public int getItemCount() {
        return super.getItemCount() == 0 ? super.getItemCount() : super.getItemCount() + 1;
    }

    private Spannable setBrushedTotalValue(int position) {
        double total = getTotalOfTheDay(position);
        String valueWithCurrencySymbol = FormatUtil.getValueWithCurrencySymbolFromNumber(
            storageManager.getCountry(), total);

        Spannable wordToSpan = new SpannableString(
            context.getResources()
                .getString(R.string.cash_report_total, valueWithCurrencySymbol)
        );

        int color = ContextCompat.getColor(context, total > 0 ? R.color.colorAccent : R.color.snackbar_background);
        //Label of the text would be default color
        //and value would be green if it's  positive or red if negative
        wordToSpan.setSpan(
            new ForegroundColorSpan(color),
            context.getResources().getString(R.string.cash_report_total_label).length(),
            wordToSpan.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return wordToSpan;
    }

    /**
     * Count the sum of the values for each day
     *
     * @param position position in the list between two days
     * @return the sum of values for the whole day
     */
    double getTotalOfTheDay(int position) {
        double total = 0;
        if (objects.isEmpty()) {
            return total;
        }
        long firstTime = objects.get(position).getDateTime().withTimeAtStartOfDay().getMillis();
        for (int i = position; i > 0; i--) {
            if (firstTime == objects.get(i).getDateTime().withTimeAtStartOfDay().getMillis()) {
                total += objects.get(i).getValue();
            }
        }

        return total;
    }

    class ViewMainContentHolder extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtCode;
        public TextView txtName;
        public TextView txtValue;
        public View viewDivider;

        public ViewMainContentHolder(View itemView) {
            super(itemView);
            txtCode = (TextView) itemView.findViewById(R.id.txt_cash_report_code);
            txtName = (TextView) itemView.findViewById(R.id.txt_cash_report_name);
            txtValue = (TextView) itemView.findViewById(R.id.txt_cash_report_value);
            viewDivider = itemView.findViewById(R.id.divider_cash_report);
        }
    }

    class ViewHeaderHolder extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtTotal;
        public TextView txtDate;
        public View layoutTotal;

        public ViewHeaderHolder(View itemView) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.txt_header_title);
            txtTotal = (TextView) itemView.findViewById(R.id.txt_header_total);
            layoutTotal = itemView.findViewById(R.id.layout_total);
        }
    }

    class ViewFooterHolder extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtTotal;

        public ViewFooterHolder(View itemView) {
            super(itemView);
            txtTotal = (TextView) itemView.findViewById(R.id.txt_header_total);
        }
    }

}
