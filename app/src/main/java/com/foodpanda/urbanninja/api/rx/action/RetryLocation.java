package com.foodpanda.urbanninja.api.rx.action;

import com.foodpanda.urbanninja.api.model.RiderLocationCollectionWrapper;
import com.foodpanda.urbanninja.manager.ApiQueue;
import com.foodpanda.urbanninja.model.GeoCoordinate;

import org.joda.time.DateTime;

public class RetryLocation extends RetryWithDelay {
    private int vehicleId;
    private RiderLocationCollectionWrapper locationCollectionWrapper;
    private ApiQueue apiQueue;

    /**
     * Api call with retry logic inside
     * In case this request was failed, rider location
     * would be added to the queue and send as soon as connection would be available again
     *
     * @param vehicleId                 id of rider vehicle that should be saved and sent
     * @param locationCollectionWrapper wrapper with a list of all information about rider status such as
     *                                  {@link DateTime} and {@link GeoCoordinate}
     * @param apiQueue                  api queue where failed object would be stored
     */
    public RetryLocation(
        int vehicleId,
        RiderLocationCollectionWrapper locationCollectionWrapper,
        ApiQueue apiQueue
    ) {
        this.vehicleId = vehicleId;
        this.locationCollectionWrapper = locationCollectionWrapper;
        this.apiQueue = apiQueue;
    }

    @Override
    protected void storeAction() {
        super.storeAction();
        apiQueue.enqueueLocation(locationCollectionWrapper, vehicleId);
    }
}
