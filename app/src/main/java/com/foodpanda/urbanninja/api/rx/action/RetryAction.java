package com.foodpanda.urbanninja.api.rx.action;

import com.foodpanda.urbanninja.api.model.PerformActionWrapper;
import com.foodpanda.urbanninja.manager.ApiQueue;

public class RetryAction extends RetryWithDelay {
    private long routeId;
    private PerformActionWrapper performActionWrapper;
    private ApiQueue apiQueue;

    /**
     * Api call with retry logic inside
     * In case this request was failed rider action
     * would be added to the queue and send as soon as connection would be available again
     *
     * @param routeId              route id of rider action that should be stored
     * @param performActionWrapper route action that that rider did and should be stored
     * @param apiQueue             api queue where failed object would be stored
     */
    public RetryAction(long routeId, PerformActionWrapper performActionWrapper, ApiQueue apiQueue) {
        this.routeId = routeId;
        this.performActionWrapper = performActionWrapper;
        this.apiQueue = apiQueue;
    }

    @Override
    protected void storeAction() {
        super.storeAction();
        apiQueue.enqueueAction(performActionWrapper, routeId);
    }

}
