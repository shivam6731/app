package com.foodpanda.urbanninja.ui.interfaces;

import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.model.GeoCoordinate;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;

/**
 * Interface describe all possible state for rider working work flow
 * from clock-in to clock-out
 */
public interface NestedFragmentCallback {
    /**
     * Calls the activity callback to open the external map app
     *
     * @param geoCoordinate coordinates to the clock-in point in a map
     * @param pinLabel      name of the place where rider should clock-in
     */
    void onSeeMapClicked(GeoCoordinate geoCoordinate, String pinLabel);

    /**
     * Call the activity callback to open the external phone call app
     *
     * @param phoneNumber phone phoneNumber that would be called
     */
    void onPhoneNumberClicked(String phoneNumber);

    /**
     * Change the main action button state
     *
     * @param isVisible   is action button is visible
     * @param textResLink link to the android resources to set the text for button
     */
    void setActionButtonVisible(boolean isVisible, int textResLink);

    /**
     * Set visibility the main action button
     * We need this logic for case when items were selected and it means that
     * button is enable to finish an order
     * however when this screen would be re-created all items would be not selected
     * and the button should be invisible
     *
     * @param isVisible is actionButton visible
     */
    void setActionButtonVisible(boolean isVisible);

    /**
     * Open clock-in fragment if rider as a schedule and doesn't clock-in jet
     *
     * @param scheduleWrapper current rider schedule where he has to clock-in
     */
    void openReadyToWork(ScheduleWrapper scheduleWrapper);

    /**
     * Open empty list screen if rider is clocked-in and doesn't have any order to do
     *
     * @param vehicleDeliveryAreaRiderBundle information about rider to send api request to retrieve the
     *                                       order list
     */
    void openEmptyListFragment(VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle);

    /**
     * Open both of route details screen and route action screen depend on order state
     *
     * @param stop order that should be opened
     */
    void openRoute(Stop stop);

    /**
     * Open screen with just a progress bar inside
     * shows only when we load the data from the server side for the first time
     */
    void openLoadFragment();

    /**
     * Notify fragment that some api request is finished and we have to hide progress bar
     * or other indicator
     */
    void hideProgressIndicator();

    /**
     * Enable swipe to refresh feature when we reached
     * the top on the scroll view
     * to prevent miss calling of update logic
     *
     * @param enable set enable to the swipe view
     */
    void setSwipeToRefreshEnable(boolean enable);
}
