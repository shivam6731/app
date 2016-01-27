package com.foodpanda.urbanninja.ui.interfaces;

import android.location.Location;

/**
 * Used for receiving notifications from the {@link com.foodpanda.urbanninja.api.service.LocationService}
 * and with a BroadcastReceiver send it to Fragment that implement this interface
 * when the location has changed
 */
public interface LocationChangedCallback {
    void onLocationChanged(Location location);
}
