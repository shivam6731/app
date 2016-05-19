package com.foodpanda.urbanninja.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Config;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.api.ApiTag;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.BaseCallback;
import com.foodpanda.urbanninja.api.RetryActionCallback;
import com.foodpanda.urbanninja.api.RetryLocationCallback;
import com.foodpanda.urbanninja.api.StorableApiCallback;
import com.foodpanda.urbanninja.api.client.CancelableOkHttpClient;
import com.foodpanda.urbanninja.api.model.AuthRequest;
import com.foodpanda.urbanninja.api.model.CountryListWrapper;
import com.foodpanda.urbanninja.api.model.OrdersReportCollection;
import com.foodpanda.urbanninja.api.model.PerformActionWrapper;
import com.foodpanda.urbanninja.api.model.PushNotificationRegistrationWrapper;
import com.foodpanda.urbanninja.api.model.RiderLocation;
import com.foodpanda.urbanninja.api.model.RiderLocationCollectionWrapper;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleCollectionWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.api.request.CountryService;
import com.foodpanda.urbanninja.api.request.LogisticsService;
import com.foodpanda.urbanninja.api.serializer.DateTimeDeserializer;
import com.foodpanda.urbanninja.model.Rider;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.Token;
import com.foodpanda.urbanninja.model.TokenData;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.model.enums.Status;
import com.foodpanda.urbanninja.utils.DateUtil;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiManager implements Managable {
    private LogisticsService service;
    private CountryService countryService;
    private StorageManager storageManager;
    private Retrofit retrofit;

    @Override
    public void init(Context context) {
        storageManager = App.STORAGE_MANAGER;
        initService();
    }

    private void initService() {
        OkHttpClient httpClient = new CancelableOkHttpClient.Builder().
            addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder build = chain.request().newBuilder().addHeader("Accept", "application/json");
                    Token token = storageManager.getToken();
                    if (token != null) {
                        build.addHeader("Authorization", token.getTokenType() +
                            " " +
                            token.getAccessToken())
                            .build();
                    }

                    return chain.proceed(build.build());
                }
            }).build();

        retrofit = new Retrofit.Builder()
            .baseUrl(Config.ApiBaseUrl.getBaseUrl(storageManager.getCountry()))
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .client(httpClient)
            .build();

        service = retrofit.create(LogisticsService.class);

        retrofit = new Retrofit.Builder().
            baseUrl(Config.ApiUrbanNinjaUrl.BASE_URL).
            addConverterFactory(GsonConverterFactory.create(createCountryGson())).
            build();
        countryService = retrofit.create(CountryService.class);
        sendAllFailedRequests();
    }

    private Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeDeserializer());

        return gsonBuilder.create();
    }

    private Gson createCountryGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES);

        return gsonBuilder.create();
    }

    public void login(
        String username,
        String password,
        @NonNull final BaseApiCallback<Token> tokenBaseApiCallback
    ) {
        AuthRequest authRequest = new AuthRequest(username, password);
        Call<Token> call = service.auth(authRequest);
        call.enqueue(new BaseCallback<Token>(tokenBaseApiCallback, call) {
            @Override
            public void onResponse(Call<Token> call, retrofit2.Response<Token> response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    storageManager.storeToken(response.body());
                    initService();
                    tokenBaseApiCallback.onSuccess(response.body());
                }
            }
        });
    }

    public void getCurrentRider(@NonNull final BaseApiCallback<VehicleDeliveryAreaRiderBundle> riderBundleBaseApiCallback) {
        TokenData tokenData = storageManager.getTokenData();
        if (tokenData != null) {
            Call<VehicleDeliveryAreaRiderBundle> call = service.getRider(tokenData.getUserId());
            call.enqueue(new BaseCallback<VehicleDeliveryAreaRiderBundle>(riderBundleBaseApiCallback, call) {

                @Override
                public void onResponse(Call<VehicleDeliveryAreaRiderBundle> call, retrofit2.Response<VehicleDeliveryAreaRiderBundle> response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        riderBundleBaseApiCallback.onSuccess(response.body());
                    }
                }
            });
        }

    }

    public void getRoute(
        int vehicleId,
        @NonNull final BaseApiCallback<RouteWrapper> baseApiCallback
    ) {
        Call<RouteWrapper> call = service.getRoute(vehicleId);
        call.enqueue(new BaseCallback<RouteWrapper>(baseApiCallback, call) {
            @Override
            public void onResponse(Call<RouteWrapper> call, retrofit2.Response<RouteWrapper> response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    storageManager.storeStopList(response.body().getStops());
                    baseApiCallback.onSuccess(response.body());
                }
            }
        });

    }

    public void getCurrentSchedule(
        BaseApiCallback<ScheduleCollectionWrapper> baseApiCallback
    ) {
        DateTime dateTimeNow = DateTime.now();
        DateTime datePlusOneDay = DateTime.now().plusDays(1);

        getScheduleList(dateTimeNow, datePlusOneDay, baseApiCallback);
    }

    public void getScheduleList(BaseApiCallback<ScheduleCollectionWrapper> baseApiCallback
    ) {
        DateTime dateTimeNow = DateTime.now();
        DateTime dateTimeEnd = DateTime.now().plusDays(Constants.SCHEDULE_ORDERS_REPORT_LIST_RANGE_DAYS);

        getScheduleList(dateTimeNow, dateTimeEnd, baseApiCallback);
    }

    private void getScheduleList(DateTime dateTimeStart,
                                 DateTime dateTimeEnd,
                                 @NonNull final BaseApiCallback<ScheduleCollectionWrapper> baseApiCallback
    ) {
        TokenData tokenData = storageManager.getTokenData();

        Call<ScheduleCollectionWrapper> call = service.getRiderSchedule(
            tokenData.getUserId(),
            dateTimeStart,
            dateTimeEnd,
            ApiTag.SORT_VALUE);

        call.enqueue(new BaseCallback<ScheduleCollectionWrapper>(baseApiCallback, call) {
            @Override
            public void onResponse(Call<ScheduleCollectionWrapper> call, retrofit2.Response<ScheduleCollectionWrapper> response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    baseApiCallback.onSuccess(response.body());
                }
            }

        });
    }

    public void scheduleClockIn(
        int scheduleId,
        @NonNull final BaseApiCallback<ScheduleWrapper> baseApiCallback
    ) {
        Call<ScheduleWrapper> call = service.clockInSchedule(scheduleId);
        call.enqueue(new BaseCallback<ScheduleWrapper>(baseApiCallback, call) {
            @Override
            public void onResponse(Call<ScheduleWrapper> call, retrofit2.Response<ScheduleWrapper> response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    baseApiCallback.onSuccess(response.body());
                }
            }

        });
    }

    public void notifyActionPerformed(long routeId, Status status) {
        PerformActionWrapper performActionWrapper = new PerformActionWrapper(status, new DateTime());
        Call<Stop> call = service.notifyActionPerformed(routeId, performActionWrapper);
        call.enqueue(new RetryActionCallback<>(call, routeId, performActionWrapper));
    }

    public void sendLocation(
        int vehicleId,
        List<RiderLocation> riderLocation,
        @NonNull final StorableApiCallback<RiderLocationCollectionWrapper> baseApiCallback) {

        RiderLocationCollectionWrapper riderLocationCollectionWrapper = new RiderLocationCollectionWrapper();
        riderLocationCollectionWrapper.addAll(riderLocation);

        Call<RiderLocationCollectionWrapper> call = service.sendLocation(vehicleId, riderLocationCollectionWrapper);
        call.enqueue(new RetryLocationCallback<RiderLocationCollectionWrapper>(
            baseApiCallback,
            call,
            vehicleId,
            riderLocationCollectionWrapper) {
            @Override
            public void onResponse(Call<RiderLocationCollectionWrapper> call, retrofit2.Response<RiderLocationCollectionWrapper> response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    baseApiCallback.onSuccess(response.body());
                }
            }
        });
    }

    public void sendAllFailedRequests() {
        ApiQueue.getInstance().resendRequests(service);
    }

    public void registerDeviceId(String token) {
        if (!TextUtils.isEmpty(token)) {
            TokenData tokenData = storageManager.getTokenData();

            Call<Rider> call = service.registerDeviceId(
                tokenData.getUserId(),
                new PushNotificationRegistrationWrapper(token));

            call.enqueue(new BaseCallback<Rider>(null, call) {
            });
        }
    }

    public void getWorkingDayReport(BaseApiCallback<OrdersReportCollection> baseApiCallback) {
        DateTime dateTimeStart = DateTime.now().withTimeAtStartOfDay().minusDays(Constants.SCHEDULE_ORDERS_REPORT_LIST_RANGE_DAYS);
        DateTime dateTimeEnd = DateTime.now();
        getWorkingDayReport(
            dateTimeStart,
            dateTimeEnd,
            DateUtil.formatTimeZone(dateTimeStart.toDate()),
            baseApiCallback);
    }

    private void getWorkingDayReport(
        DateTime startAt,
        DateTime endAt,
        String timezone,
        @NonNull final BaseApiCallback<OrdersReportCollection> baseApiCallback) {
        TokenData tokenData = storageManager.getTokenData();

        Call<OrdersReportCollection> call = service.getOrdersReport(tokenData.getUserId(), startAt, endAt, timezone);
        call.enqueue(new BaseCallback<OrdersReportCollection>(baseApiCallback, call) {
            @Override
            public void onResponse(Call<OrdersReportCollection> call, retrofit2.Response<OrdersReportCollection> response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    baseApiCallback.onSuccess(response.body());
                }
            }

        });
    }

    //Internal foodpanda API
    public void getCountries(final BaseApiCallback<CountryListWrapper> baseApiCallback) {
        Call<CountryListWrapper> call = countryService.getCountries();
        call.enqueue(new BaseCallback<CountryListWrapper>(baseApiCallback, call) {
            @Override
            public void onResponse(Call<CountryListWrapper> call, retrofit2.Response<CountryListWrapper> response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    baseApiCallback.onSuccess(response.body());
                }
            }
        });
    }

    /**
     * logout from rider from api side
     * cancel all API requests that are in flight right now
     * and un-subscribe from push notification for current rider
     */
    public void logout() {
//        service.
//        retrofit.
//        retrofit.callAdapterFactories().get(0).caclient().cancel(CancelableOkHttpClient.TAG_CALL);
    }
}
