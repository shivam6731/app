package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.utils.DateUtil;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

public class ScheduleAdapter extends SimpleBaseAdapter<ScheduleWrapper, ScheduleAdapter.ViewHolder>
    implements StickyRecyclerHeadersAdapter<ScheduleAdapter.ViewHeaderHolder> {

    public ScheduleAdapter(List<ScheduleWrapper> objects, Context context) {
        super(objects, context);
    }

    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_schedule, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScheduleWrapper scheduleWrapper = getItem(position);
        if (scheduleWrapper.getDeliveryZone() != null && scheduleWrapper.getDeliveryZone().getStartingPoint() != null) {
            holder.txtScheduleStartName.setText(scheduleWrapper.getDeliveryZone().getStartingPoint().getName());
            holder.txtScheduleStartAddress.setText(scheduleWrapper.getDeliveryZone().getStartingPoint().getAddress());
        }
        if (scheduleWrapper.getTimeWindow() != null) {
            holder.txtScheduleWorkingHours.setText(
                context.getResources().getString(R.string.schedule_list_working_hours,
                    DateUtil.formatTimeHoursMinutes(scheduleWrapper.getTimeWindow().getStartAt()),
                    DateUtil.formatTimeHoursMinutes(scheduleWrapper.getTimeWindow().getEndAt())));

            DateUtil.setHoursMinutes(
                holder.txtScheduleDurationHour,
                holder.txtScheduleDurationMinutes,
                scheduleWrapper.getTimeWindow());
        }
    }

    @Override
    public long getHeaderId(int position) {
        if (!objects.isEmpty() &&
            getItem(position).getTimeWindow() != null &&
            getItem(position).getTimeWindow().getStartAt() != null) {
            return getItem(position).getTimeWindow().getStartAt().withTimeAtStartOfDay().getMillis();
        } else {
            return -1;
        }
    }

    @Override
    public ViewHeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_schedule_header, parent, false);

        return new ViewHeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(ViewHeaderHolder holder, int position) {
        ScheduleWrapper scheduleWrapper = getItem(position);

        if (scheduleWrapper.getTimeWindow() != null && scheduleWrapper.getTimeWindow().getStartAt() != null) {
            holder.txtDate.setText(
                DateUtil.formatScheduleTimeWeekDayDateMonth(
                    scheduleWrapper.getTimeWindow().getStartAt(), context));
        }
    }

    public class ViewHolder extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtScheduleDurationHour;
        public TextView txtScheduleDurationMinutes;

        public TextView txtScheduleWorkingHours;
        public TextView txtScheduleStartName;
        public TextView txtScheduleStartAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            txtScheduleDurationHour = (TextView) itemView.findViewById(R.id.txt_schedule_duration_hours);
            txtScheduleDurationMinutes = (TextView) itemView.findViewById(R.id.txt_schedule_duration_minutes);
            txtScheduleWorkingHours = (TextView) itemView.findViewById(R.id.txt_schedule_working_hours);
            txtScheduleStartName = (TextView) itemView.findViewById(R.id.txt_schedule_start_name);
            txtScheduleStartAddress = (TextView) itemView.findViewById(R.id.txt_schedule_start_address);
        }
    }

    class ViewHeaderHolder extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtDate;

        public ViewHeaderHolder(View itemView) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.txt_schedule_header_date);
        }
    }

}

