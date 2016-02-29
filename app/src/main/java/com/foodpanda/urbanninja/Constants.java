package com.foodpanda.urbanninja;

public class Constants {
    public static final int SCHEDULE_LIST_RANGE = 15;
    public static final String LOCATION_UPDATED = "RiderLocationUpdated";
    public static final int SNACKBAR_DURATION_IN_MILLISECONDS = 1000 * 60;

    public static class Preferences {
        public static final String TOKEN = "Token";
        public static final String USERNAME = "Username";
        public static final String PASSWORD = "Password";
        public static final String COUNTRY = "Country";
        public static final String ACTION_LIST = "ActionList";
        public static final String ACTION_REQUEST_LIST = "ActionRequestList";
        public static final String LOCATION_REQUEST_LIST = "LocationRequestList";
        public static final String VEHICLE_ID = "VEHICLE_ID";

        public static final String CACHED_REQUESTS_PREFERENCES_NAME = "CachedRequestListPreferenceName";
    }

    public static class BundleKeys {
        public static final String COUNTRY = "country";
        public static final String VEHICLE_ID = "VehicleId";
        public static final String VEHICLE_DELIVERY_AREA_RIDER_BUNDLE = "VehicleDeliveryAreaRiderBundle";
        public static final String SCHEDULE_WRAPPER = "ScheduleWrapper";
        public static final String LOCATION = "Location";
        public static final String STOP = "Stop";
        public static final String PUSH_NOTIFICATION_TYPE = "PushNotificationType";
    }
}
