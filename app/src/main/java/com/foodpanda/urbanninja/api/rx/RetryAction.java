package com.foodpanda.urbanninja.api.rx;

import com.foodpanda.urbanninja.api.model.PerformActionWrapper;
import com.foodpanda.urbanninja.manager.ApiQueue;

public class RetryAction extends RetryWithDelay {
    private long routeId;
    private PerformActionWrapper performActionWrapper;

    /**
     *
     * @param routeId
     * @param performActionWrapper
     */
    public RetryAction(long routeId, PerformActionWrapper performActionWrapper) {
        this.routeId = routeId;
        this.performActionWrapper = performActionWrapper;
    }

    @Override
    protected void storeAction() {
        super.storeAction();
        ApiQueue.getInstance().enqueueAction(performActionWrapper, routeId);
    }

}
