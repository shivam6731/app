package com.foodpanda.urbanninja.api.rx.subscriber;

import android.util.Log;

import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.api.utils.ApiUtils;
import com.foodpanda.urbanninja.model.Model;

import rx.Subscriber;

public abstract class BaseSubscriber<T extends Model> extends Subscriber<T> {
    private static final String TAG = BaseSubscriber.class.getSimpleName();
    protected BaseApiCallback<T> baseApiCallback;

    protected BaseSubscriber(BaseApiCallback<T> baseApiCallback) {
        this.baseApiCallback = baseApiCallback;
    }

    @Override
    public void onError(Throwable throwable) {
        if (baseApiCallback != null) {
            ErrorMessage errorMessage = ApiUtils.handleError(throwable);
            baseApiCallback.onError(errorMessage);
        }

    }

    @Override
    public void onCompleted() {
        Log.i(TAG, "onCompleted");
    }
}
