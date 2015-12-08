package com.foodpanda.urbanninja.api.request;

import com.foodpanda.urbanninja.api.ApiTags;
import com.foodpanda.urbanninja.api.model.AuthRequest;
import com.foodpanda.urbanninja.model.Token;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

public interface LogisticsService {
    @POST(ApiTags.AUTH_URL)
    Call<Token> auth(
        @Body AuthRequest authRequest);
}
