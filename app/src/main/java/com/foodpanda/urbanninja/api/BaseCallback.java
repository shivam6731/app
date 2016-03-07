package com.foodpanda.urbanninja.api;

import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.model.Model;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public abstract class BaseCallback<T extends Model> implements Callback<T> {
    private static final int TOTAL_RETRIES = 3;
    private int retryCount = 0;

    private BaseApiCallback baseApiCallback;

    private Call<T> call;

    public BaseCallback(BaseApiCallback callback, Call<T> call) {
        this.baseApiCallback = callback;
        this.call = call;
    }

    @Override
    public void onResponse(Response<T> response, Retrofit retrofit) {
        if (!response.isSuccess()) {
            ErrorMessage errorMessage = new ErrorMessage();
            try {
                errorMessage = new GsonBuilder().create().fromJson(response.errorBody().string(), ErrorMessage.class);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
            }
            if (baseApiCallback != null) {
                baseApiCallback.onError(errorMessage);
            }

            sendRetry();
        }

    }

    @Override
    public void onFailure(Throwable t) {
        sendRetry();
        if (baseApiCallback != null) {
            baseApiCallback.onError(new ErrorMessage(500, t.getMessage()));
        }
    }

    protected boolean sendRetry() {
        if (retryCount++ < TOTAL_RETRIES) {
            call.clone().enqueue(this);

            return true;
        }

        return false;
    }
}
