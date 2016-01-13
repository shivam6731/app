package com.foodpanda.urbanninja.api;

import com.foodpanda.urbanninja.api.model.PerformActionWrapper;
import com.foodpanda.urbanninja.manager.ApiQueue;
import com.foodpanda.urbanninja.model.Stop;

import retrofit.Call;

public class RetryCallback<T extends Stop> extends BaseCallback<T> {
    private int routeId;
    private PerformActionWrapper performActionWrapper;

    public RetryCallback(Call<T> call, int routeId, PerformActionWrapper performActionWrapper) {
        super(null, call);
        this.routeId = routeId;
        this.performActionWrapper = performActionWrapper;
    }

    @Override
    protected boolean sendRetry() {
        boolean isSent = super.sendRetry();
        if (!isSent) {
            ApiQueue.getInstance().enqueue(performActionWrapper, routeId);
        }

        return isSent;
    }

}
