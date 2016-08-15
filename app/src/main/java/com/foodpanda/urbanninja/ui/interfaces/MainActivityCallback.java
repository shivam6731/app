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

    /**
     * Write the title for the main activity
     *
     * @param title for the action bar with information about current selected fragment
     */
    void writeFragmentTitle(String title);

    /**
     * In case when we can't retrieve user location
     * or GPS in disabled we need to redirect to the location settings
     */
    void onGPSSettingClicked();

    /**
     * Open dialog fragment with any type of information
     *
     * @param title                title of the dialog
     * @param message              message with details
     * @param buttonLabel          label for button
     * @param redirectToGPSSetting param to open GPS settings
     */
    void showInformationDialog(CharSequence title, CharSequence message, CharSequence buttonLabel, boolean redirectToGPSSetting);
}
