package com.foodpanda.urbanninja.api.rx.action;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

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
}
