package com.foodpanda.urbanninja.api;

import com.foodpanda.urbanninja.api.model.PerformActionWrapper;
import com.foodpanda.urbanninja.manager.ApiQueue;
import com.foodpanda.urbanninja.model.Stop;

import retrofit2.Call;


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
        long routeId,
        PerformActionWrapper performActionWrapper
    ) {
        super(null);
        this.routeId = routeId;
        this.performActionWrapper = performActionWrapper;
    }

    @Override
    protected boolean sendRetry(Call<T> call) {
        boolean isSent = super.sendRetry(call);
        if (!isSent) {
            ApiQueue.getInstance().enqueueAction(performActionWrapper, routeId);
        }

        return isSent;
    }

}
