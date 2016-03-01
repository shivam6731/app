package com.foodpanda.urbanninja.ui.interfaces;

import com.foodpanda.urbanninja.model.GeoCoordinate;

/**
 * Interface to communicate with  activities and fragment from
 * MainActivity's children
 */
public interface MainActivityCallback {
    /**
     * Open third par maps app with intent
     * @param geoCoordinate coordinates to the point
     * @param pinLabel label that should be shown with point
     */
    void onSeeMapClicked(GeoCoordinate geoCoordinate, String pinLabel);
}
