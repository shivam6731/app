package com.foodpanda.urbanninja.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.manager.ApiExecutor;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.model.enums.Action;
import com.foodpanda.urbanninja.model.enums.RouteStopTaskStatus;
import com.foodpanda.urbanninja.model.enums.UserStatus;
import com.foodpanda.urbanninja.ui.activity.MainActivity;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;

/**
 * To encapsulate all logic according to current rider's orders in one separate navigation menu item
 * this order wrapper fragment was created.
 * It would be recreated any time when 'orders' menu item was selected in navigation menu except only
 * if this item is current one
 */
public class OrdersNestedFragment extends BaseFragment implements NestedFragmentCallback {
    private MainActivityCallback mainActivityCallback;
    private FragmentManager fragmentManager;
    private Button btnAction;
    private View layoutAction;
    private SwipeRefreshLayout swipeRefreshLayout;

    private UserStatus userStatus = UserStatus.LOADING;
    private ApiExecutor apiExecutor;

    private StorageManager storageManager;

    private int isActionButtonView = View.GONE;

    public static OrdersNestedFragment newInstance() {
        OrdersNestedFragment fragment = new OrdersNestedFragment();

        return fragment;
    }

    private RouteStopDetailsFragment routeStopDetailsFragment;

    private BroadcastReceiver locationChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (routeStopDetailsFragment != null) {
                Location location = intent.getExtras().getParcelable(Constants.BundleKeys.LOCATION);
                routeStopDetailsFragment.onLocationChanged(location);
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityCallback = (MainActivityCallback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getChildFragmentManager();
        storageManager = App.STORAGE_MANAGER;
        apiExecutor = new ApiExecutor((MainActivity) getActivity(), this, App.API_MANAGER, storageManager);
        openLoadFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        apiExecutor = null;
    }

    @Override
    public void onStop() {
        activity.unregisterReceiver(locationChangeReceiver);
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.registerReceiver(locationChangeReceiver, new IntentFilter(Constants.LOCATION_UPDATED));
    }


    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.order_nested_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeRefreshLayout(view);
        setActionButton(view);
    }

    public void getRidersSchedule() {
        apiExecutor.getRidersSchedule();
    }

    public void getRoute() {
        apiExecutor.getRoute();
    }

    public void startLocationSerivce() {
        apiExecutor.startLocationService();
    }

