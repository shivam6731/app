package com.foodpanda.urbanninja.ui.interfaces;

import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Stop;

/**
 * Interface to communicate with  activities and fragment from
 * MainActivity's children
 */
public interface MainActivityCallback {
    /**
     * Open third par maps app with intent
     *
     * @param geoCoordinate coordinates to the point
     * @param pinLabel      label that should be shown with point
     */
    void onSeeMapClicked(GeoCoordinate geoCoordinate, String pinLabel);

    /**
     * Open third part phone app
     * to call to the customer to the restaurant or to the manager
     *
     * @param phoneNumber phone number that will be send to the call app
     */
    void onPhoneSelected(String phoneNumber);

    /**
     * Write the stop code as title for the main activity
     *
     * @param stop current stop
     */
    void writeCodeAsTitle(Stop stop);
}
