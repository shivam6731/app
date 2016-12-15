package com.foodpanda.urbanninja.api.rx.action;

import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.functions.Func1;

import static java.util.Arrays.asList;

public class RetryWithDelay implements Func1<Observable<? extends Throwable>, Observable<?>> {

    private static final int MAX_RETRIES_COUNT = 3;
    private static final int RETRY_DELAY_SECONDS = 15;
    private int retryCount;

    @Override
    public Observable<?> call(Observable<? extends Throwable> attempts) {
        return attempts
            .flatMap(new Func1<Throwable, Observable<?>>() {
                @Override
                public Observable<?> call(Throwable throwable) {
                    if (shouldNotRetryOnError(throwable)) {
                        return Observable.error(throwable);
                    }
                    if (++retryCount < MAX_RETRIES_COUNT) {
                        // When this Observable calls onNext, the original
                        // Observable will be retried (i.e. re-subscribed).
                        return Observable.timer(RETRY_DELAY_SECONDS,
                            TimeUnit.SECONDS);
                    } else {
                        storeAction();
                    }

                    // Max retries hit. Just pass the error along.
                    return Observable.error(throwable);
                }
            });
    }

    /**
     * Needs only for API calls that should be stored
     */
    protected void storeAction() {
    }

    /**
     * In case when we receive an error message from server side about any conflict it
     * means only that something is wrong with content that we send not with server or client side
     * so as result this API call should be send again.
     *
     * @param throwable error that we receive from server side
     * @return true if reason of this reason is not internet connection problem or server problem
     */
    private boolean shouldNotRetryOnError(Throwable throwable) {
        return (throwable instanceof HttpException) &&
            asList(
                HttpURLConnection.HTTP_BAD_REQUEST,
                HttpURLConnection.HTTP_FORBIDDEN,
                HttpURLConnection.HTTP_NOT_FOUND,
                HttpURLConnection.HTTP_CONFLICT
            ).contains(
                ((HttpException) throwable)
                    .code());
    }
}
