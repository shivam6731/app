package com.foodpanda.urbanninja.api.request;

import com.foodpanda.urbanninja.Config;
import com.foodpanda.urbanninja.api.model.CountryListWrapper;

import retrofit.Call;
import retrofit.http.GET;

public interface CountryService {
    @GET(Config.ApiUrbanNinjaTag.PARAMS)
    Call<CountryListWrapper> getCountries();

}
