package com.foodpanda.urbanninja.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.net.HttpURLConnection;

public abstract class BaseActivity extends AppCompatActivity {
    protected FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
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
}
