package com.foodpanda.urbanninja.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.service.RegistrationIntentService;
import com.foodpanda.urbanninja.manager.ApiExecutor;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.enums.PushNotificationType;
import com.foodpanda.urbanninja.ui.fragments.OrdersNestedFragment;
import com.foodpanda.urbanninja.ui.fragments.ScheduleListFragment;
import com.foodpanda.urbanninja.ui.interfaces.SlideMenuCallback;
import com.foodpanda.urbanninja.ui.util.SnackbarHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends BaseActivity implements SlideMenuCallback {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final int PERMISSIONS_REQUEST_LOCATION = 100;

    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    private StorageManager storageManager;

    private int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        storageManager = App.STORAGE_MANAGER;

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);

        setActionBarDrawerToggle(initToolbar());
        setNavigationDrawer();

        if (savedInstanceState == null) {
            onOrderClicked();
        }


        if (isPlayServicesAvailable()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private void setNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                if (selectedItem == item.getItemId()) {
                    return false;
                }
                item.setChecked(true);
                selectedItem = item.getItemId();

                switch (item.getItemId()) {
                    case R.id.orders:
                        onOrderClicked();
                        break;
                    case R.id.shift:
                        onScheduleClicked();
                        break;
                    case R.id.order_history:
                        //TODO should be replaced to order history
                        onScheduleClicked();
                        break;
                    case R.id.logout:
                        onLogoutClicked();
                        break;
                }
                return false;
            }
        });
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        PushNotificationType pushNotificationType = (PushNotificationType)
            intent.getSerializableExtra(Constants.BundleKeys.PUSH_NOTIFICATION_TYPE);
        showProgress();

        switch (pushNotificationType) {
            case SCHEDULE_UPDATED:
                //TODO add api executor
//                apiExecutor.getRidersSchedule();
                break;
            case ROUTE_CANCELED:
                showSnackbar();
            case ROUTE_UPDATED:
                //TODO add api executor
//                apiExecutor.getRoute();
                break;
        }
    }

    private void showSnackbar() {
        new SnackbarHelper(this, toolbar).showOrderCanceledSnackbar();
    }


    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
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
                    //TODO add api executor
//                    apiExecutor.startLocationService();
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
        ScheduleListFragment fragment = ScheduleListFragment.newInstance();
        fragmentManager.
            beginTransaction().
            replace(R.id.container, fragment).
            addToBackStack(ScheduleListFragment.class.getSimpleName()).
            commit();
        drawerLayout.closeDrawers();
    }

    @Override
    public void onOrderClicked() {
        OrdersNestedFragment fragment = OrdersNestedFragment.newInstance();
        fragmentManager.
            beginTransaction().
            replace(R.id.container, fragment).
            addToBackStack(ScheduleListFragment.class.getSimpleName()).
            commit();
        drawerLayout.closeDrawers();
    }


}
