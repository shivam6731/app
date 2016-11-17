package com.foodpanda.urbanninja.manager;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.foodpanda.urbanninja.Config;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.api.ApiTag;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.HockeyAppUrl;
import com.foodpanda.urbanninja.api.model.AuthRequest;
import com.foodpanda.urbanninja.api.model.CashCollectionIssueList;
import com.foodpanda.urbanninja.api.model.CashCollectionIssueWrapper;
import com.foodpanda.urbanninja.api.model.CountryListWrapper;
import com.foodpanda.urbanninja.api.model.HockeyAppVersionList;
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
import com.foodpanda.urbanninja.api.request.HockeyAppService;
import com.foodpanda.urbanninja.api.request.LogisticsService;
import com.foodpanda.urbanninja.api.rx.action.RetryAction;
import com.foodpanda.urbanninja.api.rx.action.RetryLocation;
import com.foodpanda.urbanninja.api.rx.action.RetryWithDelay;
import com.foodpanda.urbanninja.api.rx.subscriber.BackgroundSubscriber;
import com.foodpanda.urbanninja.api.rx.subscriber.BaseSubscriber;
import com.foodpanda.urbanninja.api.serializer.DateTimeDeserializer;
import com.foodpanda.urbanninja.api.utils.ApiUtils;
import com.foodpanda.urbanninja.model.Token;
import com.foodpanda.urbanninja.model.TokenData;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.model.enums.CollectionIssueReason;
import com.foodpanda.urbanninja.model.enums.Status;
import com.foodpanda.urbanninja.utils.DateUtil;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

@Singleton
public class ApiManager {

    private LogisticsService service;
    private CountryService countryService;
    private HockeyAppService hockeyAppService;

    private final StorageManager storageManager;

    private final ApiQueue apiQueue;

    //Will store all requests that are executing right now
    //and after logout will un-subscribe from all of them
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    public ApiManager(StorageManager storageManager, ApiQueue apiQueue) {
        this.storageManager = storageManager;
        this.apiQueue = apiQueue;
        initService();
    }

