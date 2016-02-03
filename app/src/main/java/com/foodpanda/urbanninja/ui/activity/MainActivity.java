package com.foodpanda.urbanninja.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.api.service.RegistrationIntentService;
import com.foodpanda.urbanninja.manager.ApiExecutor;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.model.enums.Action;
import com.foodpanda.urbanninja.model.enums.RouteStopTaskStatus;
import com.foodpanda.urbanninja.model.enums.UserStatus;
import com.foodpanda.urbanninja.ui.fragments.EmptyTaskListFragment;
import com.foodpanda.urbanninja.ui.fragments.LoadDataFragment;
import com.foodpanda.urbanninja.ui.fragments.ReadyToWorkFragment;
import com.foodpanda.urbanninja.ui.fragments.RouteStopActionListFragment;
import com.foodpanda.urbanninja.ui.fragments.RouteStopDetailsFragment;
import com.foodpanda.urbanninja.ui.fragments.ScheduleListFragment;
import com.foodpanda.urbanninja.ui.fragments.SlideMenuFragment;
import com.foodpanda.urbanninja.ui.interfaces.LocationChangedCallback;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.foodpanda.urbanninja.ui.interfaces.SlideMenuCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Locale;

public class MainActivity extends BaseActivity implements SlideMenuCallback, MainActivityCallback {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final int PERMISSIONS_REQUEST_LOCATION = 100;

    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private Button btnAction;
    private View layoutAction;

    private StorageManager storageManager;
    private ApiExecutor apiExecutor;

    private UserStatus userStatus;

    private LocationChangedCallback locationChangedCallback;
    private BroadcastReceiver locationChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (locationChangedCallback != null) {
                Location location = intent.getExtras().getParcelable(Constants.BundleKeys.LOCATION);
                locationChangedCallback.onLocationChanged(location);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        storageManager = App.STORAGE_MANAGER;

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setActionButton();
        setActionBarDrawerToggle(initToolbar());

        if (savedInstanceState == null) {
            openLoadFragment();
            fragmentManager.
                beginTransaction().
                add(R.id.left_drawer, SlideMenuFragment.newInstance()).
                commit();
        }

        apiExecutor = new ApiExecutor(this);

        if (isPlayServicesAvailable()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        apiExecutor = null;
    }

    private void enableButton(final boolean isEnabled, final int textResourceLink) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateActionButton(true, isEnabled, textResourceLink);
            }
        });
    }

    @Override
    protected void onStop() {
        unregisterReceiver(locationChangeReceiver);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(locationChangeReceiver, new IntentFilter(Constants.LOCATION_UPDATED));
    }

    private void setActionButton() {
        layoutAction = findViewById(R.id.layout_action);
        btnAction = (Button) findViewById(R.id.btn_action);
        layoutAction.setEnabled(false);
        layoutAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus();
            }
        });
        updateActionButton(false, false, 0);
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

    private void setTaskTitle() {
        userStatus = UserStatus.ARRIVING;
        int title = storageManager.getCurrentStop().getTask() == RouteStopTaskStatus.DELIVER ?
            R.string.action_at_delivery : R.string.action_at_pick_up;
        updateActionButton(true, true, title, R.drawable.arrow_swipe);
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.call_menu, menu);

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length == ApiExecutor.PERMISSIONS_ARRAY.length) {
                    apiExecutor.startLocationService();
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_call:

                return true;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLogoutClicked() {
        storageManager.cleanToken();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onScheduleClicked() {
        ScheduleListFragment fragment = ScheduleListFragment.newInstance(isActionButtonVisible());
        fragmentManager.
            beginTransaction().
            add(R.id.container, fragment).
            addToBackStack(ScheduleListFragment.class.getSimpleName()).
            commit();

        updateActionButton(false, false, 0);
        drawerLayout.closeDrawers();
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
    public void enableActionButton(final boolean isEnabled, final int textResLink) {
        enableButton(isEnabled, textResLink);
    }

    @Override
    public void changeActionButtonVisibility(boolean b) {
        setActionButtonVisibility(b);
    }

    @Override
    public void openReadyToWork(ScheduleWrapper scheduleWrapper) {
        userStatus = UserStatus.CLOCK_IN;
        fragmentManager.
            beginTransaction().
            replace(R.id.container,
                ReadyToWorkFragment.newInstance(scheduleWrapper)).
            commit();
    }

    @Override
    public void openEmptyListFragment(
        VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle) {
        userStatus = UserStatus.EMPTY_LIST;
        fragmentManager.
            beginTransaction().
            replace(R.id.container,
                EmptyTaskListFragment.newInstance(vehicleDeliveryAreaRiderBundle)).
            commit();

        updateActionButton(false, false, 0);
    }

    @Override
    public void openRouteStopDetails(Stop stop) {
        userStatus = UserStatus.VIEWING;
        apiExecutor.notifyActionPerformed(Action.VIEWED);

        RouteStopDetailsFragment fragment = RouteStopDetailsFragment.newInstance(stop);
        locationChangedCallback = fragment;

        fragmentManager.
            beginTransaction().
            replace(R.id.container, fragment).
            commit();

        if (storageManager.getCurrentStop() != null) {
            updateActionButton(true, true, R.string.action_driving, R.drawable.arrow_swipe);
        }
    }

    @Override
    public void openRouteStopActionList(Stop stop) {
        userStatus = UserStatus.ACTION_LIST;
        fragmentManager.
            beginTransaction().
            replace(R.id.container,
                RouteStopActionListFragment.newInstance(stop)).
            commit();

        int title = storageManager.getCurrentStop().getTask() == RouteStopTaskStatus.DELIVER ?
            R.string.action_at_delivered : R.string.action_at_picked_up;
        updateActionButton(true, false, title, R.drawable.arrow_swipe);
    }

    @Override
    public void openLoadFragment() {
        fragmentManager.
            beginTransaction().
            replace(R.id.container, LoadDataFragment.newInstance()).
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
        } else {
            layoutAction.setVisibility(View.GONE);
            return;
        }
        layoutAction.setEnabled(isEnable);
        if (drawableLeft != 0) {
            btnAction.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_swipe, 0, 0, 0);
        }
        btnAction.setText(textResLink);
    }

    private boolean isActionButtonVisible() {
        return layoutAction.getVisibility() == View.VISIBLE;
    }

    private void setActionButtonVisibility(boolean isVisible) {
        layoutAction.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }


}
