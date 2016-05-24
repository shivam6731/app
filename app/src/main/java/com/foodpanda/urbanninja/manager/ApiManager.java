package com.foodpanda.urbanninja.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Config;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.api.ApiTag;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.BaseCallback;
import com.foodpanda.urbanninja.api.RetryActionCallback;
import com.foodpanda.urbanninja.api.RetryLocationCallback;
import com.foodpanda.urbanninja.api.StorableApiCallback;
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
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ApiManager implements Managable {
    private LogisticsService service;
    private CountryService countryService;
    private StorageManager storageManager;
    private OkHttpClient httpClient;

    @Override
    public void init(Context context) {
        storageManager = App.STORAGE_MANAGER;
        initService();
    }

    private void initService() {
        httpClient = new OkHttpClient.Builder().
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

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.ApiBaseUrl.getBaseUrl(storageManager.getCountry()))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
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
        Observable<Token> observable = service.auth(authRequest);
        observable.
            subscribeOn(Schedulers.newThread()).
            observeOn(AndroidSchedulers.mainThread()).
            subscribe(new Subscriber<Token>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
//                    tokenBaseApiCallback.onError(e.getMessage());
                }

                @Override
                public void onNext(Token token) {
                    storageManager.storeToken(token);
                    initService();
                    tokenBaseApiCallback.onSuccess(token);
                }
            });
    }

    public void getCurrentRider(@NonNull final BaseApiCallback<VehicleDeliveryAreaRiderBundle> riderBundleBaseApiCallback) {
        TokenData tokenData = storageManager.getTokenData();
        if (tokenData != null) {
            service.getRider(tokenData.getUserId()).enqueue(new BaseCallback<VehicleDeliveryAreaRiderBundle>(riderBundleBaseApiCallback) {

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
        service.getRoute(vehicleId).enqueue(new BaseCallback<RouteWrapper>(baseApiCallback) {
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

        service.getRiderSchedule(
            tokenData.getUserId(),
            dateTimeStart,
            dateTimeEnd,
            ApiTag.SORT_VALUE)
            .enqueue(new BaseCallback<ScheduleCollectionWrapper>(baseApiCallback) {
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
        service.clockInSchedule(scheduleId).enqueue(new BaseCallback<ScheduleWrapper>(baseApiCallback) {
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
        service.notifyActionPerformed(routeId, performActionWrapper).enqueue(new RetryActionCallback<>(routeId, performActionWrapper));
    }

    public void sendLocation(
        int vehicleId,
        List<RiderLocation> riderLocation,
        @NonNull final StorableApiCallback<RiderLocationCollectionWrapper> baseApiCallback) {

        RiderLocationCollectionWrapper riderLocationCollectionWrapper = new RiderLocationCollectionWrapper();
        riderLocationCollectionWrapper.addAll(riderLocation);

        service.sendLocation(vehicleId, riderLocationCollectionWrapper).enqueue(new RetryLocationCallback<RiderLocationCollectionWrapper>(
            baseApiCallback,
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

            service.registerDeviceId(
                tokenData.getUserId(),
                new PushNotificationRegistrationWrapper(token))
                .enqueue(new BaseCallback<Rider>(null) {
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

        service.getOrdersReport(tokenData.getUserId(), startAt, endAt, timezone)
            .enqueue(new BaseCallback<OrdersReportCollection>(baseApiCallback) {
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
        countryService.getCountries().enqueue(new BaseCallback<CountryListWrapper>(baseApiCallback) {
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
        Log.e("Calls", String.valueOf(httpClient.dispatcher().runningCallsCount()));
        httpClient.dispatcher().cancelAll();
    }
}
