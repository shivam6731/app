package com.foodpanda.urbanninja.model;

/**
 * Provides all information that is required for map details fragment
 * such as coordinates, address, name and comments
 */
public interface MapDetailsProvider extends ParcelableModel {
    /**
     * get coordinate to be shown as a destination point
     *
     * @return coordinate of destination point
     */
    GeoCoordinate getCoordinate();

    /**
     * get address to be shown as a destination point address
     *
     * @return address
     */
    String getAddress();

    /**
     * get comment for current task
     *
     * @return comment
     */
    String getComment();

    /**
     * get name to be shown as a destination point name
     *
     * @return name
     */
    String getName();

    /**
     * get a phone number that would be called if call button is clicked
     *
     * @return phone number
     */
    String getPhoneNumber();

    /**
     * we don't need to show done checkbox in some cases
     *
     * @return show for done button
     */
    boolean isDoneButtonVisible();
}
