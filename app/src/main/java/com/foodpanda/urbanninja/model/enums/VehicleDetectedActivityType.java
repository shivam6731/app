package com.foodpanda.urbanninja.model.enums;

import com.google.android.gms.location.DetectedActivity;

/**
 * To get android specific information about this activity check documentation
 * https://developers.google.com/android/reference/com/google/android/gms/location/DetectedActivity#getConfidence()
 */
public enum VehicleDetectedActivityType {
    /**
     * The device is in a vehicle, such as a car.
     */
    IN_VEHICLE,
    /**
     * The device is on a bicycle.
     */
    ON_BICYCLE,
    /**
     * The device is on a user who is walking or running.
     */
    ON_FOOT,
    /**
     * The device is on a user who is running.
     */
    RUNNING,
    /**
     * The device is still (not moving).
     */
    STILL,
    /**
     * The device angle relative to gravity changed significantly.
     */
    TILTING,
    /**
     * Unable to detect the current activity.
     */
    UNKNOWN,
    /**
     * The device is on a user who is walking.
     */
    WALKING;

    /**
     * get rider activity type from android int constant value
     *
     * @param value constant value from google play service
     * @return type of rider activity
     */
    public static VehicleDetectedActivityType fromInteger(int value) {
        switch (value) {
            case DetectedActivity.IN_VEHICLE:
                return IN_VEHICLE;
            case DetectedActivity.ON_BICYCLE:
                return ON_BICYCLE;
            case DetectedActivity.ON_FOOT:
                return ON_FOOT;
            case DetectedActivity.RUNNING:
                return RUNNING;
            case DetectedActivity.STILL:
                return STILL;
            case DetectedActivity.TILTING:
                return TILTING;
            case DetectedActivity.UNKNOWN:
                return UNKNOWN;
            case DetectedActivity.WALKING:
                return WALKING;
            default:
                return UNKNOWN;
        }
    }
}
