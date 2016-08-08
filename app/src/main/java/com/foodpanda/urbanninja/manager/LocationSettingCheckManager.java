package com.foodpanda.urbanninja.manager;

import android.app.Activity;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.util.Log;

import com.foodpanda.urbanninja.ui.interfaces.NestedFragmentCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class LocationSettingCheckManager {
    private static final String TAG = LocationSettingCheckManager.class.getSimpleName();
    /**
     * Request code that would be returned in activity method onActivityResult
     * with result of enabling gps
     */
    public static final int GPS_SETTINGS_CHECK_REQUEST = 200;
    private Activity activity;
    private NestedFragmentCallback nestedFragmentCallback;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    // The callback for the management of the user settings regarding location
    // https://developer.android.com/training/location/change-location-settings.html we can check tutorial
    private ResultCallback<LocationSettingsResult> resultCallbackFromSettings = new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(@NonNull LocationSettingsResult result) {
            final Status status = result.getStatus();

            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    if (nestedFragmentCallback != null) {
                        nestedFragmentCallback.startLocationService();
                    }
                    Log.e(TAG, "GPS Turned on");
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.e(TAG, "RESOLUTION_REQUIRED");
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(activity, GPS_SETTINGS_CHECK_REQUEST);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.e(TAG, "Settings change unavailable. We have no way to fix the settings so we won't show the dialog.");
                    break;
            }
        }
    };

    /**
     * Manager to check if gps enabled
     * and if not show dialog to force to turn it on
     *
     * @param activity               need to get context and show enable gps dialog
     * @param nestedFragmentCallback callback to start LocationService to send rider location
     */
    public LocationSettingCheckManager(@NonNull Activity activity, @NonNull NestedFragmentCallback nestedFragmentCallback) {
        this.activity = activity;
        this.nestedFragmentCallback = nestedFragmentCallback;
    }

    /**
     * Check if location setting is disabled in the device
     * if location is enable we start LocationService
     * if location is disable we show dialog to turn location check on
     */
    public void checkGpsEnabled() {
        setApiClient();

        // Check the location settings of the user and create the callback to react to the different possibilities
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);

        PendingResult<LocationSettingsResult> result =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequestBuilder.build());

        result.setResultCallback(resultCallbackFromSettings);

    }

    /**
     * LocationService implements google interface {@link GoogleApiClient.ConnectionCallbacks}
     * as soon as we successfully connected to the ApiClient
     */
    private void setApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this.activity)
            .addApi(LocationServices.API)
            .build();
        googleApiClient.connect();

        locationRequest = new LocationRequest();
    }
}
