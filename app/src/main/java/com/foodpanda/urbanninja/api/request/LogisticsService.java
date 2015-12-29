package com.foodpanda.urbanninja.api.request;

import com.foodpanda.urbanninja.api.ApiTag;
import com.foodpanda.urbanninja.api.model.AuthRequest;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.model.Token;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface LogisticsService {
    @POST(ApiTag.AUTH_URL)
    Call<Token> auth(
        @Body AuthRequest authRequest);

    @GET(ApiTag.GET_RIDER_URL)
    Call<VehicleDeliveryAreaRiderBundle> getRider(@Path(ApiTag.USER_TAG) int riderId);

    @GET(ApiTag.GET_ROUTE_URL)
    Call<RouteWrapper> getRoute(@Path(ApiTag.VEHICLE_TAG) int vehicleId);

    @GET(ApiTag.GET_SCHEDULE_URL)
    Call<List<ScheduleWrapper>> getRiderSchedule(@Query(ApiTag.SCHEDULE_RIDER_TAG) int riderId);

    @POST(ApiTag.POST_SCHEDULE_CLOCK_IN_URL)
    Call<ScheduleWrapper> clockInSchedule(@Path(ApiTag.SCHEDULE_ID_TAG) int scheduleId);

}
