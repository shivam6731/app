package com.foodpanda.urbanninja.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.utils.DateUtil;

import java.util.List;

public class ScheduleAdapter extends SimpleBaseAdapter<ScheduleWrapper, ScheduleAdapter.ViewHolder> {

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
        if (scheduleWrapper.getStartingPoint() != null) {
            holder.txtScheduleStartName.setText(scheduleWrapper.getStartingPoint().getName());
            holder.txtScheduleStartAddress.setText(scheduleWrapper.getStartingPoint().getDescription());
        }
        if (scheduleWrapper.getTimeWindow() != null) {
            holder.txtScheduleStartTime.setText(DateUtil.formatTimeHoursMinutes(scheduleWrapper.getTimeWindow().getStartAt()));
            holder.txtScheduleEndTime.setText(DateUtil.formatTimeHoursMinutes(scheduleWrapper.getTimeWindow().getEndAt()));
            holder.txtScheduleStartDate.setText(DateUtil.formatTimeWeekDayDateMonth(scheduleWrapper.getTimeWindow().getStartAt()));
        }
    }

    public class ViewHolder extends SimpleBaseAdapter.BaseViewHolder {
        public TextView txtScheduleStartDate;
        public TextView txtScheduleStartName;
        public TextView txtScheduleStartAddress;
        public TextView txtScheduleStartTime;
        public TextView txtScheduleEndTime;

        public ViewHolder(View itemView) {
            super(itemView);
            txtScheduleStartDate = (TextView) itemView.findViewById(R.id.txt_schedule_start_date);
            txtScheduleStartName = (TextView) itemView.findViewById(R.id.txt_schedule_start_name);
            txtScheduleStartAddress = (TextView) itemView.findViewById(R.id.txt_schedule_start_address);
            txtScheduleStartTime = (TextView) itemView.findViewById(R.id.txt_schedule_start_time);
            txtScheduleEndTime = (TextView) itemView.findViewById(R.id.txt_schedule_end_time);
        }
    }

}

