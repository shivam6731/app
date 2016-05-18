package com.foodpanda.urbanninja.ui.interfaces;

import android.location.Location;

import com.foodpanda.urbanninja.model.enums.Status;

/**
 * All cases that can has influence to the map address fragment
 */
public interface MapAddressDetailsChangeListener {
    /**
     * Calls every time when receive new location from location service
     *
     * @param location new rider location
     */
    void onLocationChanged(Location location);

    /**
     * When driver at status that is not {@link Status#ON_THE_WAY}
     * checkbox should be not visible
     *
     * @param isVisible set visibility
     */
    void setActionDoneCheckboxVisibility(boolean isVisible);

}
