package com.foodpanda.urbanninja.api.service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.api.model.RiderLocation;
import com.foodpanda.urbanninja.manager.ApiManager;
import com.foodpanda.urbanninja.manager.StorageManager;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

/**
 * This Service provides background collecting rider location
 * send it to the server side every minute and if this sending failed
 * store this location List to the {@link com.foodpanda.urbanninja.manager.ApiQueue}
 * to send it to the server side as soon as connection would be available
 * <p>
 * This service launched as soon as rider's schedule was received
 * and shut down with the end of current schedule
 */
public class LocationService extends Service implements
    LocationListener,
    GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks {
    /**
     * Battery level param, according to this param we set location update interval
     * When the battery level more then @see #BIG_BATTERY_LEVEL get location updates every {@value #SMALL_UPDATE_PERIOD}
     * When the battery level less then {@value #BIG_BATTERY_LEVEL},
     * but more then {@value #LOW_BATTERY_LEVEL} we get location updates every {@link #MIDDLE_UPDATE_PERIOD},
     * otherwise we set {@value #BIG_UPDATE_PERIOD}
     */
    //Big battery level in %
    private static final int BIG_BATTERY_LEVEL = 75;
    //Low battery level in %
    private static final int LOW_BATTERY_LEVEL = 25;
    //Minimal distance that should trigger our location service
    private static final int DISTANCE_RANGE = 0;
    //Location update interval for not requested location it means we will receive this location
    //in case when some other app needs location and we just subscribe to this updates
    private static final int FASTEST_UPDATE_PERIOD = 10000;
    //The shortest location update interval for the full battery level
    private static final int SMALL_UPDATE_PERIOD = 30000;
    //Middle location update interval for the not full battery level
    private static final int MIDDLE_UPDATE_PERIOD = 60000;
    //The longest location update interval for the low battery level
    private static final int BIG_UPDATE_PERIOD = 90000;
    //Interval for sending location to the server side
    //the data would be send if only location bundle is not empty
    private static final int SEND_DATA_INTERVAL = 30000;
    //intent for retrieve data about rider activity
    private PendingIntent pendingIntent;
    @Inject
    ApiManager apiManager;
    @Inject
    StorageManager storageManager;

    private Timer timer;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private BroadcastReceiver batteryLevelReceiver;

    private List<RiderLocation> locationList = new ArrayList<>();

    private int batteryLevel;
    private int vehicleId;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.get(this).getMainComponent().inject(this);
    }

    /**
     * This is a start point of our service
     * this method would be called as soon as service started
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            vehicleId = intent.getExtras().getInt(Constants.BundleKeys.VEHICLE_ID);
            setApiClient();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        disconnect();
        super.onDestroy();
    }

    /**
     * After calling {@link #requestLocationUpdates()} will be triggered
     * any time when rider location would be changed
     *
     * @param location new rider location from googleApi
     */
    @Override
    public void onLocationChanged(Location location) {
        sendLocationUpdate(location);
        storeLocation(location);
    }

    /**
     * As soon as we successfully connected to the googleApi we should
     * request location updates from the GPS
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
        retrieveVehicleDetectedActivity();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    private void disconnect() {
        if (batteryLevelReceiver != null) {
            unregisterReceiver(batteryLevelReceiver);
        }

        if (timer != null) {
            timer.cancel();
        }
        timer = null;
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private void setApiClient() {
        if (googleApiClient == null) {
            /**
             * LocationService implements google interface {@link GoogleApiClient.ConnectionCallbacks}
             * it means that setting addConnectionCallbacks(this) we would call {@link #onConnected(Bundle)} method
             * as soon as we successfully connected to the ApiClient
             */
            googleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();

            locationRequest = new LocationRequest();
            locationRequest.setInterval(SMALL_UPDATE_PERIOD);
            locationRequest.setFastestInterval(FASTEST_UPDATE_PERIOD);
            locationRequest.setSmallestDisplacement(DISTANCE_RANGE);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            connect();
        }
    }

    private void connect() {
        if (vehicleId != 0) {
            googleApiClient.connect();
            getBatteryLevel();
            setTimer();
        }
    }

    private void setTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                sendRiderLocation();
            }
        }, SEND_DATA_INTERVAL, SEND_DATA_INTERVAL);
    }

    /**
     * LocationService implements google interface {@link LocationListener}
     * it means that setting "this" as a param we would call {@link #onLocationChanged(Location)}} method
     * after any location change
     */
    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
        }
    }

    /**
     * send an API call to retrieve all rider location activities.
     */
    private void retrieveVehicleDetectedActivity() {
        if (googleApiClient.isConnected()) {
            Intent intent = new Intent(this, VehicleDetectedActivityService.class);
            pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, getUpdatePeriod(), pendingIntent);
        }
    }

    /**
     * remove rider activity update callback
     */
    private void removeActivityUpdateCallback() {
        if (googleApiClient != null && googleApiClient.isConnected() && pendingIntent != null) {
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClient, pendingIntent);
        }
    }

    /**
     * We want to decrease rider battery consumption,
     * and to do so we change update interval depend on battery level
     * <p/>
     * in means that riders with low battery would receive and send location and activity updates
     * not that often to keep alive device as long as possible
     */
    private void requestUpToDateRiderActivity() {
        removeActivityUpdateCallback();
        retrieveVehicleDetectedActivity();
    }

    private void getBatteryLevel() {
        batteryLevelReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (currentLevel >= 0 && scale > 0) {
                    batteryLevel = (currentLevel * 100) / scale;
                    locationRequest.setInterval(getUpdatePeriod());
                    requestUpToDateRiderActivity();
                }
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    /**
     * get an update period for all kind of google map service to retrive both
     */
    private int getUpdatePeriod() {
        if (batteryLevel > BIG_BATTERY_LEVEL) {
            return SMALL_UPDATE_PERIOD;
        } else if (batteryLevel > LOW_BATTERY_LEVEL) {
            return MIDDLE_UPDATE_PERIOD;
        } else {
            return BIG_UPDATE_PERIOD;
        }
    }

    /**
     * send rider location list
     * and clean-up locations list
     * to get rid of copies that can be created during api call executing
     */
    private void sendRiderLocation() {
        if (vehicleId == 0 || locationList.isEmpty()) {
            return;
        }
        List<RiderLocation> sendingLocationList = new LinkedList<>(locationList);
        this.locationList.clear();

        apiManager.sendLocation(
            vehicleId,
            sendingLocationList);
    }

    /**
     * BroadcastReceiver is only one good way to inform from Service
     * to the Activity that something happened
     *
     * @param location new rider {@link Location}
     */
    private void sendLocationUpdate(Location location) {
        Intent intent = new Intent(Constants.LOCATION_UPDATED);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKeys.LOCATION, location);
        intent.putExtras(bundle);

        sendBroadcast(intent);
    }

    /**
     * Store new location to the list with additional information
     * such as accuracy speed and battery level
     * this data would be send with #sendRiderLocation() will be triggered
     *
     * @param location new rider location
     */
    private void storeLocation(Location location) {
        RiderLocation riderLocation = new RiderLocation();
        riderLocation.setGeoCoordinate(new GeoCoordinate(location.getLatitude(), location.getLongitude()));
        if (location.hasSpeed()) {
            riderLocation.setSpeedInKmh((int) location.getSpeed() * 1000 / 3600);
        }
        if (location.hasAccuracy()) {
            riderLocation.setAccuracyInMeters((int) location.getAccuracy());
        }
        riderLocation.setBatteryLevel(batteryLevel);
        riderLocation.setDateTime(DateTime.now());
        riderLocation.setVehicleDetectedActivity(storageManager.getVehicleDetectedActivity());

        locationList.add(riderLocation);

        //store last known rider location with all additional data such as battery level or speed
        storageManager.storeRiderLocation(riderLocation);
    }
}
