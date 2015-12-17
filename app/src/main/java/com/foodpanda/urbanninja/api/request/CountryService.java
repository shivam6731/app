package com.foodpanda.urbanninja.api.request;

import com.foodpanda.urbanninja.api.ApiTag;
import com.foodpanda.urbanninja.api.ApiUrbanNinjaTag;
import com.foodpanda.urbanninja.api.model.CountryWrapper;

import retrofit.Call;
import retrofit.http.GET;

public interface CountryService {
    @GET(ApiUrbanNinjaTag.PARAMS)
    Call<CountryWrapper> getCountries();

}
