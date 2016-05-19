package com.foodpanda.urbanninja.api.request;

import com.foodpanda.urbanninja.Config;
import com.foodpanda.urbanninja.api.model.CountryListWrapper;

import retrofit2.Call;
import retrofit2.http.GET;


public interface CountryService {
    @GET(Config.ApiUrbanNinjaTag.PARAMS)
    Call<CountryListWrapper> getCountries();

}
