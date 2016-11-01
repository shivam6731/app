package com.foodpanda.urbanninja;

import android.text.TextUtils;

import com.foodpanda.urbanninja.api.ApiUrl;
import com.foodpanda.urbanninja.model.Country;

public class Config {
    public static class ApiBaseUrl {

        public static String getBaseUrl(Country country) {
            if (country != null && !TextUtils.isEmpty(country.getUrl())) {

                return country.getUrl();
            } else {

                return ApiUrl.BASE_URL;
            }
        }
    }

    public class ApiUrbanNinjaTag {
        private static final String ENVIRONMENT_VALUE = "production";
        private static final String COMPONENT_VALUE = "fleet";
        private static final String VERSION_VALUE = "1";
        private static final String PLATFORM_VALUE = "foodpanda";

        public static final String PARAMS =
            "endpoints?" +
                "environment=" +
                ENVIRONMENT_VALUE +
                "&component=" +
                COMPONENT_VALUE +
                "&version=" +
                VERSION_VALUE +
                "&platform=" +
                PLATFORM_VALUE;
    }

    public class ApiUrbanNinjaUrl {
        private static final String URL = "http://api.foodpanda.com/";
        private static final String CONFIG = "configuration/urban-ninja-v2/";
        public static final String BASE_URL = URL + CONFIG;
    }

    /**
     * For production and beta production we don't allow to use mock location,
     * however we allow it for dev and staging environments.
     */
    public static final boolean IS_FAKE_LOCATION_ALLOWED = false;

}
