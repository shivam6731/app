package com.foodpanda.urbanninja.api;

public class ApiTag {
    public static final String AUTH_URL = "auth/oauth";
    public static final String GET_RIDER_URL = "riders/{rider_id}";
    public static final String GET_ROUTE_URL = "vehicles/{vehicle_id}/route";
    public static final String GET_SCHEDULE_URL = "schedules";
    public static final String POST_SCHEDULE_CLOCK_IN_URL = "schedules/{schedule_id}/clock-in";


    public static final String USER_TAG = "rider_id";
    public static final String VEHICLE_TAG = "vehicle_id";
    public static final String SCHEDULE_RIDER_TAG = "rider";
    public static final String SCHEDULE_START_TIME_TAG = "startTime";
    public static final String SCHEDULE_END_TIME_TAG = "endTime";
    public static final String SORT = "sort";
    public static final String SORT_VALUE = "+startTime";
    public static final String SCHEDULE_ID_TAG = "schedule_id";

}
