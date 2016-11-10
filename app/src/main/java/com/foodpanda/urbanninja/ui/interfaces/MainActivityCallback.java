package com.foodpanda.urbanninja.ui.interfaces;

import android.support.annotation.NonNull;

import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.enums.CollectionIssueReason;
import com.foodpanda.urbanninja.model.enums.DialogType;

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
    void onPhoneSelected(@NonNull String phoneNumber);

    /**
     * Write the stop code as title for the main activity
     *
     * @param stop current stop
     */
    void writeCodeAsTitle(@NonNull Stop stop);

    /**
     * Write the title for the main activity
     *
     * @param title for the action bar with information about current selected fragment
     */
    void writeFragmentTitle(@NonNull String title);

    /**
     * In case when we can't retrieve user location
     * or GPS in disabled we need to redirect to the location settings
     */
    void onGPSSettingClicked();

    /**
     * Open dialog fragment with any type of information
     *
     * @param title       title of the dialog
     * @param message     message with details
     * @param buttonLabel label for button
     * @param dialogType  param to redirect after click ok
     */
    void showInformationDialog(CharSequence title, CharSequence message, CharSequence buttonLabel, DialogType dialogType);

    /**
     * Open dialog fragment with amount reason of collection issue and amount of collected money
     */
    void showCollectionIssueDialog();

    /**
     * Send collection issue API call to report it
     *
     * @param collectionAmount amount of money that had been collected
     * @param reason           reason of an issue
     */
    void sendCollectionIssue(double collectionAmount, CollectionIssueReason reason);

    /**
     * Open web page with intent.It means that third part browser app would open this web page
     *
     * @param url String with url to be converted to Uri
     *            and after this Uri would be passed to
     */
    void openWebPage(@NonNull String url);

    /**
     * show dialog to launch or continue vendor or customer issue collection activity
     *
     * @param dialogType to identify with what kind of issue you we are working
     */
    void showIssueDialog(DialogType dialogType);

    /**
     * In case when rider use fake location.
     * we need to force them to disable mock location
     * and to do so we redirect to the dev settings
     */
    void showDevSetting();
}
