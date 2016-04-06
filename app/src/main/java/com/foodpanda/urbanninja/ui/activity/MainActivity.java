package com.foodpanda.urbanninja.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.BuildConfig;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.service.LocationService;
import com.foodpanda.urbanninja.api.service.RegistrationIntentService;
import com.foodpanda.urbanninja.manager.ApiExecutor;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.PushNotificationType;
import com.foodpanda.urbanninja.ui.dialog.PhoneNumberSingleChoiceDialog;
import com.foodpanda.urbanninja.ui.dialog.ProgressDialogFragment;
import com.foodpanda.urbanninja.ui.fragments.CashReportListFragment;
import com.foodpanda.urbanninja.ui.fragments.OrdersNestedFragment;
import com.foodpanda.urbanninja.ui.fragments.ScheduleListFragment;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.foodpanda.urbanninja.ui.interfaces.SlideMenuCallback;
import com.foodpanda.urbanninja.ui.util.SnackbarHelper;
import com.foodpanda.urbanninja.utils.DateUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Locale;

public class MainActivity extends BaseActivity implements SlideMenuCallback, MainActivityCallback {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final int PERMISSIONS_REQUEST_LOCATION = 100;

    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private StorageManager storageManager;

    private int currentItemId;

    private OrdersNestedFragment ordersNestedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        storageManager = App.STORAGE_MANAGER;

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
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
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
    }

    private void setNavigationDrawer() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        setSelectedNavigationItem();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
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
                    case R.id.logout:
                        onLogoutClicked();
                        break;
                }

                return true;
            }
        });

        showAppVersion();
    }

    /**
     * in the bottom of sliding menu we would have a label with
     * current version of the app taken from {@link BuildConfig} file
     * base on version in the gradle build file
     */
    private void showAppVersion() {
        TextView textView = (TextView) findViewById(R.id.txt_app_version);
        textView.setText(getResources().getString(R.string.side_menu_version, BuildConfig.VERSION_NAME));
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

    private void updateRiderSchedule() {
        if (ordersNestedFragment != null) {
            ordersNestedFragment.getRidersSchedule();
        } else {
            onOrdersClicked();
        }
    }

    private void updateRiderRoutes() {
        if (ordersNestedFragment != null) {
            ordersNestedFragment.getRoute();
        } else {
            onOrdersClicked();
        }
    }

    private void showSnackbar() {
        new SnackbarHelper(this, toolbar).showOrderCanceledSnackbar();
    }

    private void setSelectedNavigationItem() {
        navigationView.getMenu().getItem(0).setChecked(true);
        currentItemId = navigationView.getMenu().getItem(0).getItemId();
    }

    private Toolbar initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return toolbar;
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
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length == ApiExecutor.PERMISSIONS_ARRAY.length && ordersNestedFragment != null) {
                    ordersNestedFragment.startLocationSerivce();
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
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
                showPhoneDialog();
                return true;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLogoutClicked() {
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

    @Override
    public void onScheduleClicked() {
        ScheduleListFragment scheduleListFragment = ScheduleListFragment.newInstance();

        fragmentManager.
            beginTransaction().
            add(R.id.container, scheduleListFragment).
            addToBackStack(ScheduleListFragment.class.getSimpleName()).
            commit();
        drawerLayout.closeDrawers();
    }

    @Override
    public void onOrdersClicked() {
        if (ordersNestedFragment == null) {
            ordersNestedFragment = OrdersNestedFragment.newInstance();
        }
        fragmentManager.
            beginTransaction().
            detach(ordersNestedFragment).
            attach(ordersNestedFragment).
            commit();
        drawerLayout.closeDrawers();
    }

    @Override
    public void onCashReportClicked() {
        CashReportListFragment cashReportListFragment = CashReportListFragment.newInstance();

        fragmentManager.
            beginTransaction().
            add(R.id.container, cashReportListFragment).
            addToBackStack(CashReportListFragment.class.getSimpleName()).
            commit();
        drawerLayout.closeDrawers();
    }

    private void startOrderFragment() {
        ordersNestedFragment = OrdersNestedFragment.newInstance();
        fragmentManager.
            beginTransaction().
            replace(R.id.container, ordersNestedFragment).
            commit();
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
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_start_point_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPhoneSelected(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    @Override
    public void writeCodeAsTitle(Stop stop) {
        toolbar.setTitle(stop != null && !TextUtils.isEmpty(stop.getOrderCode()) ? stop.getOrderCode() : "");
        toolbar.setSubtitle(formatDeliverBefore(stop));
    }

    /**
     * Get sub title for action bar with information about delivery time
     *
     * @param stop current stop that should be deliveried
     * @return secondary title with delivery time
     */
    private String formatDeliverBefore(Stop stop) {
        return stop != null ?
            getString(R.string.main_activity_deliver_before, DateUtil.formatTimeHoursMinutes(stop.getArrivalTime())) : "";
    }

    @Override
    public void onBackPressed() {
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

    private void showPhoneDialog() {
        if (storageManager.getCurrentStop() != null) {
            PhoneNumberSingleChoiceDialog phoneNumberSingleChoiceDialog = PhoneNumberSingleChoiceDialog.newInstance(storageManager.getCurrentStop());
            phoneNumberSingleChoiceDialog.show(fragmentManager, ProgressDialogFragment.class.getSimpleName());
        } else {
            Toast.makeText(this, getResources().getString(R.string.dialog_phone_not_data), Toast.LENGTH_SHORT).show();
        }
    }
}
