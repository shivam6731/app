package com.foodpanda.urbanninja.api.rx;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

public class RetryWithDelay implements Func1<Observable<? extends Throwable>, Observable<?>> {

    private static final int MAX_RETRIES_COUNT = 5;
    private static final int RETRY_DELAY_SECONDS = 1;
    private int retryCount;

    @Override
    public Observable<?> call(Observable<? extends Throwable> attempts) {
        return attempts
            .flatMap(new Func1<Throwable, Observable<?>>() {
                @Override
                public Observable<?> call(Throwable throwable) {
                    if (++retryCount < MAX_RETRIES_COUNT) {
                        // When this Observable calls onNext, the original
                        // Observable will be retried (i.e. re-subscribed).
                        return Observable.timer(RETRY_DELAY_SECONDS,
                            TimeUnit.SECONDS);
                    } else {
                        Log.e("Retry", "Completed");
                    }

                    // Max retries hit. Just pass the error along.
                    return Observable.error(throwable);
                }
            });
    }
}
