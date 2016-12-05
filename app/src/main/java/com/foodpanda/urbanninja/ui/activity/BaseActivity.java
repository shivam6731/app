package com.foodpanda.urbanninja.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.R;
import com.foodpanda.urbanninja.di.component.MainComponent;
import com.foodpanda.urbanninja.model.enums.PushNotificationType;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.Tracking;
import net.hockeyapp.android.UpdateManager;

import java.net.HttpURLConnection;

public abstract class BaseActivity extends AppCompatActivity {
    protected FragmentManager fragmentManager;
    private View progressBar;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityComponent();
        fragmentManager = getSupportFragmentManager();
        checkForCrashes();
        checkForUpdates();
    }

    //http://stackoverflow.com/a/10261438/831625
    //With this code I try to get rid of crash with call
    //methods when app is not in a foreground
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    protected Toolbar initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        return toolbar;
    }

    public void setTitle(String title, boolean showHomeButton) {
        toolbar.setTitle(title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(showHomeButton);
            getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeButton);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar = findViewById(R.id.progress_spinner);
    }

    public void showProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tracking.startUsage(this);
    }

    @Override
    protected void onPause() {
        Tracking.stopUsage(this);
        super.onPause();
    }

    public void onError(int status, String message) {
        hideProgress();

        switch (status) {
            case HttpURLConnection.HTTP_CONFLICT:
                sendCancellationAction();
                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED:

            case HttpURLConnection.HTTP_NOT_FOUND:

            case 422:

            case HttpURLConnection.HTTP_INTERNAL_ERROR:

            default:
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * In case when rider trying to send an action to the canceled order
     * we need to let him know that this order is not up-to-date and we need to update route stop plan
     * <p/>
     * this is the same scenario as we have with cancellation push notifications,
     * so we are using the same code as for this push.
     */
    private void sendCancellationAction() {
        Intent intent = new Intent(Constants.PUSH_NOTIFICATION_RECEIVED);
        intent.putExtra(Constants.BundleKeys.PUSH_NOTIFICATION_TYPE, PushNotificationType.ROUTE_CANCELED);
        sendBroadcast(intent);
    }

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

    protected abstract void setupActivityComponent();

    /**
     * get main singleton component for injection
     *
     * @return dagger main component
     */
    protected MainComponent getComponent() {
        return App.get(this).getMainComponent();
    }

}
