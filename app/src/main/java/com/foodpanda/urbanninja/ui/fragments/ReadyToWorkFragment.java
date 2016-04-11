package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.enums.MapPointType;
import com.foodpanda.urbanninja.ui.interfaces.MapAddressDetailsCallback;
import com.foodpanda.urbanninja.ui.interfaces.MapAddressDetailsChangeListener;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.interfaces.TimerDataProvider;
import com.foodpanda.urbanninja.ui.util.TimerHelper;

import org.joda.time.DateTime;

public class ReadyToWorkFragment extends BaseFragment implements
    TimerDataProvider,
    MapAddressDetailsChangeListener,
    MapAddressDetailsCallback {
    private TextView txtType;
    private TextView txtTimer;
    private TextView txtEmptySchedule;

    private ScheduleWrapper scheduleWrapper;

    private NestedFragmentCallback nestedFragmentCallback;
    private MapAddressDetailsChangeListener mapAddressDetailsChangeListener;
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
        txtType = (TextView) view.findViewById(R.id.txt_type);
        txtTimer = (TextView) view.findViewById(R.id.txt_timer);
        txtEmptySchedule = (TextView) view.findViewById(R.id.txt_empty_schedule);
        setData();
    }

    /**
     * Here we set all information about the current route stop for the text field
     * we have the same text view for the address and comment so we need to check if this data
     * present before set it
     */
    private void setData() {
        setType();

        if (scheduleWrapper.getDeliveryZone() != null && scheduleWrapper.getDeliveryZone().getStartingPoint() != null) {
            txtEmptySchedule.setVisibility(View.GONE);

            //Launch the map details fragment
            MapAddressDetailsFragment mapAddressDetailsFragment = MapAddressDetailsFragment.newInstance(
                scheduleWrapper.getDeliveryZone().getStartingPoint(), MapPointType.CLOCK_IN);
            mapAddressDetailsChangeListener = mapAddressDetailsFragment;

            fragmentManager.
                beginTransaction().
                replace(R.id.map_details_container,
                    mapAddressDetailsFragment).
                commitAllowingStateLoss();
        } else {
            txtEmptySchedule.setVisibility(View.VISIBLE);
        }
        
    }

    /**
     * Put the icon and description for type textView
     */
    private void setType() {
        txtType.setText(getResources().getText(R.string.ready_to_work_clock_in));
        txtType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_start_green, 0, 0, 0);
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
    public TextView provideTimerTextView() {
        return txtTimer;
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
    public int provideActionButtonString() {
        return R.string.action_ready_to_work;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mapAddressDetailsChangeListener != null) {
            mapAddressDetailsChangeListener.onLocationChanged(location);
        }
    }

    @Override
    public void setActionDoneCheckboxVisibility(boolean isVisible) {
        mapAddressDetailsChangeListener.setActionDoneCheckboxVisibility(false);
    }

    @Override
    public void setActionButtonEnable(boolean isEnable) {
        nestedFragmentCallback.setActionButtonEnable(true);
    }

    @Override
    public void onSeeMapClicked(GeoCoordinate geoCoordinate, String pinLabel) {
        nestedFragmentCallback.onSeeMapClicked(geoCoordinate, pinLabel);
    }

    @Override
    public void onPhoneNumberClicked(String phoneNumber) {
        nestedFragmentCallback.onPhoneNumberClicked(phoneNumber);
    }
}
