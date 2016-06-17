package com.foodpanda.urbanninja.api.utils;

import android.util.Log;

import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.ui.activity.BaseActivity;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Util class to handle with threading and error handling for all API calls
 */
public class ApiUtils {
    private static final String TAG = ApiUtils.class.getSimpleName();

    /**
     * Wrap rx Observable to be executed in background thread
     * and result would come to android main thread
     *
     * @param observable that would be executed
     * @param <T>        type of expected result
     * @return Observable with thread options
     */
    public static <T> Observable<T> wrapObservable(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.newThread()).
            observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Convert all API exception to the standard ErrorMessage object
     * that should be shown to the user
     *
     * @param throwable error from the server side
     * @return error message that should be shown to the user
     */
    public static ErrorMessage handleError(Throwable throwable) {
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

        return errorMessage;
    }

    /**
     * Show error message in the activity
     *
     * @param throwable    error from the server side
     * @param baseActivity activity that will show error message
     */
    public static void showErrorMessage(Throwable throwable, BaseActivity baseActivity) {
        ErrorMessage errorMessage = handleError(throwable);
        baseActivity.onError(errorMessage.getStatus(), errorMessage.getMessage());
    }
}
