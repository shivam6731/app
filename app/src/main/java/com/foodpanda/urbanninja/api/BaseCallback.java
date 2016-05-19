package com.foodpanda.urbanninja.api;

import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.model.Model;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class BaseCallback<T extends Model> implements Callback<T> {
    private static final int TOTAL_RETRIES = 3;
    private int retryCount = 0;

    private BaseApiCallback baseApiCallback;


    public BaseCallback(BaseApiCallback callback) {
        this.baseApiCallback = callback;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (!response.isSuccessful() && !call.isCanceled()) {
            ErrorMessage errorMessage = new ErrorMessage();
            try {
                errorMessage = new GsonBuilder().create().fromJson(response.errorBody().string(), ErrorMessage.class);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
            }
            if (baseApiCallback != null) {
                baseApiCallback.onError(errorMessage);
            }

            sendRetry(call);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (!call.isCanceled()) {
            sendRetry(call);
        }
        if (baseApiCallback != null) {
            baseApiCallback.onError(new ErrorMessage(500, t.getMessage()));
        }
    }

    protected boolean sendRetry(Call<T> call) {
        if (retryCount++ < TOTAL_RETRIES) {
            call.clone().enqueue(this);

            return true;
        }

        return false;
    }
}
