package com.foodpanda.urbanninja;

import android.text.TextUtils;

import com.foodpanda.urbanninja.api.UrbanNinjaApiUrl;
import com.foodpanda.urbanninja.model.Country;

public class Config {
    public static class ApiBaseUrl {

        public static String getBaseUrl(Country country) {
            if (country != null && !TextUtils.isEmpty(country.getUrl())) {

                return country.getUrl();
            } else {

                return UrbanNinjaApiUrl.BASE_URL;
            }
        }
    }

    public class ApiUrbanNinjaTag {
        private static final String ENVIRONMENT_VALUE = "staging";
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
    public static final boolean IS_FAKE_LOCATION_ALLOWED = true;

    /**
     * To force rider to use up-to-date version of the app
     * we need to integrate last available version check
     * <p/>
     * to do so we need to use hockey_app API
     * and we need this read only token
     */
    public static final String HOCKEY_APP_API_READ_ONLY_TOKEN = "abf78b08e44b49b3902d30a2e96523a3";

}
