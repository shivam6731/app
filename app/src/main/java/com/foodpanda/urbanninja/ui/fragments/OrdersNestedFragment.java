package com.foodpanda.urbanninja.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import com.foodpanda.urbanninja.di.module.OrderNestedFragmentModule;
import com.foodpanda.urbanninja.manager.ApiExecutor;
import com.foodpanda.urbanninja.manager.LocationSettingCheckManager;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.CollectionIssueReason;
import com.foodpanda.urbanninja.model.enums.DialogType;
import com.foodpanda.urbanninja.model.enums.Status;
import com.foodpanda.urbanninja.model.enums.UserStatus;
import com.foodpanda.urbanninja.ui.activity.MainActivity;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.foodpanda.urbanninja.ui.interfaces.MapAddressDetailsChangeListener;
import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.foodpanda.urbanninja.ui.util.ActionLayoutHelper;

import javax.inject.Inject;

import static java.util.Arrays.asList;

/**
 * To encapsulate all logic according to current rider's orders in one separate navigation menu item
 * this order wrapper fragment was created.
 * It would be recreated any time when 'orders' menu item was selected in navigation menu except only
 * if this item is current one
 */
public class OrdersNestedFragment extends BaseFragment implements NestedFragmentCallback {
    private MainActivityCallback mainActivityCallback;
    private SwipeRefreshLayout swipeRefreshLayout;

    private UserStatus userStatus = UserStatus.LOADING;

    @Inject
    ActionLayoutHelper actionLayoutHelper;

    @Inject
    ApiExecutor apiExecutor;

    @Inject
    StorageManager storageManager;

    @Inject
    LocationSettingCheckManager locationSettingCheckManager;

    public static OrdersNestedFragment newInstance() {
        return new OrdersNestedFragment();
    }

    private MapAddressDetailsChangeListener mapAddressDetailsChangeListener;

    private BroadcastReceiver locationChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getExtras().getParcelable(Constants.BundleKeys.LOCATION);
            checkMockLocationDialog(location);

            if (mapAddressDetailsChangeListener != null) {
                mapAddressDetailsChangeListener.onLocationChanged(location);
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
        openLoadFragment();
    }

