package com.foodpanda.urbanninja.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.RouteStopTaskStatus;
import com.foodpanda.urbanninja.ui.adapter.RouteStopActionAdapter;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.interfaces.TimerDataProvider;
import com.foodpanda.urbanninja.ui.util.TimerHelper;

import org.joda.time.DateTime;

public class RouteStopActionListFragment extends BaseListFragment<RouteStopActionAdapter>
    implements TimerDataProvider {
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
        nestedFragmentCallback.setActionButtonEnable(false);

        txtTimer = (TextView) view.findViewById(R.id.txt_timer);

        TextView txtType = (TextView) view.findViewById(R.id.txt_type);
        setType(currentStop.getTask(), txtType);
    }

    @Override
    protected RouteStopActionAdapter provideListAdapter() {
        return new RouteStopActionAdapter(currentStop, activity, nestedFragmentCallback,recyclerView);
    }

    @Override
    protected int provideListLayout() {
        return R.layout.route_stop_action_list_fragment;
    }

    /**
     * Put the icon and description for type textView
     *
     * @param task type of order
     */
    private void setType(RouteStopTaskStatus task, TextView txtType) {
        int textResource = task == RouteStopTaskStatus.PICKUP ? R.string.route_action_pick_up : R.string.route_action_deliver;
        txtType.setText(activity.getResources().getText(textResource));

        int iconResource = task == RouteStopTaskStatus.PICKUP ? R.drawable.icon_restaurant_green : R.drawable.icon_deliver_green;
        txtType.setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, 0, 0);
    }

    @Override
    protected String provideEmptyListDescription() {
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

}
