package com.foodpanda.urbanninja.api.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.foodpanda.urbanninja.api.model.ErrorMessage;
import com.foodpanda.urbanninja.ui.activity.BaseActivity;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.converter.gson.GsonConverterFactory;
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
    public static <T> Observable<T> wrapObservable(@NonNull Observable<T> observable) {
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
    public static ErrorMessage handleError(@NonNull Throwable throwable) {
        ErrorMessage errorMessage = null;
        try {
            if (throwable instanceof HttpException) {
                // We had non-2XX http error
                HttpException httpException = (HttpException) throwable;
                errorMessage = getErrorBodyAs(httpException.response());
            }
            Log.e(TAG, throwable.getMessage());
        } catch (IOException | JsonSyntaxException e) {
            Log.e(TAG, e.getMessage());
        }

        return errorMessage == null ? parseError(throwable) : errorMessage;
    }

    /**
     * Parse internal errors in server issue
     *
     * @param throwable server problem
     * @return parsed error with message from the Throwable object
     */
    private static ErrorMessage parseError(Throwable throwable) {
        return new ErrorMessage(500, TextUtils.isEmpty(throwable.getMessage()) ? "Unknown error" : throwable.getMessage());
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

    /**
     * Method to convert error message json to the real object with details about throwable
     * <p/>
     * https://github.com/square/retrofit/blob/master/retrofit-converters/gson/src/main/java/retrofit2/converter/gson/GsonConverterFactory.java
     * (@decoursin and @kotya341 found that this is just badly designed class and I'm planing to create a PR for them to get rid of unused params,
     * because if use check https://github.com/square/retrofit/blob/master/retrofit/src/main/java/retrofit2/Retrofit.java#L310
     * second class that does the same you would find that it has generic type)
     *
     * @param response API response to parse error message from
     * @return ErrorMessage object of null if something was wrong
     */
    @SuppressWarnings("unchecked")
    @Nullable
    private static ErrorMessage getErrorBodyAs(Response response) throws IOException, JsonSyntaxException {
        if (response == null || response.errorBody() == null) {
            return null;
        }

        Converter<ResponseBody, ErrorMessage> converter =
            (Converter<ResponseBody, ErrorMessage>) GsonConverterFactory.create().responseBodyConverter(ErrorMessage.class, new Annotation[0], null);

        return converter.convert(response.errorBody());
    }
}
