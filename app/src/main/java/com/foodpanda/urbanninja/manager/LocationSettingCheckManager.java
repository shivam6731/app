package com.foodpanda.urbanninja.manager;

import android.content.IntentSender;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.foodpanda.urbanninja.Config;
import com.foodpanda.urbanninja.ui.activity.BaseActivity;
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

import javax.inject.Inject;

public class LocationSettingCheckManager {
    private static final String TAG = LocationSettingCheckManager.class.getSimpleName();
    /**
     * Request code that would be returned in baseActivity method onActivityResult
     * with result of enabling gps
     */
    public static final int GPS_SETTINGS_CHECK_REQUEST = 200;
    private final BaseActivity baseActivity;
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
                    nestedFragmentCallback.startLocationService();
                    Log.e(TAG, "GPS Turned on");
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.e(TAG, "RESOLUTION_REQUIRED");
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(baseActivity, GPS_SETTINGS_CHECK_REQUEST);
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
     * @param baseActivity           need to get context and show enable gps dialog
     * @param nestedFragmentCallback callback to start LocationService to send rider location
     */
    @Inject
    public LocationSettingCheckManager(
        @NonNull BaseActivity baseActivity,
        @NonNull NestedFragmentCallback nestedFragmentCallback) {
        this.baseActivity = baseActivity;
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
     * Because some of our riders try to cheat with fake location
     * we need to check device dev param or location object.
     * In case when rider use fake data we show un cancelable dialog to redirect to dev setting
     * to turn off fake GPS provider.
     *
     * @param location last known rider location
     * @return true in case when rider use fake location coordinate.
     */
    public boolean isLocationMocked(Location location) {
        //for dev and staging we allow to use fake location
        if (Config.IS_FAKE_LOCATION_ALLOWED) {
            return false;
        }

        // Starting with API level >= 18 we can (partially) rely on .isFromMockProvider()
        // (http://developer.android.com/reference/android/location/Location.html#isFromMockProvider%28%29)
        // For API level < 18 we have to check the Settings.Secure flag
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            return location.isFromMockProvider();
        } else {
            return !Settings.Secure.getString(baseActivity.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }
    }

    /**
     * LocationService implements google interface {@link GoogleApiClient.ConnectionCallbacks}
     * as soon as we successfully connected to the ApiClient
     */
    private void setApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this.baseActivity)
            .addApi(LocationServices.API)
            .build();
        googleApiClient.connect();

        locationRequest = new LocationRequest();
    }

    public void setNestedFragmentCallback(@NonNull NestedFragmentCallback nestedFragmentCallback) {
        this.nestedFragmentCallback = nestedFragmentCallback;
    }
}