    private void setActionButton(View view) {
        layoutAction = view.findViewById(R.id.layout_action);
        btnAction = (Button) view.findViewById(R.id.btn_action);
        layoutAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus();
            }
        });
        layoutAction.setVisibility(isActionButtonView);
    }

    private void setSwipeRefreshLayout(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateApiRequest();
            }
        });
    }

    private void updateApiRequest() {
        switch (userStatus) {
            //In all cases according to the order logic
            //break section is missed, because we need the same API request for all
            //this screens just update route list and show current state of the first route
            case ACTION_LIST:
            case VIEWING:
            case EMPTY_LIST:
            case ARRIVING:
                apiExecutor.getRoute();
                break;
            case CLOCK_IN:
                apiExecutor.getRidersSchedule();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isActionButtonView = layoutAction.getVisibility();
    }

    private void changeStatus() {
        switch (userStatus) {
            case CLOCK_IN:
                apiExecutor.clockIn();
                break;
            case EMPTY_LIST:
                break;
            case VIEWING:
                apiExecutor.notifyActionPerformed(Action.ON_THE_WAY);
                setTaskTitle();
                break;
            case ARRIVING:
                openRouteStopActionList(storageManager.getCurrentStop());
                apiExecutor.notifyActionPerformed(Action.ARRIVED);
                break;
            case ACTION_LIST:
                apiExecutor.notifyActionPerformed(Action.COMPLETED);
                break;
        }
    }

    private void openRouteStopActionList(Stop stop) {
        userStatus = UserStatus.ACTION_LIST;
        swipeRefreshLayout.setEnabled(true);
        fragmentManager.
            beginTransaction().
            replace(R.id.container,
                RouteStopActionListFragment.newInstance(stop)).
            commit();

        int titleResourcesLink = storageManager.getCurrentStop().getTask() == RouteStopTaskStatus.DELIVER ?
            R.string.action_at_delivered : R.string.action_at_picked_up;
        updateActionButton(true, stop.getActivities().isEmpty(), titleResourcesLink, R.drawable.arrow_swipe);
    }

    private void openRouteStopDetails(Stop stop) {
        swipeRefreshLayout.setEnabled(false);
        RouteStopDetailsFragment fragment = RouteStopDetailsFragment.newInstance(stop);
        routeStopDetailsFragment = fragment;
        fragmentManager.
            beginTransaction().
            replace(R.id.container, fragment).
            commit();
    }

    private void updateActionButton(
        boolean isVisible,
        boolean isEnable,
        int textResLink
    ) {
        updateActionButton(isVisible, isEnable, textResLink, 0);
    }

    private void updateActionButton(
        final boolean isVisible,
        final boolean isEnable,
        final int textResLink,
        final int drawableLeft
    ) {
        if (isVisible) {
            layoutAction.setVisibility(View.VISIBLE);
            layoutAction.setEnabled(isEnable);
            if (drawableLeft != 0) {
                btnAction.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_swipe, 0, 0, 0);
            }
            btnAction.setText(textResLink);
        } else {
            layoutAction.setVisibility(View.GONE);
        }
    }

    private void setTaskTitle() {
        userStatus = UserStatus.ARRIVING;
        int title = storageManager.getCurrentStop().getTask() == RouteStopTaskStatus.DELIVER ?
            R.string.action_at_delivery : R.string.action_at_pick_up;
        updateActionButton(true, true, title, R.drawable.arrow_swipe);
    }

    private void enableButton(final boolean isEnabled, final int textResourceLink) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateActionButton(true, isEnabled, textResourceLink);
            }
        });
    }

    @Override
    public void onSeeMapClicked(GeoCoordinate geoCoordinate, String pinLabel) {
        mainActivityCallback.onSeeMapClicked(geoCoordinate, pinLabel);
    }

    @Override
    public void enableActionButton(boolean isEnabled, int textResLink) {
        enableButton(isEnabled, textResLink);
    }

    @Override
    public void openReadyToWork(ScheduleWrapper scheduleWrapper) {
        userStatus = UserStatus.CLOCK_IN;
        swipeRefreshLayout.setEnabled(true);
        fragmentManager.
            beginTransaction().
            replace(R.id.container,
                ReadyToWorkFragment.newInstance(scheduleWrapper)).
            commit();
    }

    @Override
    public void openEmptyListFragment(VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle) {
        userStatus = UserStatus.EMPTY_LIST;
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //Do not allow retrieve the data if empty list is already launched
                if (!(fragmentManager.getFragments().get(0) instanceof EmptyTaskListFragment)) {
                    swipeRefreshLayout.setRefreshing(true);
                    apiExecutor.getRoute();
                }
            }
        });

        fragmentManager.
            beginTransaction().
            replace(R.id.container,
                EmptyTaskListFragment.newInstance()).
            commit();

        updateActionButton(false, false, 0);
    }

    @Override
    public void openRoute(Stop stop) {
        switch (stop.getStatus()) {
            case UNASSIGNED:
            case ASSIGNED:
            case VIEWED:
                userStatus = UserStatus.VIEWING;
                apiExecutor.notifyActionPerformed(Action.VIEWED);
                updateActionButton(true, true, R.string.action_driving, R.drawable.arrow_swipe);
                openRouteStopDetails(stop);
                break;
            case ON_THE_WAY:
                setTaskTitle();
                openRouteStopDetails(stop);
                break;
            case ARRIVED:
                openRouteStopActionList(stop);
                break;
            default:
                Toast.makeText(activity, R.string.route_error, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void openLoadFragment() {
        fragmentManager.
            beginTransaction().
            replace(R.id.container, LoadDataFragment.newInstance()).
            commit();
    }

    @Override
    public void hideProgressIndicator() {
        swipeRefreshLayout.setRefreshing(false);
        if (routeStopDetailsFragment != null) {
            routeStopDetailsFragment.refreshComplete();
        }
    }

    @Override
    public void onRefresh() {
        updateApiRequest();
    }
}
