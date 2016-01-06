package com.foodpanda.urbanninja.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.manager.ApiExecutor;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.model.enums.UserStatus;
import com.foodpanda.urbanninja.ui.fragments.EmptyTaskListFragment;
import com.foodpanda.urbanninja.ui.fragments.LoadDataFragment;
import com.foodpanda.urbanninja.ui.fragments.PickUpFragment;
import com.foodpanda.urbanninja.ui.fragments.ReadyToWorkFragment;
import com.foodpanda.urbanninja.ui.fragments.SlideMenuFragment;
import com.foodpanda.urbanninja.ui.interfaces.MainActivityCallback;
import com.foodpanda.urbanninja.ui.interfaces.PermissionAccepted;
import com.foodpanda.urbanninja.ui.interfaces.SlideMenuCallback;

import java.util.Locale;

public class MainActivity extends BaseActivity implements SlideMenuCallback, MainActivityCallback {

    private DrawerLayout drawerLayout;
    private Button btnAction;

    private StorageManager storageManager;
    private ApiExecutor apiExecutor;

    private UserStatus userStatus;
    private PermissionAccepted permissionAccepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        storageManager = App.STORAGE_MANAGER;

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setActionButton();
        setActionBarDrawerToggle(initToolbar());

        if (savedInstanceState == null) {
            fragmentManager.
                beginTransaction().
                add(R.id.container, LoadDataFragment.newIntance()).
                commit();

            fragmentManager.
                beginTransaction().
                add(R.id.left_drawer, SlideMenuFragment.newInstance()).
                commit();
        }
        apiExecutor = new ApiExecutor(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        apiExecutor = null;
    }

    private void enableButton(final boolean isEnabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateActionButton(true, isEnabled, R.string.action_ready_to_work);
            }
        });
    }

    private void setActionButton() {
        btnAction = (Button) findViewById(R.id.btn_action);
        btnAction.setEnabled(false);
        btnAction.setOnClickListener(new View.OnClickListener() {
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
            case EMPTY_LIST:
            case ARRIVING:
            case PICK_UP:
        }
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
            case PickUpFragment.MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length == 2 && permissionAccepted != null) {
                    permissionAccepted.onPermissionAccepted();
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
    public void onSeeMapClicked(GeoCoordinate geoCoordinate) {
        if (geoCoordinate != null) {
            String uri = String.format(
                Locale.ENGLISH,
                "geo:%f,%f",
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
    public void enableActionButton(boolean isEnabled) {
        enableButton(isEnabled);
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
    public void openEmptyListFragment(VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle) {
        userStatus = UserStatus.EMPTY_LIST;
        fragmentManager.
            beginTransaction().
            replace(R.id.container,
                EmptyTaskListFragment.newInstance(vehicleDeliveryAreaRiderBundle)).
            commit();

        updateActionButton(false, false, 0);
    }

    @Override
    public void openPickUp(RouteWrapper routeWrapper) {
        userStatus = UserStatus.ARRIVING;
        PickUpFragment fragment = PickUpFragment.newInstance(routeWrapper);
        permissionAccepted = fragment;
        fragmentManager.
            beginTransaction().
            replace(R.id.container,
                fragment).
            commit();

        updateActionButton(true, true, R.string.action_at_pick_up);
    }

    private void updateActionButton(
        boolean isVisible,
        boolean isEnable,
        int textRes) {
        if (isVisible) {
            btnAction.setVisibility(View.VISIBLE);
        } else {
            btnAction.setVisibility(View.GONE);
            return;
        }
        btnAction.setEnabled(isEnable);
        btnAction.setText(textRes);
    }

}
