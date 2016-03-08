package com.foodpanda.urbanninja.api;

public class ApiTag {
    public static final String AUTH_URL = "v1/auth/oauth";
    public static final String GET_RIDER_URL = "v1/riders/{rider_id}";
    public static final String GET_ROUTE_URL = "v1/vehicles/{vehicle_id}/route";
    public static final String GET_SCHEDULE_URL = "v1/schedules";
    public static final String POST_SCHEDULE_CLOCK_IN_URL = "v1/schedules/{schedule_id}/clock-in";
    public static final String NOTIFY_ACTION_PERFORMED = "v1/routestops/{route_stop_id}/notify";
    public static final String REGISTRY_PUSH_NOTIFICATION = "v1/riders/{rider_id}/device";
    public static final String POST_LOCATION = "v1/vehicles/{vehicle_id}/locations";

    public static final String USER_TAG = "rider_id";
    public static final String VEHICLE_TAG = "vehicle_id";
    public static final String SCHEDULE_RIDER_TAG = "rider";
    public static final String SCHEDULE_START_TIME_TAG = "startAt";
    public static final String SCHEDULE_END_TIME_TAG = "endAt";
    public static final String SORT = "sort";
    public static final String SORT_VALUE = "+startAt";
    public static final String SCHEDULE_ID_TAG = "schedule_id";
    public static final String ROUTE_STOP_ID_TAG = "route_stop_id";

}
