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

}
