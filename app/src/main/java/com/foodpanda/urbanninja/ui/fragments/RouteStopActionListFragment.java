package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.di.module.TimerHelperAndOrderTypePaymentHelperModule;
import com.foodpanda.urbanninja.manager.MultiPickupManager;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.MapPointType;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.ui.adapter.RouteStopActionAdapter;
import com.foodpanda.urbanninja.ui.interfaces.MapAddressDetailsCallback;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.interfaces.ShowMapAddressCallback;
import com.foodpanda.urbanninja.ui.interfaces.TimerDataProvider;
import com.foodpanda.urbanninja.ui.util.OrderTypeAndPaymentHelper;
import com.foodpanda.urbanninja.ui.util.TimerHelper;

import org.joda.time.DateTime;

import javax.inject.Inject;

public class RouteStopActionListFragment extends BaseListFragment<RouteStopActionAdapter>
    implements TimerDataProvider,
    ShowMapAddressCallback,
    MapAddressDetailsCallback {
    private NestedFragmentCallback nestedFragmentCallback;

    @Inject
    TimerHelper timerHelper;

    @Inject
    StorageManager storageManager;

    @Inject
    MultiPickupManager multiPickupManager;

    @Inject
    OrderTypeAndPaymentHelper orderTypeAndPaymentHelper;

    private TextView txtTimer;

    private Stop currentStop;

    public static RouteStopActionListFragment newInstance(Stop stop) {
        RouteStopActionListFragment fragment = new RouteStopActionListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.STOP, stop);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        nestedFragmentCallback = (NestedFragmentCallback) getParentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentStop = getArguments().getParcelable(Constants.BundleKeys.STOP);
    }

    @Override
    protected void setupComponent() {
        super.setupComponent();
        App.get(getContext()).getMainComponent().plus(new TimerHelperAndOrderTypePaymentHelperModule(activity, this, this)).inject(this);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nestedFragmentCallback.setActionButtonVisible(false);

        txtTimer = (TextView) view.findViewById(R.id.txt_timer);

        //Set payment details and type of the task
        RelativeLayout layoutTypeAndPayment = (RelativeLayout) view.findViewById(R.id.layout_type_payment);
        orderTypeAndPaymentHelper.setType(layoutTypeAndPayment);
        recyclerView.removeItemDecoration(dividerItemDecoration);
    }

    @Override
    protected RouteStopActionAdapter provideListAdapter() {
        return new RouteStopActionAdapter(
            currentStop,
            activity,
            nestedFragmentCallback,
            this,
            recyclerView,
            storageManager,
            multiPickupManager);
    }

    @Override
    protected int provideListLayout() {
        return R.layout.route_stop_action_list_fragment;
    }


    @Override
    protected CharSequence provideEmptyListDescription() {
        return "";
    }

    @Override
    public void onItemClick(View view, int position) {
    }

    @Override
    public TextView provideTimerTextView() {
        return txtTimer;
    }

    @Override
    public DateTime provideScheduleDate() {
        return currentStop.getArrivalTime();
    }

    @Override
    public DateTime provideScheduleEndDate() {
        return currentStop.getArrivalTime().plusDays(1);
    }

    @Override
    public int provideActionButtonString() {
        return 0;
    }

    @Override
    public void showNextPreviousStep(Stop stop, int viewContainerId) {
        MapAddressDetailsFragment mapAddressDetailsFragment = MapAddressDetailsFragment.newInstance(
            stop,
            stop.getTask() == RouteStopTask.DELIVER ? MapPointType.DELIVERY : MapPointType.PICK_UP,
            false,
            false);

        addFragment(viewContainerId, mapAddressDetailsFragment);

    }

    @Override
    public void setActionButtonVisible(boolean isVisible) {

    }

    @Override
    public void onSeeMapClicked(GeoCoordinate geoCoordinate, String pinLabel) {
        nestedFragmentCallback.onSeeMapClicked(geoCoordinate, pinLabel);
    }

    @Override
    public void onPhoneNumberClicked(String phoneNumber) {

    }
}
