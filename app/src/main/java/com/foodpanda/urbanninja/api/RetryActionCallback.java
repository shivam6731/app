package com.foodpanda.urbanninja.api;

import com.foodpanda.urbanninja.api.model.PerformActionWrapper;
import com.foodpanda.urbanninja.manager.ApiQueue;
import com.foodpanda.urbanninja.model.Stop;

import retrofit.Call;

/**
 * Child of {@see BaseCallback} to allows to store rider actions to the {@link ApiQueue}
 * and after reSend this data to the server side as soon as internet would work
 *
 * @param <T> extends list of {@link Stop}
 */
public class RetryActionCallback<T extends Stop> extends BaseCallback<T> {
    private long routeId;
    private PerformActionWrapper performActionWrapper;

    public RetryActionCallback(
        Call<T> call,
        long routeId,
        PerformActionWrapper performActionWrapper
    ) {
        super(null, call);
        this.routeId = routeId;
        this.performActionWrapper = performActionWrapper;
    }

    @Override
    protected boolean sendRetry() {
        boolean isSent = super.sendRetry();
        if (!isSent) {
            ApiQueue.getInstance().enqueueAction(performActionWrapper, routeId);
        }

        return isSent;
    }

}
