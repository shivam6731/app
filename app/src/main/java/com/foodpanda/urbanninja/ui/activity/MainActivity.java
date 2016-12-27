package com.foodpanda.urbanninja.ui.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.service.LocationService;
import com.foodpanda.urbanninja.api.service.RegistrationIntentService;
import com.foodpanda.urbanninja.di.module.MainActivityModule;
import com.foodpanda.urbanninja.manager.ApiExecutor;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.manager.LocationSettingCheckManager;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Rider;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.CollectionIssueReason;
import com.foodpanda.urbanninja.model.enums.DialogType;
import com.foodpanda.urbanninja.model.enums.PushNotificationType;
import com.foodpanda.urbanninja.ui.dialog.InformationDialogFragment;
import com.foodpanda.urbanninja.ui.dialog.IssueCollectedDialog;
import com.foodpanda.urbanninja.ui.dialog.IssueVendorCustomerDialog;
import com.foodpanda.urbanninja.ui.fragments.CashReportListFragment;
import com.foodpanda.urbanninja.ui.fragments.OrdersNestedFragment;
import com.foodpanda.urbanninja.ui.fragments.ScheduleListFragment;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.foodpanda.urbanninja.ui.util.CircleTransform;
import com.foodpanda.urbanninja.ui.util.SnackbarHelper;
import com.foodpanda.urbanninja.utils.DateUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements MainActivityCallback {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final int PERMISSIONS_REQUEST_LOCATION = 100;

    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Inject
    StorageManager storageManager;
    @Inject
    ApiManager apiManager;
    @Inject
    LocationSettingCheckManager locationSettingCheckManager;

    private int currentItemId;

    private OrdersNestedFragment ordersNestedFragment;

    //Receive push notification content and force to update data without triggering with click
    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sendApiRequestAfterPush(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        setActionBarDrawerToggle(initToolbar());
        setNavigationDrawer();

        if (savedInstanceState == null) {
            startOrderFragment();
        }

        if (isPlayServicesAvailable()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        //Subscribe for all push updates
        registerReceiver(notificationReceiver, new IntentFilter(Constants.PUSH_NOTIFICATION_RECEIVED));
    }

    @Override
    protected void setupActivityComponent() {
        getComponent().plus(new MainActivityModule(this, ordersNestedFragment)).inject(this);
    }

    @Override
    public void onDestroy() {
        if (notificationReceiver != null) {
            //Un-subscribe from all push updates
            unregisterReceiver(notificationReceiver);
        }
        super.onDestroy();
    }

    /**
     * Triggers when push notification was clicked
     *
     * @param intent contains type on notification
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        sendApiRequestAfterPush(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIfOrderFragmentVisible();
    }

    /**
     * In case of low memory android system can destroy
     * fragment and after that we getting blank screen,
     * to get rid of this bug we need to recreate
     * and launch OrdersNestedFragment.
     * All data will be retrieved again
     */
    private void checkIfOrderFragmentVisible() {
        if (ordersNestedFragment == null ||
            ordersNestedFragment.isRemoving() ||
            !ordersNestedFragment.isAdded()) {
            startOrderFragment();
        }
    }

    /**
     * send API request to be up-to-date for all notification types
     *
     * @param intent with push notification type to trigger correct API request
     */
    private void sendApiRequestAfterPush(Intent intent) {
        if (intent == null) {
            return;
        }
        PushNotificationType pushNotificationType = (PushNotificationType)
            intent.getSerializableExtra(Constants.BundleKeys.PUSH_NOTIFICATION_TYPE);
        showProgress();
        if (pushNotificationType != null) {
            switch (pushNotificationType) {
                case SCHEDULE_UPDATED:
                    updateRiderSchedule();
                    break;
                case ROUTE_CANCELED:
                    showSnackbar();
                case ROUTE_UPDATED:
                    updateRiderRoutes();
                    break;
            }
        }
        navigateToMainFragmentIfNecessary();
    }

    /**
     * We need to navigate to the just updated screen after receiving push notification
     * if only we are not in OrdersNestedFragment we should navigate to this screen
     * Should be called only if activity in a foreground
     */
    private void navigateToMainFragmentIfNecessary() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (App.get(this).isMainActivityVisible() &&
            fragment != null &&
            !(fragment instanceof OrdersNestedFragment)) {
            onBackPressed();
        }
    }

    private void setNavigationDrawer() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        setSelectedNavigationItem();

        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawers();
            if (currentItemId == item.getItemId()) {
                return true;
            }
            currentItemId = item.getItemId();

            switch (item.getItemId()) {
                case R.id.orders:
                    onOrdersClicked();
                    break;
                case R.id.shift:
                    onScheduleClicked();
                    break;
                case R.id.cash_report:
                    onCashReportClicked();
                    break;
            }

            return true;
        });

        NavigationView navigationBottomView = (NavigationView) findViewById(R.id.navigation_drawer_bottom);
        if (navigationBottomView != null) {
            navigationBottomView.setNavigationItemSelectedListener(item -> {
                switch (item.getItemId()) {
                    case R.id.logout:
                        onLogoutClicked();
                        break;
                }

                return true;
            });
        }

        showAppVersion();
    }

    /**
     * set rider name and rider picture to the header view
     */
    public void setRiderContent() {
        Rider rider = storageManager.getRider();
        if (rider != null) {
            TextView txtRiderName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_rider_name);
            txtRiderName.setText(getResources().getString(R.string.side_menu_rider_name, rider.getFirstName(), rider.getSurname()));

            if (!TextUtils.isEmpty(rider.getPicture())) {
                ImageView imageRiderIcon = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.image_rider_icon);

                Picasso.with(this).
                    load(rider.getPicture()).
                    transform(new CircleTransform()).
                    into(imageRiderIcon);
            }
        }

    }

    /**
     * in the bottom of sliding menu we would have a label with
     * current version of the app taken from {@link BuildConfig} file
     * base on version in the gradle build file
     */
    private void showAppVersion() {
        TextView txtAppVersion = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_app_version);
        txtAppVersion.setText(getResources().getString(R.string.side_menu_version, BuildConfig.VERSION_NAME));
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean isPlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                    .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }

            return false;
        }

        return true;
    }

    /**
     * After push notification send API request to update
     * rider schedule to get up to date information
     */
    private void updateRiderSchedule() {
        if (ordersNestedFragment != null) {
            ordersNestedFragment.getRidersSchedule();
        } else {
            onOrdersClicked();
        }
    }

    /**
     * After push notification send API request to update
     * rider route list to get up to date information
     */
    private void updateRiderRoutes() {
        if (ordersNestedFragment != null) {
            ordersNestedFragment.getRoute();
        } else {
            onOrdersClicked();
        }
    }

    /**
     * Show error message in the snackBar and after
     */
    private void showSnackbar() {
        new SnackbarHelper(this, toolbar).showOrderCanceledSnackbar();
    }

    private void setSelectedNavigationItem() {
        navigationView.getMenu().getItem(0).setChecked(true);
        currentItemId = navigationView.getMenu().getItem(0).getItemId();

        //set title and subtitle for order section
        writeCodeAsTitle(storageManager.getCurrentStop());
    }

    private void setActionBarDrawerToggle(Toolbar toolbar) {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.side_menu_open,
            R.string.side_menu_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    /**
     * In android 6+ user has to allow the app use some permission
     * and here we receive result of permission request
     *
     * @param requestCode  request code with permission type
     * @param permissions  array of requested permissions
     * @param grantResults array of granted permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length == ApiExecutor.PERMISSIONS_ARRAY.length && ordersNestedFragment != null) {
                    locationSettingCheckManager.checkGpsEnabled();
                } else {
                    Toast.makeText(this, R.string.error_gps_permission_disabled, Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.call_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_call:
                showIssueDialog(DialogType.ISSUE_VENDOR_CUSTOMER_SELECTION);
                return true;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onLogoutClicked() {
        apiManager.logout();
        storageManager.cleanSession();
        stopLocationService();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * We need to stop location service when rider
     * logout to avoid unauthorized sending location
     * and stop tracking location for riders how not logged in anymore
     */
    private void stopLocationService() {
        Intent closeServiceIntent = new Intent(this, LocationService.class);
        stopService(closeServiceIntent);
    }

    private void onScheduleClicked() {
        setTitleNotForOrderPage(R.string.side_menu_schedule);
        ScheduleListFragment scheduleListFragment = ScheduleListFragment.newInstance();

        fragmentManager.
            beginTransaction().
            add(R.id.container, scheduleListFragment).
            addToBackStack(ScheduleListFragment.class.getSimpleName()).
            commit();
        drawerLayout.closeDrawers();
    }

    private void onCashReportClicked() {
        setTitleNotForOrderPage(R.string.side_menu_cash_report);
        CashReportListFragment cashReportListFragment = CashReportListFragment.newInstance();

        fragmentManager.
            beginTransaction().
            add(R.id.container, cashReportListFragment).
            addToBackStack(CashReportListFragment.class.getSimpleName()).
            commit();
        drawerLayout.closeDrawers();
    }

    /**
     * When order menu selected we should close the drawer and only after redirect to the #OrdersNestedFragment
     * like when we press back button.
     * In this case we don't have to recreate or reattach the fragment from activity
     * and all data would be present and the state would be the same
     */
    private void onOrdersClicked() {
        writeCodeAsTitle(storageManager.getCurrentStop());

        //After closing the drawer we have to redirect to the  nested fragment
        //and to check the close action we need this callback to start onBackPressed method
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //when drawer closed we should un subscribe this listener
                //and call redirect method witch is onBackPressed
                onBackPressed();
                drawerLayout.removeDrawerListener(this);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        drawerLayout.closeDrawers();
    }

    private void startOrderFragment() {
        ordersNestedFragment = OrdersNestedFragment.newInstance();
        fragmentManager.
            beginTransaction().
            replace(R.id.container, ordersNestedFragment).
            commit();

        locationSettingCheckManager.setNestedFragmentCallback(ordersNestedFragment);
    }

    @Override
    public void onSeeMapClicked(GeoCoordinate geoCoordinate, String pinLabel) {
        if (geoCoordinate != null) {
            String uri = String.format(
                Locale.ENGLISH,
                "geo:0,0?q=%f,%f(" + pinLabel + ")",
                geoCoordinate.getLat(),
                geoCoordinate.getLon()
            );

            try {
                // not all our devices have android maps so we should catch this case and show error message
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(this, getResources().getString(R.string.error_no_map_application), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, getResources().getString(R.string.error_start_point_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPhoneSelected(@NonNull String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    @Override
    public void writeCodeAsTitle(Stop stop) {
        if (toolbar != null) {
            toolbar.setTitle(stop != null && !TextUtils.isEmpty(stop.getOrderCode()) ? stop.getOrderCode() : "");
            toolbar.setSubtitle(formatDeliverBefore(stop));
        }
    }

    @Override
    public void writeFragmentTitle(@NonNull String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
            toolbar.setSubtitle("");
        }
    }

    @Override
    public void onGPSSettingClicked() {
        Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(viewIntent);
    }

    @Override
    public void showInformationDialog(
        @NonNull CharSequence title,
        @NonNull CharSequence message,
        @NonNull CharSequence buttonLabel,
        @NonNull String webUrl,
        @NonNull DialogType dialogType
    ) {
        if (!isInfoDialogShown()) {
            InformationDialogFragment informationDialogFragment = InformationDialogFragment.newInstance(
                title,
                message,
                buttonLabel,
                webUrl,
                dialogType);

            informationDialogFragment.show(fragmentManager, InformationDialogFragment.class.getSimpleName());
        }
    }

    @Override
    public void showCollectionIssueDialog() {
        IssueCollectedDialog issueCollectedDialog = IssueCollectedDialog.newInstance(storageManager.getCountry());
        issueCollectedDialog.show(fragmentManager, IssueCollectedDialog.class.getSimpleName());
    }

    @Override
    public void sendCollectionIssue(double collectionAmount, CollectionIssueReason reason) {
        if (ordersNestedFragment != null) {
            ordersNestedFragment.reportCollectionIssue(collectionAmount, reason);
        }
    }

    @Override
    public void openWebPage(@NonNull String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void showIssueDialog(DialogType dialogType) {
        if (storageManager.hasCurrentStop()) {
            IssueVendorCustomerDialog issueVendorCustomerDialog = IssueVendorCustomerDialog.newInstance(
                dialogType,
                storageManager.getCurrentStop(),
                storageManager.getRider()
            );

            issueVendorCustomerDialog.show(fragmentManager, IssueVendorCustomerDialog.class.getSimpleName());
        } else {
            Toast.makeText(this, getResources().getString(R.string.dialog_phone_not_data), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showDevSetting() {
        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
    }

    /**
     * Get sub title for action bar with information about delivery time
     *
     * @param currentStop that should be delivered
     * @return secondary title with delivery time
     */
    private String formatDeliverBefore(Stop currentStop) {
        return currentStop != null &&
            !TextUtils.isEmpty(currentStop.getOrderCode()) ?
            getDeliveryArrivalTimeForAllTaskTypes(currentStop) : "";
    }

    /**
     * get arrival time for delivery part of each route stop
     * no matter if it's pick-up or delivery type the arrival time would be
     * for delivery part of current order
     *
     * @param currentStop current stop
     * @return formatted arrival time for delivery part of route stop
     */
    private String getDeliveryArrivalTimeForAllTaskTypes(Stop currentStop) {
        Stop deliveryStop = storageManager.getDeliveryPartOfEachRouteStop(currentStop);

        return deliveryStop == null ? "" : getString(R.string.main_activity_deliver_before,
            DateUtil.formatTimeHoursMinutes(deliveryStop.getArrivalTime()));
    }

    @Override
    public void onBackPressed() {
        //if drawer in opened it should be closed by back button press
        //otherwise we have to redirect to the OrdersNestedFragment
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            int count = fragmentManager.getBackStackEntryCount();
            if (count <= 0) {
                super.onBackPressed();
            } else {
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (!(fragment instanceof OrdersNestedFragment)) {
                        fragmentManager.popBackStack();
                    }
                }
                setSelectedNavigationItem();
            }
        }
    }

    /**
     * set title for all section except only orders with detail about current route stop
     *
     * @param stringResource link for title string resource
     */
    private void setTitleNotForOrderPage(int stringResource) {
        toolbar.setTitle(getResources().getString(stringResource));
        toolbar.setSubtitle("");
    }

    /**
     * Used to check the result of the check of the user location settings
     *
     * @param requestCode code of the request made
     * @param resultCode  code of the result of that request
     * @param intent      intent with further information
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == LocationSettingCheckManager.GPS_SETTINGS_CHECK_REQUEST) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    ordersNestedFragment.startLocationService();
                    break;
                case Activity.RESULT_CANCELED:
                    //In case when location is disabled we need to request it again
                    locationSettingCheckManager.checkGpsEnabled();
                    //Toast to show error message
                    Toast.makeText(this, R.string.error_gps_disabled, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     * To prevent showing a lot of the same dialogs
     * we need to check if dialog is shown to not to show the new one.
     * to do so we get dialog fragment from fragmentManager to check the status
     *
     * @return true if we have some dialog shown in screen right now
     */
    private boolean isInfoDialogShown() {
        Fragment fragment = fragmentManager.findFragmentByTag(InformationDialogFragment.class.getSimpleName());

        return fragment != null && (fragment instanceof DialogFragment);
    }
}
