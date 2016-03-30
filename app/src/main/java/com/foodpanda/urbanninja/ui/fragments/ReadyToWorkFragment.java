package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.interfaces.TimerDataProvider;
import com.foodpanda.urbanninja.ui.util.TimerHelper;

import org.joda.time.DateTime;

public class ReadyToWorkFragment extends BaseFragment implements TimerDataProvider {
    private TextView txtStartPoint;
    private TextView txtTimer;
    private TextView txtTimerDescription;

    private ScheduleWrapper scheduleWrapper;

    private NestedFragmentCallback nestedFragmentCallback;
    private TimerHelper timerHelper;

    public static ReadyToWorkFragment newInstance(ScheduleWrapper scheduleWrapper) {
        ReadyToWorkFragment readyToWorkFragment = new ReadyToWorkFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.SCHEDULE_WRAPPER, scheduleWrapper);
        readyToWorkFragment.setArguments(bundle);

        return readyToWorkFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        nestedFragmentCallback = (NestedFragmentCallback) getParentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleWrapper = getArguments().getParcelable(Constants.BundleKeys.SCHEDULE_WRAPPER);
        timerHelper = new TimerHelper(activity, this, this, nestedFragmentCallback);
    }

    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.ready_to_work_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtStartPoint = (TextView) view.findViewById(R.id.txt_start_point);
        txtTimer = (TextView) view.findViewById(R.id.txt_timer);
        txtTimerDescription = (TextView) view.findViewById(R.id.txt_timer_description);

        view.findViewById(R.id.btn_see_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scheduleWrapper.getStartingPoint() != null && scheduleWrapper.getStartingPoint().getGeoCoordinate() != null) {
                    String pinLabel = scheduleWrapper.getStartingPoint().getName();
                    nestedFragmentCallback.onSeeMapClicked(scheduleWrapper.getStartingPoint().getGeoCoordinate(), pinLabel);
                }
            }
        });
        if (scheduleWrapper.getStartingPoint() == null) {
            view.findViewById(R.id.btn_see_map).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        timerHelper.setTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        timerHelper.stopTimer();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (scheduleWrapper.getStartingPoint() != null) {
            txtStartPoint.setText(scheduleWrapper.getStartingPoint().getDescription());
        } else {
            txtStartPoint.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public TextView provideTimerTextView() {
        return txtTimer;
    }

    @Override
    public TextView provideTimerDescriptionTextView() {
        return txtTimerDescription;
    }

    @Override
    public DateTime provideScheduleDate() {
        if (scheduleWrapper.getTimeWindow() != null) {

            return scheduleWrapper.getTimeWindow().getStartAt();
        } else {

            return new DateTime().plusDays(2);
        }
    }

    @Override
    public DateTime provideScheduleEndDate() {
        if (scheduleWrapper.getTimeWindow() != null) {

            return scheduleWrapper.getTimeWindow().getEndAt();
        } else {

            return new DateTime().plusDays(2);
        }
    }

    @Override
    public String provideLeftString() {
        return getResources().getString(R.string.ready_to_work_time_left);
    }

    @Override
    public String providePassedString() {
        return getResources().getString(R.string.ready_to_work_time_passed);
    }

    @Override
    public int provideActionButtonString() {
        return R.string.action_ready_to_work;
    }

    @Override
    public String provideExpireString() {
        return getResources().getString(R.string.action_ready_shift_expired);
    }

    @Override
    public String provideFutureString() {
        return getResources().getString(R.string.action_ready_no_shift);
    }
}
