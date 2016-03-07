package com.foodpanda.urbanninja;

import android.text.TextUtils;

import com.foodpanda.urbanninja.api.ApiUrl;
import com.foodpanda.urbanninja.model.Country;

public class Config {
    public static class ApiBaseUrl {
        private static final String URL_SCHEME_TO_REPLACE_OLD = "urbanninja";
        private static final String URL_SCHEME_TO_REPLACE_NEW = "urbanninja2";

        public static String getBaseUrl(Country country) {
            if (country != null && !TextUtils.isEmpty(country.getUrl())) {
                //TODO fix country end point and replace url with normal one
                return country.getUrl().replace(URL_SCHEME_TO_REPLACE_OLD, URL_SCHEME_TO_REPLACE_NEW);
            } else {

                return ApiUrl.BASE_URL;
            }
        }
    }

    public class ApiUrbanNinjaTag {
        //TODO replace params with UN2 one when https://foodpanda.atlassian.net/browse/LOGI-303 will be done
        public static final String PARAMS = "getmobilecountries?environment=production&version=1.0&component=urbanninja";
    }

    public class ApiUrbanNinjaUrl {
        //TODO replace link with UN2 link when https://foodpanda.atlassian.net/browse/LOGI-303 will be done
        private static final String URL = "https://api.foodpanda.com/";
        private static final String CONFIG = "configuration/";
        public static final String BASE_URL = URL + CONFIG;
    }
}
