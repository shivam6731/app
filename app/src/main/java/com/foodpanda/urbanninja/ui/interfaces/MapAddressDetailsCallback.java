package com.foodpanda.urbanninja.ui.interfaces;

import com.foodpanda.urbanninja.model.GeoCoordinate;

/**
 * All action that can be done in details
 */
public interface MapAddressDetailsCallback {
    /**
     * Set enable the main action button
     * We need this logic for case when items were selected and it means that
     * button is enable to finish an order
     * however when this screen would be re-created all items would be not selected
     * and the button should be disabled
     *
     * @param isEnable is actionButton enable
     */
    void setActionButtonEnable(boolean isEnable);

    /**
     * Calls the activity callback to open the external map app
     * to show the clock-in point
     *
     * @param geoCoordinate coordinates to the clock-in point in a map
     * @param pinLabel      name of the place where rider should clock-in
     */
    void onSeeMapClicked(GeoCoordinate geoCoordinate, String pinLabel);

    /**
     * Calls the activity callback to open the external phone call app
     *
     * @param phoneNumber phone phoneNumber that should be called
     */
    void onPhoneNumberClicked(String phoneNumber);
}
