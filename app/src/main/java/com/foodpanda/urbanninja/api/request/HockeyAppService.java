package com.foodpanda.urbanninja.api.request;

import com.foodpanda.urbanninja.api.ApiTag;
import com.foodpanda.urbanninja.api.model.HockeyAppVersionList;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import rx.Observable;

public interface HockeyAppService {
    @Headers(ApiTag.ApiHockeyAppTag.APP_VERSIONS_HEADER)
    @GET(ApiTag.ApiHockeyAppTag.APP_VERSIONS_URL)
    Observable<HockeyAppVersionList> getAppVersions(
        @Path(ApiTag.ApiHockeyAppTag.API_KEY_TAG) String apiKey);
}
