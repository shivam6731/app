package com.foodpanda.urbanninja.api.request;

import com.foodpanda.urbanninja.Config;
import com.foodpanda.urbanninja.api.model.CountryListWrapper;

import retrofit2.http.GET;
import rx.Observable;


public interface CountryService {
    @GET(Config.ApiUrbanNinjaTag.PARAMS)
    Observable<CountryListWrapper> getCountries();

}