    /**
     * Create Retrofit service to communicate with our internal API
     * When we launch the app or select country we need to set new base url for the whole UN2 APi service.
     */
    public void createUrbanNinjaService() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.ApiBaseUrl.getBaseUrl(storageManager.getCountry()))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .client(createOkHttpClient())
            .build();

        service = retrofit.create(LogisticsService.class);
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
                storageManager.storeToken(token);
                tokenBaseApiCallback.onSuccess(token);
            }
        };

        compositeSubscription.add(
            ApiUtils.wrapObservable(
                service.auth(authRequest)).
                subscribe(baseSubscriber));

    }

    public void getScheduleList(BaseApiCallback<ScheduleCollectionWrapper> baseApiCallback
    ) {
        DateTime dateTimeNow = DateTime.now();
        DateTime dateTimeEnd = DateTime.now().plusDays(Constants.SCHEDULE_ORDERS_REPORT_LIST_RANGE_DAYS);

        getScheduleList(dateTimeNow, dateTimeEnd, baseApiCallback);
    }

    void scheduleClockIn(
        int scheduleId,
        @NonNull final BaseApiCallback<ScheduleWrapper> baseApiCallback
    ) {
        BaseSubscriber<ScheduleWrapper> baseSubscriber = new BaseSubscriber<ScheduleWrapper>(baseApiCallback) {
            @Override
            public void onNext(ScheduleWrapper scheduleWrapper) {
                baseApiCallback.onSuccess(scheduleWrapper);
            }
        };

        compositeSubscription.add(
            wrapRetryObservable(
                service.clockInSchedule(scheduleId)).
                subscribe(baseSubscriber));
    }

    void notifyActionPerformed(long routeId, Status status) {
        PerformActionWrapper performActionWrapper = new PerformActionWrapper(status, new DateTime(), storageManager.getRiderLocation());

        sendRiderAction(performActionWrapper, routeId);
    }

    public void sendLocation(
        int vehicleId,
        List<RiderLocation> riderLocationList) {

        RiderLocationCollectionWrapper riderLocationCollectionWrapper = new RiderLocationCollectionWrapper();
        riderLocationCollectionWrapper.addAll(riderLocationList);

        compositeSubscription.add(
            wrapRetryObservable(
                service.sendLocation(
                    vehicleId, riderLocationCollectionWrapper),
                new RetryLocation(vehicleId, riderLocationCollectionWrapper, apiQueue)).
                subscribe(new BackgroundSubscriber<>()));
    }

    public void sendAllFailedRequests() {
        resendAction(apiQueue.getRequestsQueue());
        resendLocation(apiQueue.getRequestsLocationQueue());
    }

    /**
     * Call ApiManager to report collection issue
     *
     * @param collectionAmount amount of money collected
     * @param reason           reason of an issue
     */
    void reportCollectionIssue(
        long routeStopId,
        double collectionAmount,
        @NonNull CollectionIssueReason reason,
        @NonNull final BaseApiCallback<CashCollectionIssueList> baseApiCallback) {

        BaseSubscriber<CashCollectionIssueList> baseSubscriber = new BaseSubscriber<CashCollectionIssueList>(baseApiCallback) {
            @Override
            public void onNext(CashCollectionIssueList cashCollectionIssueList) {
                baseApiCallback.onSuccess(cashCollectionIssueList);
            }
        };
        compositeSubscription.add(
            wrapRetryObservable(
                service.reportCollectionIssue(
                    Collections.singletonList(new CashCollectionIssueWrapper(routeStopId, collectionAmount, reason))
                )
            ).subscribe(baseSubscriber)
        );
    }

    public void registerDeviceId(String token) {
        if (!TextUtils.isEmpty(token)) {
            TokenData tokenData = storageManager.getTokenData();
            wrapRetryObservable(
                service.registerDeviceId(tokenData.getUserId(),
                    new PushNotificationRegistrationWrapper(token)))
                .subscribe(new BackgroundSubscriber<>());
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

    //Internal foodpanda API
    public void getCountries(final BaseApiCallback<CountryListWrapper> baseApiCallback) {
        BaseSubscriber<CountryListWrapper> baseSubscriber = new BaseSubscriber<CountryListWrapper>(baseApiCallback) {
            @Override
            public void onNext(CountryListWrapper countryListWrapper) {
                baseApiCallback.onSuccess(countryListWrapper);
            }
        };
        wrapRetryObservable(
            countryService.getCountries()).subscribe(baseSubscriber);
    }

    Observable<ScheduleCollectionWrapper> getCurrentScheduleObservable() {
        DateTime dateTimeNow = DateTime.now();
        DateTime datePlusOneDay = DateTime.now().plusDays(1);
        TokenData tokenData = storageManager.getTokenData();

        return service.getRiderSchedule(tokenData.getUserId(), dateTimeNow, datePlusOneDay, ApiTag.SORT_VALUE);
    }

    Observable<RouteWrapper> getRouteObservable(int vehicleId) {
        return service.getRoute(vehicleId);
    }

    Observable<VehicleDeliveryAreaRiderBundle> getRiderObservable() {
        return service.getRider(storageManager.getTokenData().getUserId());
    }

    Observable<HockeyAppVersionList> getAppVersionsObservable(String apiKey) {
        return hockeyAppService.getAppVersions(apiKey);
    }

    /**
     * logout from rider from api side
     * cancel all API requests that are in flight right now
     */
    public void logout() {
        unregisterDevice();
        compositeSubscription.unsubscribe();
        compositeSubscription = new CompositeSubscription();
    }

    /**
     * in case he logouts we need to delete all data and un-subscribe from push notifications
     */
    private void unregisterDevice() {
        TokenData tokenData = storageManager.getTokenData();
        if (tokenData != null) {
            wrapRetryObservable(service.unregisterDeviceId(tokenData.getUserId()))
                .subscribe(new BackgroundSubscriber<>());
        }
    }

    private void getScheduleList(
        DateTime dateTimeStart,
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

        compositeSubscription.add(
            wrapRetryObservable(
                service.getRiderSchedule(
                    tokenData.getUserId(),
                    dateTimeStart,
                    dateTimeEnd,
                    ApiTag.SORT_VALUE)).
                subscribe(baseSubscriber));
    }

    /**
     * Create service objects for all types of retrofit interfaces
     */
    private void initService() {
        //creating internal UN2 API service
        createUrbanNinjaService();

        //creating foodpanda country API service
        Retrofit retrofit = new Retrofit.Builder().
            baseUrl(Config.ApiUrbanNinjaUrl.BASE_URL).
            addCallAdapterFactory(RxJavaCallAdapterFactory.create()).
            addConverterFactory(GsonConverterFactory.create(createLowerCaseWithUnderscoreGson())).
            build();
        countryService = retrofit.create(CountryService.class);

        //creating hockey app version control API service
        retrofit = new Retrofit.Builder().
            baseUrl(HockeyAppUrl.BASE_URL).
            addCallAdapterFactory(RxJavaCallAdapterFactory.create()).
            addConverterFactory(GsonConverterFactory.create(createLowerCaseWithUnderscoreGson())).
            build();

        hockeyAppService = retrofit.create(HockeyAppService.class);

        sendAllFailedRequests();
    }

    private void getWorkingDayReport(
        DateTime startAt,
        DateTime endAt,
        String timezone,
        @NonNull final BaseApiCallback<OrdersReportCollection> baseApiCallback) {
        TokenData tokenData = storageManager.getTokenData();

        BaseSubscriber<OrdersReportCollection> baseSubscriber = new BaseSubscriber<OrdersReportCollection>(baseApiCallback) {
            @Override
            public void onNext(OrdersReportCollection workingDays) {
                baseApiCallback.onSuccess(workingDays);
            }
        };

        compositeSubscription.add(
            wrapRetryObservable(
                service.getOrdersReport(
                    tokenData.getUserId(),
                    startAt,
                    endAt,
                    timezone))
                .subscribe(baseSubscriber));
    }

    /**
     * Simple wrapping method with just retry logic
     * Observable with retry logic that would be executed
     * if API call was failed
     *
     * @param observable Observable that would be executed in case of fail
     * @param <T>        type of expected result
     * @return Observable with injected retry logic
     */
    private <T> Observable<T> wrapRetryObservable(Observable<T> observable) {
        return wrapRetryObservable(observable, new RetryWithDelay());
    }

    /**
     * Base wrapping method with possible saving
     * Wrap Observable with retry logic that would be executed
     * if API call was failed
     *
     * @param observable     Observable that would be executed in case of fail
     * @param retryWithDelay basic setting for retry logic such as delay count of tries
     * @param <T>            type of expected result
     * @return Observable with injected retry logic
     */
    private <T> Observable<T> wrapRetryObservable(Observable<T> observable, RetryWithDelay retryWithDelay) {
        return ApiUtils.wrapObservable(observable).retryWhen(retryWithDelay);
    }

    /**
     * Build {@link Gson} object with specific serializer to properly parse {@link DateTime} objects
     *
     * @return gson object for {@link LogisticsService} class
     */
    private Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeDeserializer());

        return gsonBuilder.create();
    }

    /**
     * Build {@link Gson} object with specific serializer naming policy,
     * to properly parse lower case objects
     *
     * @return gson object to work with lower case with underscore naming policy
     */
    private Gson createLowerCaseWithUnderscoreGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        return gsonBuilder.create();
    }

    /**
     * Create Api client where timeout and API calls header will be set
     *
     * @return okhttp3 client with all settings
     */
    private OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder().
            connectTimeout(Constants.API_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS).
            writeTimeout(Constants.API_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS).
            readTimeout(Constants.API_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS).
            addInterceptor(chain -> {
                Request.Builder build = chain.request().newBuilder().addHeader("Accept", "application/json");
                Token token = storageManager.getToken();
                if (token != null) {
                    build.addHeader("Authorization", token.getTokenType() + " " + token.getAccessToken()).build();
                }

                return chain.proceed(build.build());
            }).build();
    }

    /**
     * Try to execute all users action api calls
     *
     * @param requestsQueue all stored rider action that we failed during offline time of server problem
     */
    private void resendAction(Queue<StorableStatus> requestsQueue) {
        if (!requestsQueue.isEmpty()) {
            StorableStatus storableStatus = requestsQueue.remove();
            sendRiderAction(storableStatus.getPerformActionWrapper(), storableStatus.getRouteId());
            resendAction(requestsQueue);
        }
        storageManager.storeStatusApiRequests(requestsQueue);
    }

    /**
     * Send rider action to the server.
     * </p>
     * in case if this API call would fail this action would be stored to #apiQueue.
     * And as soon as connection would be back, this action would be send with {@link #sendAllFailedRequests()}
     *
     * @param performActionWrapper rider action that was done (included time, type of action and location)
     * @param routeId              id of route stop
     */
    private void sendRiderAction(PerformActionWrapper performActionWrapper, long routeId) {
        compositeSubscription.add(
            wrapRetryObservable(
                service.notifyActionPerformed(routeId, performActionWrapper),
                new RetryAction(routeId, performActionWrapper, apiQueue)).
                subscribe(new BackgroundSubscriber<>()));
    }

    /**
     * Try to execute all users location api calls
     */
    private void resendLocation(Queue<RiderLocation> requestsLocationQueue) {
        if (!requestsLocationQueue.isEmpty()) {
            RiderLocationCollectionWrapper riderLocations = new RiderLocationCollectionWrapper();
            riderLocations.addAll(requestsLocationQueue);
            sendLocation(storageManager.getVehicleId(), riderLocations);

            requestsLocationQueue.clear();
            storageManager.storeLocationApiRequests(requestsLocationQueue);
        }
    }

}
