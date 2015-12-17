package com.foodpanda.urbanninja.api.request;

import com.foodpanda.urbanninja.api.ApiUrbanNinjaTag;
import com.foodpanda.urbanninja.api.model.CountryListWrapper;

import retrofit.Call;
import retrofit.http.GET;

public interface CountryService {
    @GET(ApiUrbanNinjaTag.PARAMS)
    Call<CountryListWrapper> getCountries();

}
