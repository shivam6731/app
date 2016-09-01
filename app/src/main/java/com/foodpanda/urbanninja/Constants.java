package com.foodpanda.urbanninja;

public class Constants {
    public static final int SCHEDULE_ORDERS_REPORT_LIST_RANGE_DAYS = 15;
    public static final String LOCATION_UPDATED = "RiderLocationUpdated";
    public static final String PUSH_NOTIFICATION_RECEIVED = "pushNotificationReceived";
    public static final String PUSH_NOTIFICATION_TYPE = "type";
    public static final int SNACKBAR_DURATION_IN_MILLISECONDS = 1000 * 60;
    public static final int API_CALL_TIMEOUT_SECONDS = 120;

    public static class Preferences {
        public static final String TOKEN = "Token";
        public static final String USERNAME = "Username";
        public static final String PASSWORD = "Password";
        public static final String COUNTRY = "Country";
        public static final String LANGUAGE = "Language";
        public static final String STATUS_LIST = "StatusList";
        public static final String STATUS_REQUEST_LIST = "StatusRequestList";
        public static final String LOCATION_REQUEST_LIST = "LocationRequestList";
        public static final String VEHICLE_ID = "VEHICLE_ID";

        public static final String CACHED_REQUESTS_PREFERENCES_NAME = "CachedRequestListPreferenceName";
    }

    public static class BundleKeys {
        public static final String COUNTRY = "country";
        public static final String LANGUAGE = "language";
        public static final String VEHICLE_ID = "VehicleId";
        public static final String MAP_ADDRESS_DETAILS = "mapAddressDetails";
        public static final String IS_ROUTE_DETAILS_SHOWN = "isRouteDetailsShown";
        public static final String MAP_TOP_LAYOUT_SHOWN = "mapTopLayoutShown";
        public static final String MAP_ADDRESS_POINT_TYPE = "mapAddressPointType";
        public static final String SCHEDULE_WRAPPER = "ScheduleWrapper";
        public static final String LOCATION = "Location";
        public static final String STOP = "Stop";
        public static final String PUSH_NOTIFICATION_TYPE = "PushNotificationType";
        public static final String TITLE = "Title";
        public static final String MESSAGE = "Message";
        public static final String LABEL = "Label";
        public static final String DIALOG_TYPE = "DialogType";
    }
}
