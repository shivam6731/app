package com.foodpanda.urbanninja.api.request;

import com.foodpanda.urbanninja.api.ApiTags;
import com.foodpanda.urbanninja.api.model.AuthRequest;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.model.Token;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface LogisticsService {
    @POST(ApiTags.AUTH_URL)
    Call<Token> auth(
        @Body AuthRequest authRequest);

    @GET(ApiTags.GET_RIDER_URL)
    Call<VehicleDeliveryAreaRiderBundle> getRider(@Path(ApiTags.USER_TAG) int riderId);

    @GET(ApiTags.GET_ROUTE_URL)
    Call<RouteWrapper> getRoute(@Path(ApiTags.VEHICLE_TAG) int vehicleId);

}
