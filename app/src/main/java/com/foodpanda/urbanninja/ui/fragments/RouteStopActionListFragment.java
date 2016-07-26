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
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.MapPointType;
import com.foodpanda.urbanninja.model.enums.RouteStopTask;
import com.foodpanda.urbanninja.ui.adapter.RouteStopActionAdapter;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.interfaces.ShowMapAddressCallback;
import com.foodpanda.urbanninja.ui.interfaces.TimerDataProvider;
import com.foodpanda.urbanninja.ui.util.OrderTypeAndPaymentHelper;
import com.foodpanda.urbanninja.ui.util.TimerHelper;

import org.joda.time.DateTime;

public class RouteStopActionListFragment extends BaseListFragment<RouteStopActionAdapter>
    implements TimerDataProvider,
    ShowMapAddressCallback {
    private NestedFragmentCallback nestedFragmentCallback;
    private TimerHelper timerHelper;

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
        timerHelper = new TimerHelper(activity, this, this);
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
        new OrderTypeAndPaymentHelper(activity, currentStop, App.STORAGE_MANAGER).setType(layoutTypeAndPayment);
    }

    @Override
    protected RouteStopActionAdapter provideListAdapter() {
        return new RouteStopActionAdapter(
            currentStop,
            activity,
            nestedFragmentCallback,
            this);
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
}