    @Override
    protected void setupComponent() {
        super.setupComponent();
        App.get(getContext()).getMainComponent().plus(new OrderNestedFragmentModule((MainActivity) activity, this)).inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //if the view fragment is not alive anymore we shouldn't execute any API request
        //it allows us to get rid of memory leaks 
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

    @Override
    public void onResume() {
        super.onResume();
        sendViewedStatusIfNeeds();
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
        if (apiExecutor != null) {
            apiExecutor.updateScheduleAndRouteStop();
        }
    }

    public void getRoute() {
        if (apiExecutor != null) {
            apiExecutor.updateRoute();
        }
    }

    public void reportCollectionIssue(double collectionAmount, CollectionIssueReason reason) {
        if (apiExecutor != null) {
            apiExecutor.reportCollectionIssue(collectionAmount, reason);
        }
    }

    private void notifyActionPerformed(Status status) {
        if (apiExecutor != null && status != null) {
            apiExecutor.notifyActionPerformed(status);
        }
    }

    private void setActionButton(View view) {
        View layoutAction = view.findViewById(R.id.layout_action);
        Button btnAction = (Button) view.findViewById(R.id.btn_action);

        actionLayoutHelper.setLayoutAction(layoutAction);
        actionLayoutHelper.setBtnAction(btnAction);

        layoutAction.setOnClickListener(v -> changeStatus());
        actionLayoutHelper.setActionButtonState();
    }

    private void setSwipeRefreshLayout(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(activity, R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this::updateApiRequest);
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
                getRoute();
                break;
            case CLOCK_IN:
                getRidersSchedule();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        actionLayoutHelper.saveActionButtonState();
    }

    /**
     * we found a bug with sending rider notify method (you can find it here https://foodpanda.atlassian.net/browse/LOGI-1067).
     * in case when rider receive new order push notification and update route stop plan with locked device or with app in a background
     * we use to automatically send view status for route stop that was not viewed yet.
     * <p/>
     * from now we send viewed status every time when rider open device from background and
     * route stop was not viewed.
     */
    private void sendViewedStatusIfNeeds() {
        Stop stop = storageManager.getCurrentStop();
        if (stop != null &&
            stop.getStatus() != null &&
            asList(Status.UNASSIGNED, Status.VIEWED).contains(stop.getStatus())) {
            notifyViewed();
        }
    }

    private void changeStatus() {
        switch (userStatus) {
            case CLOCK_IN:
                //we need to turn off check if rider inside delivery zone for now
                apiExecutor.tryToClockInInsideDeliveryZone();
                break;
            case EMPTY_LIST:
                break;
            case VIEWING:
                notifyActionPerformed(Status.ON_THE_WAY);
                userStatus = UserStatus.ARRIVING;
                showDoneCheckbox();
                actionLayoutHelper.setViewedStatusActionButton(storageManager.getCurrentStop());
                break;
            case ARRIVING:
                openRouteStopActionList(storageManager.getCurrentStop());
                notifyActionPerformed(Status.ARRIVED);
                break;
            case ACTION_LIST:
                notifyActionPerformed(Status.COMPLETED);
                break;
        }
    }

    /**
     * Next step checkbox should be visible only in Status.ON_THE_WAY status
     * so we have to make it visible manually
     */
    private void showDoneCheckbox() {
        if (mapAddressDetailsChangeListener != null) {
            mapAddressDetailsChangeListener.setActionDoneCheckboxVisibility(true);
        }
    }

    private void openRouteStopActionList(Stop stop) {
        userStatus = UserStatus.ACTION_LIST;
        replaceFragment(RouteStopActionListFragment.newInstance(stop));

        actionLayoutHelper.setRouteStopActionListButton(stop);
    }

    private void openRouteStopDetails(Stop stop) {
        //TODO add list for stops
        RouteStopDetailsFragment fragment = RouteStopDetailsFragment.newInstance(stop);
        replaceFragment(fragment);
    }

    private void setButtonVisibility(final boolean isVisible, final int textResourceLink) {
        activity.runOnUiThread(() -> actionLayoutHelper.updateActionButton(isVisible, textResourceLink));
    }

    @Override
    public void onSeeMapClicked(GeoCoordinate geoCoordinate, String pinLabel) {
        mainActivityCallback.onSeeMapClicked(geoCoordinate, pinLabel);
    }

    @Override
    public void onPhoneNumberClicked(String phoneNumber) {
        mainActivityCallback.onPhoneSelected(phoneNumber);
    }

    @Override
    public void setActionButtonVisible(boolean isVisible, int textResLink) {
        setButtonVisibility(isVisible, textResLink);
    }

    @Override
    public void setActionButtonVisible(boolean isVisible) {
        actionLayoutHelper.setVisibility(isVisible);
    }

    @Override
    public void openReadyToWork(ScheduleWrapper scheduleWrapper) {
        userStatus = UserStatus.CLOCK_IN;
        replaceFragment(ReadyToWorkFragment.newInstance(scheduleWrapper));
        actionLayoutHelper.setReadyToWorkActionButton();
    }

    @Override
    public void openEmptyListFragment() {
        userStatus = UserStatus.EMPTY_LIST;
        swipeRefreshLayout.post(() -> {
            //Do not allow retrieve the data if empty list is already launched
            //in case when the fragment stack is empty the data should be retrieved
            if (fragmentManager.getFragments() != null &&
                !fragmentManager.getFragments().isEmpty() &&
                !(fragmentManager.getFragments().get(0) instanceof EmptyTaskListFragment)) {
                swipeRefreshLayout.setRefreshing(true);
                getRoute();
            }
        });
        replaceFragment(EmptyTaskListFragment.newInstance());

        actionLayoutHelper.hideActionButton();
    }

    @Override
    public void openLoadFragment() {
        addFragment(R.id.container, LoadDataFragment.newInstance());
    }

    @Override
    public void openRoute(Stop stop) {
        switch (stop.getStatus()) {
            case UNASSIGNED:
            case VIEWED:
                userStatus = UserStatus.VIEWING;
                notifyViewed();
                openRouteStopDetails(stop);
                actionLayoutHelper.setDrivingHereStatusActionButton();
                break;
            case ON_THE_WAY:
                userStatus = UserStatus.ARRIVING;
                openRouteStopDetails(stop);
                actionLayoutHelper.setViewedStatusActionButton(stop);
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
    public void hideProgressIndicator() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setSwipeToRefreshEnable(boolean enable) {
        swipeRefreshLayout.setEnabled(enable);
    }

    @Override
    public void startLocationService() {
        if (apiExecutor != null) {
            apiExecutor.startLocationService();
        }
    }

    @Override
    public void openInformationDialog(CharSequence title, CharSequence message, CharSequence buttonLabel, DialogType dialogType) {
        openInformationDialog(title, message, buttonLabel, dialogType, null);
    }

    @Override
    public void openInformationDialog(CharSequence title, CharSequence message, CharSequence buttonLabel, DialogType dialogType, String webUrl) {
        mainActivityCallback.showInformationDialog(
            title,
            message,
            buttonLabel,
            webUrl,
            dialogType
        );
    }

    /**
     * check if app right now in a foreground and send vieved status of the route stop to the server side
     */
    private void notifyViewed() {
        if (App.get(activity).isMainActivityVisible()) {
            notifyActionPerformed(Status.VIEWED);
        }
    }

    private void replaceFragment(BaseFragment baseFragment) {
        if (baseFragment instanceof MapAddressDetailsChangeListener) {
            mapAddressDetailsChangeListener = (MapAddressDetailsChangeListener) baseFragment;
        }
        if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
            fragmentManager.
                beginTransaction().
                replace(R.id.container,
                    baseFragment).
                commitAllowingStateLoss();
        }
    }

    /**
     * Show information dialog with description how to turn off
     * mock location in dev settings.
     */
    private void showFakeGpsDialog() {
        openInformationDialog(
            getResources().getText(R.string.fake_location_dialog_title),
            getResources().getText(R.string.fake_location_dialog_text),
            getResources().getText(R.string.fake_location_dialog_button_label),
            DialogType.FAKE_LOCATION_SETTING);
    }

    /**
     * We check if rider use fake gps to handle {@link #showFakeGpsDialog()}
     * in case when he does.
     *
     * @param location last known rider location
     */
    private void checkMockLocationDialog(Location location) {
        if (locationSettingCheckManager.isLocationMocked(location)) {
            showFakeGpsDialog();
        }
    }

}
