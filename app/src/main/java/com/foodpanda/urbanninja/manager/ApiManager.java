package com.foodpanda.urbanninja.manager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.api.ApiUrbanNinjaUrl;
import com.foodpanda.urbanninja.api.ApiUrl;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.BaseCallback;
import com.foodpanda.urbanninja.api.model.AuthRequest;
import com.foodpanda.urbanninja.api.model.CountryListWrapper;
import com.foodpanda.urbanninja.api.model.RouteListWrapper;
import com.foodpanda.urbanninja.api.model.ScheduleWrapper;
import com.foodpanda.urbanninja.api.request.CountryService;
import com.foodpanda.urbanninja.api.request.LogisticsService;
import com.foodpanda.urbanninja.model.Token;
import com.foodpanda.urbanninja.model.TokenData;
import com.foodpanda.urbanninja.model.VehicleDeliveryAreaRiderBundle;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

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
    }

    private Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

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
        service.auth(authRequest).enqueue(new BaseCallback<Token>(tokenBaseApiCallback) {
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

        service.getRider(tokenData.getUserId()).enqueue(new BaseCallback<VehicleDeliveryAreaRiderBundle>(riderBundleBaseApiCallback) {
            @Override
            public void onResponse(Response<VehicleDeliveryAreaRiderBundle> response, Retrofit retrofit) {
                super.onResponse(response, retrofit);
                if (response.isSuccess()) {
                    riderBundleBaseApiCallback.onSuccess(response.body());
                }
            }
        });
    }

    public void getRoute(int vehicleId,
                         @NonNull final BaseApiCallback<RouteListWrapper> baseApiCallback
    ) {
        service.getRoute(vehicleId).enqueue(new BaseCallback<RouteListWrapper>(baseApiCallback) {
            @Override
            public void onResponse(Response<RouteListWrapper> response, Retrofit retrofit) {
                super.onResponse(response, retrofit);
                if (response.isSuccess()) {
                    baseApiCallback.onSuccess(response.body());
                }
            }
        });

    }

    public void getSchedule(int riderId,
                            @NonNull final BaseApiCallback<List<ScheduleWrapper>> baseApiCallback
    ) {
        service.getRiderSchedule(riderId).enqueue(new BaseCallback<List<ScheduleWrapper>>(baseApiCallback) {
            @Override
            public void onResponse(Response<List<ScheduleWrapper>> response, Retrofit retrofit) {
                super.onResponse(response, retrofit);
                if (response.isSuccess()) {
                    baseApiCallback.onSuccess(response.body());
                }
            }
        });
    }

    public void scheduleClockIn(int scheduleId,
                                @NonNull final BaseApiCallback<ScheduleWrapper> baseApiCallback
    ) {
        service.clockInSchedule(scheduleId).enqueue(new BaseCallback<ScheduleWrapper>(baseApiCallback) {
            @Override
            public void onResponse(Response<ScheduleWrapper> response, Retrofit retrofit) {
                super.onResponse(response, retrofit);
                if (response.isSuccess()) {
                    baseApiCallback.onSuccess(response.body());
                }
            }
        });
    }

    //Internal foodpanda API
    public void getCountries(final BaseApiCallback<CountryListWrapper> baseApiCallback) {
        countryService.getCountries().enqueue(new BaseCallback<CountryListWrapper>(baseApiCallback) {
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
