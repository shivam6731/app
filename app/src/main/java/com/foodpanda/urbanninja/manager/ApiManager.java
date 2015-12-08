package com.foodpanda.urbanninja.manager;

import android.content.Context;

import com.foodpanda.urbanninja.App;
import com.foodpanda.urbanninja.api.ApiUrl;
import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.BaseCallback;
import com.foodpanda.urbanninja.api.model.AuthRequest;
import com.foodpanda.urbanninja.api.request.LogisticsService;
import com.foodpanda.urbanninja.model.Token;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class ApiManager implements Managable {
    private LogisticsService service;
    private StorageManager storageManager;

    @Override
    public void init(Context context) {
        initService();
        storageManager = App.STORAGE_MANAGER;
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
    }

    private Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        return gsonBuilder.create();
    }

    public void login(
        String username,
        String password,
        final BaseApiCallback<Token> tokenBaseApiCallback
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
}
