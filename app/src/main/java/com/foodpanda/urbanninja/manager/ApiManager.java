package com.foodpanda.urbanninja.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Config;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.api.ApiTag;
import com.foodpanda.urbanninja.api.BaseApiCallback;
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
import com.foodpanda.urbanninja.api.model.StorableStatus;
import com.foodpanda.urbanninja.api.request.CountryService;
import com.foodpanda.urbanninja.api.request.LogisticsService;
import com.foodpanda.urbanninja.api.rx.RetryAction;
import com.foodpanda.urbanninja.api.rx.RetryLocation;
import com.foodpanda.urbanninja.api.rx.RetryWithDelay;
import com.foodpanda.urbanninja.api.serializer.DateTimeDeserializer;
import com.foodpanda.urbanninja.api.subsriber.BaseSubscriber;
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
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
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
            addCallAdapterFactory(RxJavaCallAdapterFactory.create()).
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

        BaseSubscriber<Token> baseSubscriber = new BaseSubscriber<Token>(tokenBaseApiCallback) {
            @Override
            public void onNext(Token token) {
                Log.e("Login", "OnNext");
                storageManager.storeToken(token);
                initService();
                tokenBaseApiCallback.onSuccess(token);
            }
        };
        wrapObservable(service.auth(authRequest)).
            subscribe(baseSubscriber);

    }

    public void getCurrentRider(@NonNull final BaseApiCallback<VehicleDeliveryAreaRiderBundle> baseApiCallback) {
        TokenData tokenData = storageManager.getTokenData();
        if (tokenData != null) {
            BaseSubscriber<VehicleDeliveryAreaRiderBundle> baseSubscriber = new BaseSubscriber<VehicleDeliveryAreaRiderBundle>(baseApiCallback) {
                @Override
                public void onNext(VehicleDeliveryAreaRiderBundle vehicleDeliveryAreaRiderBundle) {
                    Log.e("getCurrentRider", "OnNext");
                    baseApiCallback.onSuccess(vehicleDeliveryAreaRiderBundle);
                }
            };
            wrapRetryObservable(service.getRider(tokenData.getUserId())).
                subscribe(baseSubscriber);
        }
    }

    public void getRoute(
        int vehicleId,
        @NonNull final BaseApiCallback<RouteWrapper> baseApiCallback
    ) {
        BaseSubscriber<RouteWrapper> baseSubscriber = new BaseSubscriber<RouteWrapper>(baseApiCallback) {
            @Override
            public void onNext(RouteWrapper routeWrapper) {
                Log.e("getRoute", "OnNext");
                storageManager.storeStopList(routeWrapper.getStops());
                baseApiCallback.onSuccess(routeWrapper);
            }
        };
        wrapRetryObservable(service.getRoute(vehicleId)).
            subscribe(baseSubscriber);

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
        BaseSubscriber<ScheduleCollectionWrapper> baseSubscriber = new BaseSubscriber<ScheduleCollectionWrapper>(baseApiCallback) {
            @Override
            public void onNext(ScheduleCollectionWrapper scheduleWrappers) {
                baseApiCallback.onSuccess(scheduleWrappers);
            }
        };
        wrapRetryObservable(
            service.getRiderSchedule(
                tokenData.getUserId(),
                dateTimeStart,
                dateTimeEnd,
                ApiTag.SORT_VALUE)).
            subscribe(baseSubscriber);
    }

    public void scheduleClockIn(
        int scheduleId,
        @NonNull final BaseApiCallback<ScheduleWrapper> baseApiCallback
    ) {
        wrapRetryObservable(
            service.clockInSchedule(scheduleId)).
            subscribe(new BaseSubscriber<ScheduleWrapper>(baseApiCallback) {
                @Override
                public void onNext(ScheduleWrapper scheduleWrapper) {
                    baseApiCallback.onSuccess(scheduleWrapper);
                }
            });
    }

    public void notifyActionPerformed(long routeId, Status status) {
        PerformActionWrapper performActionWrapper = new PerformActionWrapper(status, new DateTime());
        wrapRetryObservable(
            service.notifyActionPerformed(routeId, performActionWrapper),
            new RetryAction(routeId, performActionWrapper)
        ).subscribe();
    }

    public void notifyStoredAction(StorableStatus storableStatus) {
        wrapRetryObservable(
            service.notifyActionPerformed(
                storableStatus.getRouteId(),
                storableStatus.getPerformActionWrapper()),
            new RetryAction(storableStatus.getRouteId(), storableStatus.getPerformActionWrapper())).subscribe();
    }

    public void sendLocation(
        int vehicleId,
        final List<RiderLocation> riderLocationList,
        @Nullable final StorableApiCallback<RiderLocationCollectionWrapper> baseApiCallback) {

        RiderLocationCollectionWrapper riderLocationCollectionWrapper = new RiderLocationCollectionWrapper();
        riderLocationCollectionWrapper.addAll(riderLocationList);

        wrapRetryObservable(
            service.sendLocation(vehicleId, riderLocationCollectionWrapper), new RetryLocation(baseApiCallback, vehicleId, riderLocationCollectionWrapper)).
            subscribe(new BaseSubscriber<RiderLocationCollectionWrapper>(baseApiCallback) {
                @Override
                public void onNext(RiderLocationCollectionWrapper riderLocations) {
                    if (baseApiCallback != null) {
                        baseApiCallback.onSuccess(riderLocations);
                    }
                }
            });
    }

    public void sendAllFailedRequests() {
        ApiQueue.getInstance().resendRequests();
    }

    public void registerDeviceId(String token) {
        if (!TextUtils.isEmpty(token)) {
            TokenData tokenData = storageManager.getTokenData();
            wrapRetryObservable(
                service.registerDeviceId(tokenData.getUserId(),
                    new PushNotificationRegistrationWrapper(token)))
                .subscribe();
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

        wrapRetryObservable(
            service.getOrdersReport(
                tokenData.getUserId(),
                startAt,
                endAt,
                timezone))
            .subscribe(new BaseSubscriber<OrdersReportCollection>(baseApiCallback) {
                @Override
                public void onNext(OrdersReportCollection workingDays) {
                    baseApiCallback.onSuccess(workingDays);
                }
            });
    }

    //Internal foodpanda API
    public void getCountries(final BaseApiCallback<CountryListWrapper> baseApiCallback) {
        wrapRetryObservable(
            countryService.getCountries()).subscribe(new BaseSubscriber<CountryListWrapper>(baseApiCallback) {
            @Override
            public void onNext(CountryListWrapper countryListWrapper) {
                baseApiCallback.onSuccess(countryListWrapper);
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
    }

    /**
     * @param observable
     * @param <T>
     * @return
     */
    private <T> Observable<T> wrapObservable(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.newThread()).
            observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param observable
     * @param <T>
     * @return
     */
    private <T> Observable<T> wrapRetryObservable(Observable<T> observable) {
        return wrapRetryObservable(observable, new RetryWithDelay());
    }

    /**
     * @param observable
     * @param retryWithDelay
     * @param <T>
     * @return
     */
    private <T> Observable<T> wrapRetryObservable(Observable<T> observable, RetryWithDelay retryWithDelay) {
        return wrapObservable(observable).retryWhen(retryWithDelay);
    }


}
