package com.foodpanda.urbanninja.api.request;

import com.foodpanda.urbanninja.api.ApiTag;
import com.foodpanda.urbanninja.api.model.AuthRequest;
import com.foodpanda.urbanninja.api.model.CashCollectionIssueList;
import com.foodpanda.urbanninja.api.model.CashCollectionIssueWrapper;
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

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;


public interface LogisticsService {
    @POST(ApiTag.AUTH_URL)
    Observable<Token> auth(@Body AuthRequest authRequest);

    @GET(ApiTag.GET_RIDER_URL)
    Observable<VehicleDeliveryAreaRiderBundle> getRider(@Path(ApiTag.USER_TAG) int riderId);

    @GET(ApiTag.GET_ROUTE_URL)
    Observable<RouteWrapper> getRoute(@Path(ApiTag.VEHICLE_TAG) int vehicleId);

    @GET(ApiTag.GET_SCHEDULE_URL)
    Observable<ScheduleCollectionWrapper> getRiderSchedule(
        @Query(ApiTag.SCHEDULE_RIDER_TAG) int riderId,
        @Query(ApiTag.START_TIME_TAG) DateTime startAt,
        @Query(ApiTag.END_TIME_TAG) DateTime endAt,
        @Query(ApiTag.SORT) String sort
    );

    @POST(ApiTag.POST_SCHEDULE_CLOCK_IN_URL)
    Observable<ScheduleWrapper> clockInSchedule(@Path(ApiTag.SCHEDULE_ID_TAG) int scheduleId);

    @PUT(ApiTag.NOTIFY_ACTION_PERFORMED)
    Observable<Stop> notifyActionPerformed(
        @Path(ApiTag.ROUTE_STOP_ID_TAG) long routeId,
        @Body PerformActionWrapper performActionWrapper
    );

    @PUT(ApiTag.REGISTER_UN_REGISTER_PUSH_NOTIFICATION)
    Observable<Rider> registerDeviceId(
        @Path(ApiTag.USER_TAG) int riderId,
        @Body PushNotificationRegistrationWrapper pushNotificationRegistrationWrapper
    );

    @POST(ApiTag.POST_LOCATION)
    Observable<RiderLocationCollectionWrapper> sendLocation(
        @Path(ApiTag.VEHICLE_TAG) int vehicleId,
        @Body RiderLocationCollectionWrapper riderLocation
    );

    @GET(ApiTag.ORDERS_REPORT)
    Observable<OrdersReportCollection> getOrdersReport(
        @Path(ApiTag.USER_TAG) int riderId,
        @Query(ApiTag.START_TIME_TAG) DateTime startAt,
        @Query(ApiTag.END_TIME_TAG) DateTime endAt,
        @Query(ApiTag.ORDER_REPORT_TIME_ZONE) String timezone
    );

    @PUT(ApiTag.REPORT_COLLECTION_ISSUE)
    Observable<CashCollectionIssueList> reportCollectionIssue(@Body List<CashCollectionIssueWrapper> cashCollectionIssueWrappers);

    @DELETE(ApiTag.REGISTER_UN_REGISTER_PUSH_NOTIFICATION)
    Observable<Rider> unregisterDeviceId(
        @Header(ApiTag.AUTHORIZATION) String header,
        @Path(ApiTag.USER_TAG) int riderId);

}
