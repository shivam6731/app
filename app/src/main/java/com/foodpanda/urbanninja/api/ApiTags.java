package com.foodpanda.urbanninja.api;

public class ApiTags {
    public static final String AUTH_URL = "auth/oauth";
    public static final String GET_RIDER_URL = "riders/{rider_id}";
    public static final String GET_ROUTE_URL = "vehicles/{vehicle_id}/route";


    public static final String USER_TAG = "rider_id";
    public static final String VEHICLE_TAG = "vehicle_id";

    public class Country {
        public static final String PARAMS = "getmobilecountries?environment=staging&version=1.0&component=urbanninja";
    }
}
