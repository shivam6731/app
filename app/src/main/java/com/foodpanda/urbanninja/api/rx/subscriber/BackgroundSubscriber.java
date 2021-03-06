package com.foodpanda.urbanninja.api.rx.subscriber;

import android.util.Log;

import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.model.Model;

/**
 * Needs for that API call that shouldn't chance UI
 * or inform user about some changes
 * For instance it can be push notification subscription
 *
 * @param <T> model that was received from the server
 */
public class BackgroundSubscriber<T extends Model> extends BaseSubscriber<T> {

    public BackgroundSubscriber() {
        super(new BaseApiCallback<T>() {
            @Override
            public void onSuccess(T t) {
                Log.i("onSuccess", t.toString());
            }

            @Override
            public void onError(ErrorMessage errorMessage) {
                Log.e("onError", errorMessage.getMessage());
            }
        });
    }

    @Override
    public void onNext(T t) {
        baseApiCallback.onSuccess(t);
    }
}
