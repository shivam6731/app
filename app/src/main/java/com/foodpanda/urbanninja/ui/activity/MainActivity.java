package com.foodpanda.urbanninja.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.model.enums.RouteStopStatus;
import com.foodpanda.urbanninja.ui.fragments.EmptyTaskListFragment;
import com.foodpanda.urbanninja.ui.fragments.LoadDataFragment;
import com.foodpanda.urbanninja.ui.fragments.SlideMenuFragment;
import com.foodpanda.urbanninja.ui.interfaces.SlideMenuCallback;

public class MainActivity extends BaseActivity implements SlideMenuCallback {
    private DrawerLayout drawerLayout;
    private Button btnAction;
    private RouteStopStatus routeStopStatus;

    private ApiManager apiManager;
    private StorageManager storageManager;

    private VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        apiManager = App.API_MANAGER;
        storageManager = App.STORAGE_MANAGER;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionButton();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setActionBarDrawerToggle(toolbar);

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().
                add(R.id.container, LoadDataFragment.newIntance()).commit();

            fragmentManager.beginTransaction().
                add(R.id.left_drawer, SlideMenuFragment.newInstance()).commit();
        }
        getCurrentRider();
    }

    private void setActionButton() {
        btnAction = (Button) findViewById(R.id.btn_action);
        btnAction.setEnabled(false);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnAction.setVisibility(View.GONE);
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

    private void getCurrentRider() {
        apiManager.getCurrentRider(new BaseApiCallback<VehicleDeliveryAreaRiderBundle>() {
            @Override
            public void onSuccess(VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle) {
                MainActivity.this.vehicleDeliveryAreaRiderBundle = vehicleDeliveryAreaRiderBundle;
                getRidersSchedule();
            }

            @Override
            public void onError(ErrorMessage errorMessage) {
                MainActivity.this.onError(errorMessage.getStatus(), errorMessage.getMessage());
            }
        });
    }

    private void getRidersSchedule() {
        fragmentManager.beginTransaction().
            replace(R.id.container, EmptyTaskListFragment.newInstance(vehicleDeliveryAreaRiderBundle)).commit();

    }

    @Override
    public void onLogoutClicked() {
        storageManager.storeToken(null);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
