package com.foodpanda.urbanninja.manager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.Constants;
import com.foodpanda.urbanninja.api.ApiTag;
import com.foodpanda.urbanninja.api.ApiUrbanNinjaUrl;
import com.foodpanda.urbanninja.api.ApiUrl;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.BaseCallback;
import com.foodpanda.urbanninja.api.RetryCallback;
import com.foodpanda.urbanninja.api.model.AuthRequest;
import com.foodpanda.urbanninja.api.model.CountryListWrapper;
import com.foodpanda.urbanninja.api.model.PerformActionWrapper;
import com.foodpanda.urbanninja.api.model.RouteWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleCollectionWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.api.request.CountryService;
import com.foodpanda.urbanninja.api.request.LogisticsService;
import com.foodpanda.urbanninja.api.serializer.DateTimeDeserializer;
import com.foodpanda.urbanninja.model.Stop;
import com.foodpanda.urbanninja.model.Token;
import com.foodpanda.urbanninja.model.TokenData;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.foodpanda.urbanninja.model.enums.Action;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.joda.time.DateTime;

import java.io.IOException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class ApiManager implements Managable {
    private LogisticsService service;
    private CountryService countryService;
    private StorageManager storageManager;

    @Override
    public void init(Context context) {
        storageManager = App.STORAGE_MANAGER;
        initService();
    }

    private void initService() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.networkInterceptors().add(
            new Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
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
            }
        );
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiUrl.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .client(httpClient)
            .build();

        service = retrofit.create(LogisticsService.class);

        retrofit = new Retrofit.Builder().
            baseUrl(ApiUrbanNinjaUrl.BASE_URL).
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
            public void onResponse(Response<Token> response, Retrofit retrofit) {
                super.onResponse(response, retrofit);
                if (response.isSuccess()) {
                    storageManager.storeToken(response.body());
                    initService();
                    tokenBaseApiCallback.onSuccess(response.body());
                }
            }
        });
    }

    public void getCurrentRider(@NonNull final BaseApiCallback<VehicleDeliveryAreaRiderBundle> riderBundleBaseApiCallback) {
        TokenData tokenData = storageManager.getTokenData();
        Call<VehicleDeliveryAreaRiderBundle> call = service.getRider(tokenData.getUserId());
        call.enqueue(new BaseCallback<VehicleDeliveryAreaRiderBundle>(riderBundleBaseApiCallback, call) {
            @Override
            public void onResponse(Response<VehicleDeliveryAreaRiderBundle> response, Retrofit retrofit) {
                super.onResponse(response, retrofit);
                if (response.isSuccess()) {
                    riderBundleBaseApiCallback.onSuccess(response.body());
                }
            }
        });
    }

    public void getRoute(
        int vehicleId,
        @NonNull final BaseApiCallback<RouteWrapper> baseApiCallback
    ) {
        Call<RouteWrapper> call = service.getRoute(vehicleId);
        call.enqueue(new BaseCallback<RouteWrapper>(baseApiCallback, call) {
            @Override
            public void onResponse(Response<RouteWrapper> response, Retrofit retrofit) {
                super.onResponse(response, retrofit);
                if (response.isSuccess()) {
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
        DateTime dateTimeEnd = DateTime.now().plusDays(Constants.SCHEDULE_LIST_RANGE);

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
            public void onResponse(Response<ScheduleCollectionWrapper> response, Retrofit retrofit) {
                super.onResponse(response, retrofit);
                if (response.isSuccess()) {
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
            public void onResponse(Response<ScheduleWrapper> response, Retrofit retrofit) {
                super.onResponse(response, retrofit);
                if (response.isSuccess()) {
                    baseApiCallback.onSuccess(response.body());
                }
            }
        });
    }

    public void notifyActionPerformed(
        int routeId,
        Action action
    ) {
        PerformActionWrapper performActionWrapper = new PerformActionWrapper(action, new DateTime());
        Call<Stop> call = service.performedActionNotify(routeId, performActionWrapper);
        call.enqueue(new RetryCallback<>(call, routeId, performActionWrapper));
    }

    public void sendAllFailedRequests() {
        ApiQueue.getInstance().recall(service);
    }

    //Internal foodpanda API
    public void getCountries(final BaseApiCallback<CountryListWrapper> baseApiCallback) {
        Call<CountryListWrapper> call = countryService.getCountries();
        call.enqueue(new BaseCallback<CountryListWrapper>(baseApiCallback, call) {
            @Override
            public void onResponse(Response<CountryListWrapper> response, Retrofit retrofit) {
                super.onResponse(response, retrofit);
                if (response.isSuccess()) {
                    baseApiCallback.onSuccess(response.body());
                }
            }
        });
    }

}
