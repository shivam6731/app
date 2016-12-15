package com.foodpanda.urbanninja.api.rx.subscriber;

import android.util.Log;

import com.foodpanda.urbanninja.api.BaseApiCallback;
import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.model.Model;
import com.foodpanda.urbanninja.ui.activity.BaseActivity;

/**
 * this class just wrap {@link BaseSubscriber} to catch error and send it directly to the activity
 * it allows as not to create separate {@link BaseApiCallback} for each call of the method
 *
 * @param <T> type of action
 */
public class ErrorHandlingSubscriber<T extends Model> extends BaseSubscriber<T> {
    private static final String TAG = ErrorHandlingSubscriber.class.getSimpleName();

    public ErrorHandlingSubscriber(final BaseActivity baseActivity) {
        super(new BaseApiCallback<T>() {
            @Override
            public void onSuccess(T t) {
                Log.i(TAG, "onSuccess");
            }

            @Override
            public void onError(ErrorMessage errorMessage) {
                Log.e(TAG, errorMessage.getStatus() + " " + errorMessage.getMessage());
                if (baseActivity != null) {
                    baseActivity.onError(errorMessage.getStatus(), errorMessage.getMessage());
                }
            }
        });
    }

    @Override
    public void onNext(T t) {
        Log.i(TAG, "onNext");
    }
}
