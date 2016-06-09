package com.foodpanda.urbanninja.api.rx.subscriber;

import android.util.Log;

import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.model.Model;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

public abstract class BaseSubscriber<T extends Model> extends Subscriber<T> {
    private static final String TAG = BaseSubscriber.class.getSimpleName();
    protected BaseApiCallback<T> baseApiCallback;

    public BaseSubscriber(BaseApiCallback<T> baseApiCallback) {
        this.baseApiCallback = baseApiCallback;
    }

    public BaseSubscriber() {
    }

    @Override
    public void onError(Throwable throwable) {
        ErrorMessage errorMessage = new ErrorMessage();
        try {
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                errorMessage = new GsonBuilder().create().fromJson(httpException.response().errorBody().string(), ErrorMessage.class);
            } else {
                errorMessage = new ErrorMessage(500, throwable.getMessage());
            }
            Log.e(TAG, throwable.getMessage());
        } catch (IOException | JsonSyntaxException e) {
            Log.e(TAG, e.getMessage());
        }

        if (baseApiCallback != null) {
            baseApiCallback.onError(errorMessage);
        }

    }

    @Override
    public void onCompleted() {
        Log.i(TAG, "onCompleted");
    }
}
