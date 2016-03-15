package com.foodpanda.urbanninja.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.Tracking;
import net.hockeyapp.android.UpdateManager;

import java.net.HttpURLConnection;

public abstract class BaseActivity extends AppCompatActivity {
    protected FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


    protected void hideActionBar() {
        if (getActionBar() != null) {
            getActionBar().hide();
        }
    }

    public void onError(int status, String message) {
        switch (status) {
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

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }
}
