package com.foodpanda.urbanninja.api.request;

import com.foodpanda.urbanninja.api.ApiTag;
import com.foodpanda.urbanninja.api.model.AuthRequest;
import com.foodpanda.urbanninja.api.model.OrdersReportCollection;
import com.foodpanda.urbanninja.api.model.PerformActionWrapper;
import com.foodpanda.urbanninja.api.model.PushNotificationRegistrationWrapper;
import com.foodpanda.urbanninja.api.model.RiderLocationCollectionWrapper;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleCollectionWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.model.Rider;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.Token;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;

import org.joda.time.DateTime;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface LogisticsService {
    @POST(ApiTag.AUTH_URL)
    Call<Token> auth(
        @Body AuthRequest authRequest);

    @GET(ApiTag.GET_RIDER_URL)
    Call<VehicleDeliveryAreaRiderBundle> getRider(@Path(ApiTag.USER_TAG) int riderId);

    @GET(ApiTag.GET_ROUTE_URL)
    Call<RouteWrapper> getRoute(@Path(ApiTag.VEHICLE_TAG) int vehicleId);

    @GET(ApiTag.GET_SCHEDULE_URL)
    Call<ScheduleCollectionWrapper> getRiderSchedule(
        @Query(ApiTag.SCHEDULE_RIDER_TAG) int riderId,
        @Query(ApiTag.START_TIME_TAG) DateTime startAt,
        @Query(ApiTag.END_TIME_TAG) DateTime endAt,
        @Query(ApiTag.SORT) String sort);

    @POST(ApiTag.POST_SCHEDULE_CLOCK_IN_URL)
    Call<ScheduleWrapper> clockInSchedule(@Path(ApiTag.SCHEDULE_ID_TAG) int scheduleId);

    @PUT(ApiTag.NOTIFY_ACTION_PERFORMED)
    Call<Stop> notifyActionPerformed(
        @Path(ApiTag.ROUTE_STOP_ID_TAG) long routeId,
        @Body PerformActionWrapper performActionWrapper);

    @PUT(ApiTag.REGISTRY_PUSH_NOTIFICATION)
    Call<Rider> registerDeviceId(
        @Path(ApiTag.USER_TAG) int riderId,
        @Body PushNotificationRegistrationWrapper pushNotificationRegistrationWrapper);

    @POST(ApiTag.POST_LOCATION)
    Call<RiderLocationCollectionWrapper> sendLocation(
        @Path(ApiTag.VEHICLE_TAG) int vehicleId,
        @Body RiderLocationCollectionWrapper riderLocation);

    @GET(ApiTag.ORDERS_REPORT)
    Call<OrdersReportCollection> getOrdersReport(
        @Path(ApiTag.USER_TAG) int riderId,
        @Query(ApiTag.START_TIME_TAG) DateTime startAt,
        @Query(ApiTag.END_TIME_TAG) DateTime endAt,
        @Query(ApiTag.ORER_REPORT_TIME_ZONE) String timezone);
}
