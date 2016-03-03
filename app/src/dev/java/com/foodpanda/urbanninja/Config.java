package com.foodpanda.urbanninja;

import com.foodpanda.urbanninja.api.ApiUrl;
import com.foodpanda.urbanninja.model.Country;

public class Config {
    public static class ApiBaseUrl {

        public static String getBaseUrl(Country country) {
            return ApiUrl.BASE_URL;
        }
    }

}
