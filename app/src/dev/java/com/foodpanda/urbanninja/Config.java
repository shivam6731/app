package com.foodpanda.urbanninja;

import com.foodpanda.urbanninja.api.ApiUrl;
import com.foodpanda.urbanninja.model.Country;

public class Config {
    public static class ApiBaseUrl {

        public static String getBaseUrl(Country country) {
            return ApiUrl.BASE_URL;
        }
    }

    public class ApiUrbanNinjaTag {
        //TODO replace params with UN2 one when https://foodpanda.atlassian.net/browse/LOGI-303 will be done
        public static final String PARAMS = "getmobilecountries?environment=staging&version=1.0&component=urbanninja";
    }

    public class ApiUrbanNinjaUrl {
        //TODO replace link with UN2 link when https://foodpanda.atlassian.net/browse/LOGI-303 will be done
        private static final String URL = "https://api-st.foodpanda.com/";
        private static final String CONFIG = "configuration/";
        public static final String BASE_URL = URL + CONFIG;
    }
}
