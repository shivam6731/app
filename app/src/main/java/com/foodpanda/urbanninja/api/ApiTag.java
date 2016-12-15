package com.foodpanda.urbanninja.api;

import com.foodpanda.urbanninja.Config;

public class ApiTag {
    public static final String AUTH_URL = "v1/auth/oauth";
    public static final String GET_RIDER_URL = "v1/riders/{rider_id}";
    public static final String GET_ROUTE_URL = "v1/vehicles/{vehicle_id}/route";
    public static final String GET_SCHEDULE_URL = "v1/schedules";
    public static final String POST_SCHEDULE_CLOCK_IN_URL = "v1/schedules/{schedule_id}/clock-in";
    public static final String NOTIFY_ACTION_PERFORMED = "v1/routestops/{route_stop_id}/notify";
    public static final String REGISTER_UN_REGISTER_PUSH_NOTIFICATION = "v1/riders/{rider_id}/device";
    public static final String POST_LOCATION = "v1/vehicles/{vehicle_id}/locations";
    public static final String ORDERS_REPORT = "v1/riders/{rider_id}/report";
    public static final String REPORT_COLLECTION_ISSUE = "v1/cash-collection";

    public static final String USER_TAG = "rider_id";
    public static final String VEHICLE_TAG = "vehicle_id";
    public static final String SCHEDULE_RIDER_TAG = "rider";
    public static final String START_TIME_TAG = "startAt";
    public static final String END_TIME_TAG = "endAt";
    public static final String SORT = "sort";
    public static final String SORT_VALUE = "+startAt";
    public static final String SCHEDULE_ID_TAG = "schedule_id";
    public static final String ROUTE_STOP_ID_TAG = "route_stop_id";
    public static final String ORDER_REPORT_TIME_ZONE = "timezone";
    public static final String AUTHORIZATION = "Authorization";

    public static final class ApiHockeyAppTag {
        public static final String APP_VERSIONS_URL = "api/2/apps/{api_key}/app_versions";
        public static final String API_KEY_TAG = "api_key";

        public static final String APP_VERSIONS_HEADER = "X-HockeyAppToken: " + Config.HOCKEY_APP_API_READ_ONLY_TOKEN;
    }

}
