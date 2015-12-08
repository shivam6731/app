package com.foodpanda.urbanninja.api;

import android.util.Log;

import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public abstract class BaseCallback<T> implements Callback<T> {
    private BaseApiCallback<T> baseApiCallback;

    public BaseCallback(BaseApiCallback<T> errorCallback) {
        this.baseApiCallback = errorCallback;
    }

    @Override
    public void onResponse(Response<T> response, Retrofit retrofit) {
        if (!response.isSuccess()) {
            ErrorMessage errorMessage = new ErrorMessage();
            try {
                errorMessage = new GsonBuilder().create().fromJson(response.errorBody().string(), ErrorMessage.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            baseApiCallback.onError(errorMessage);
        }

    }

    @Override
    public void onFailure(Throwable t) {
        Log.e("onFailure", t.getMessage());
    }
}
