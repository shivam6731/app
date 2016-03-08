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
        public static final String PARAMS = "endpoints?environment=production&component=fleet&version=1&platform=foodpanda";
    }

    public class ApiUrbanNinjaUrl {
        private static final String URL = "http://api.foodpanda.com/";
        private static final String CONFIG = "configuration/urban-ninja-v2/";
        public static final String BASE_URL = URL + CONFIG;
    }
}
