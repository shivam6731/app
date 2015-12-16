package com.foodpanda.urbanninja.api.request;

import com.foodpanda.urbanninja.api.ApiTags;
import com.foodpanda.urbanninja.api.model.CountryWrapper;

import retrofit.Call;
import retrofit.http.GET;

public interface CountryService {
    @GET(ApiTags.Country.PARAMS)
    Call<CountryWrapper> getCountries();

}
