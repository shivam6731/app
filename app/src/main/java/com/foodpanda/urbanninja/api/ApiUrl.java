package com.foodpanda.urbanninja.api;

public class ApiUrl {
    private static final String SCHEME = "http://";
    private static final String URL = "cis-fleet-dev-2.elasticbeanstalk.com/";
    private static final String VERSION = "v1/";

    public static final String BASE_URL = SCHEME + URL + VERSION;

    public class Country {
        private static final String URL = "https://api-st.foodpanda.com/";
        private static final String CONFIG = "configuration/";
        public static final String BASE_URL = URL + CONFIG;
    }
}
