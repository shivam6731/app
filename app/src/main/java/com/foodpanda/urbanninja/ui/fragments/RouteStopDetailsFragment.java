package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.MapPointType;
import com.foodpanda.urbanninja.model.enums.RouteStopTaskStatus;
import com.foodpanda.urbanninja.ui.interfaces.MapAddressDetailsCallback;
import com.foodpanda.urbanninja.ui.interfaces.MapAddressDetailsChangeListener;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.interfaces.TimerDataProvider;
import com.foodpanda.urbanninja.ui.util.TimerHelper;

import org.joda.time.DateTime;

public class RouteStopDetailsFragment extends BaseFragment implements
    TimerDataProvider,
    MapAddressDetailsChangeListener,
    MapAddressDetailsCallback {

    private NestedFragmentCallback nestedFragmentCallback;
    private TimerHelper timerHelper;

    private TextView txtType;
    private TextView txtTimer;

    private Stop stop;

    private MapAddressDetailsChangeListener mapAddressDetailsChangeListener;

    public static RouteStopDetailsFragment newInstance(Stop stop) {
        RouteStopDetailsFragment routeStopDetailsFragment = new RouteStopDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.STOP, stop);
        routeStopDetailsFragment.setArguments(bundle);

        return routeStopDetailsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        nestedFragmentCallback = (NestedFragmentCallback) getParentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stop = getArguments().getParcelable(Constants.BundleKeys.STOP);
        timerHelper = new TimerHelper(activity, this, this);
    }

    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.route_stop_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtType = (TextView) view.findViewById(R.id.txt_type);
        txtTimer = (TextView) view.findViewById(R.id.txt_timer);

        final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // this fragment in a child view for some main container with
                // swipe to refresh logic inside, however here we have own scroll and to get rid of scroll conflict
                // we make main swipe view disable until we reach the top of the view here
                if (nestedFragmentCallback != null) {
                    nestedFragmentCallback.setSwipeToRefreshEnable(scrollView.getScrollY() == 0);
                }
            }
        });

        setData();
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
    public void onDestroy() {
        super.onDestroy();
        enableSwipeToRefreshDeleteCallback();
    }

    /**
     * Here we set all information about the current route stop for the text field
     * we have the same text view for the address and comment so we need to check if this data
     * present before set it
     */
    private void setData() {
        setType(stop.getTask());
        //Launch the map details fragment
        MapAddressDetailsFragment mapAddressDetailsFragment = MapAddressDetailsFragment.newInstance(
            stop,
            stop.getTask() == RouteStopTaskStatus.DELIVER ? MapPointType.DELIVERY : MapPointType.PICK_UP,
            true);

        mapAddressDetailsChangeListener = mapAddressDetailsFragment;

        fragmentManager.
            beginTransaction().
            replace(R.id.map_details_container,
                mapAddressDetailsFragment).
            commitAllowingStateLoss();
    }

    /**
     * Put the icon and description for type textView
     *
     * @param task type of order
     */
    private void setType(RouteStopTaskStatus task) {
        int textResource = task == RouteStopTaskStatus.PICKUP ? R.string.task_details_pick_up : R.string.task_details_delivery;
        txtType.setText(getResources().getText(textResource));

        int iconResource = task == RouteStopTaskStatus.PICKUP ? R.drawable.icon_restaurant_green : R.drawable.icon_deliver_green;
        txtType.setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, 0, 0);
    }

    /**
     * Main main container swipe view enable and delete callback
     * We need to delete callback because {@link ViewTreeObserver.OnScrollChangedListener}
     * calls after {@link #onDestroy()} called and it means that we would have a wrong state for swipe view
     */
    private void enableSwipeToRefreshDeleteCallback() {
        if (nestedFragmentCallback != null) {
            nestedFragmentCallback.setSwipeToRefreshEnable(true);
            nestedFragmentCallback = null;
        }
    }

    @Override
    public TextView provideTimerTextView() {
        return txtTimer;
    }

    @Override
    public DateTime provideScheduleDate() {
        return stop.getArrivalTime();
    }

    @Override
    public DateTime provideScheduleEndDate() {
        return stop.getArrivalTime().plusDays(1);
    }

    @Override
    public int provideActionButtonString() {
        return 0;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mapAddressDetailsChangeListener != null) {
            mapAddressDetailsChangeListener.onLocationChanged(location);
        }
    }

    @Override
    public void setActionDoneCheckboxVisibility(boolean isVisible) {
        if (mapAddressDetailsChangeListener != null) {
            mapAddressDetailsChangeListener.setActionDoneCheckboxVisibility(isVisible);
        }
    }

    @Override
    public void setActionButtonVisible(boolean isVisible) {
        nestedFragmentCallback.setActionButtonVisible(isVisible);
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
